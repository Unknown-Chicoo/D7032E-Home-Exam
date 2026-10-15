package main.network;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import main.core.events.GameEvent;
import main.core.events.GameEventListener;
import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Player;
import main.core.model.TurnAction;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.core.players.PlayerController;
import main.core.players.Question;
import main.io.ActionMenu;
import main.io.TextRenderer;

/**
 * Plays a seat for a human on another machine, sending decisions and events as messages over a
 * {@link MessageChannel} (Req. 17). On an I/O failure it announces {@link PlayerDisconnected} once and
 * lets its fallback make every later decision.
 */
public final class RemoteController implements PlayerController, GameEventListener {
    private final MessageChannel channel;
    private final Player player;
    private final TextRenderer renderer;
    private final PlayerController fallback;
    private final GameEventListener announcer;
    private final AtomicInteger nextId = new AtomicInteger();
    private volatile boolean disconnected;
    private boolean announced;

    /**
     * Creates the controller; also subscribe it to the event bus so it forwards events.
     *
     * @param player the seat it plays; only events visible to this player are sent
     * @param fallback takes over every decision once the connection is lost, normally a {@code BotController}
     * @param announcer receives the disconnect event, once; {@code Main} passes {@code bus::publish}
     */
    public RemoteController(MessageChannel channel, Player player, TextRenderer renderer, PlayerController fallback,
            GameEventListener announcer) {
        this.channel = channel;
        this.player = player;
        this.renderer = renderer;
        this.fallback = fallback;
        this.announcer = announcer;
    }

    /** A remote call that may fail with an I/O error. */
    @FunctionalInterface
    private interface RemoteCall<T> {
        T call() throws IOException;
    }

    @Override
    public TurnAction chooseAction(GameView view) {
        ActionMenu menu = ActionMenu.of(view);
        return remoteOr(() -> menu.actions().get(ask(renderer.render(view) + "\nYour move:", menu.labels())),
                () -> fallback.chooseAction(view));
    }

    @Override
    public Player chooseTarget(GameView view, List<Player> candidates) {
        return remoteOr(() -> candidates.get(ask("Choose a target:", candidates.stream().map(Player::name).toList())),
                () -> fallback.chooseTarget(view, candidates));
    }

    @Override
    public Card chooseCardToGive(GameView view, Player requester) {
        List<String> hand = view.hand().stream().map(renderer::name).toList();
        return remoteOr(() -> view.hand().get(ask(renderer.favorPrompt(requester), hand)),
                () -> fallback.chooseCardToGive(view, requester));
    }

    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        return remoteOr(() -> options.get(ask("Name the card type you want:", options.stream().map(CardType::name).toList())),
                () -> fallback.chooseCardTypeToName(view, options));
    }

    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        List<String> positions = IntStream.rangeClosed(0, deckSize)
                .mapToObj(i -> i == 0 ? "Top" : i == deckSize ? "Bottom" : i + " cards down")
                .toList();
        return remoteOr(() -> ask(renderer.deckPositionPrompt(deckSize), positions),
                () -> fallback.chooseDeckPosition(view, deckSize));
    }

    @Override
    public int choose(GameView view, Question question) {
        return remoteOr(() -> ask(question.prompt(), question.options()), () -> fallback.choose(view, question));
    }

    /**
     * Runs on a worker thread, so it only marks a failure; the game thread announces it later.
     * Answers to an older question are discarded by id (rule 12, Req. 20).
     */
    @Override
    public boolean wantsToNope(GameView view, NopeOffer offer) {
        if (!disconnected) {
            try {
                return askWithin(renderer.nopePrompt(offer), TextRenderer.NOPE_OPTIONS, offer.timeLeft())
                        .filter(choice -> choice == 1)
                        .isPresent();
            } catch (IOException e) {
                disconnected = true;
            }
        }
        return fallback.wantsToNope(view, offer);
    }

    /** Sends the event's text if this player may see it; does nothing once disconnected. */
    @Override
    public void onEvent(GameEvent event) {
        announceIfDisconnected();
        if (!disconnected) {
            Optional<String> text = renderer.render(event, Optional.of(player));
            if (text.isPresent()) {
                remoteOr(() -> {
                    channel.send(Message.event(text.get()));
                    return null;
                }, () -> null);
            }
        }
    }

    /**
     * Call once after the game ends (rule 13): sends GAME_OVER with {@code farewell} and closes the
     * connection.
     */
    public void gameOver(String farewell) {
        try (channel) {
            if (!disconnected) {
                channel.send(Message.gameOver(farewell));
            }
        } catch (IOException e) {
            disconnected = true;
        }
    }

    /** Game thread only: the remote answer, or the fallback's once the connection is lost. */
    private <T> T remoteOr(RemoteCall<T> remote, Supplier<T> local) {
        announceIfDisconnected();
        if (!disconnected) {
            try {
                return remote.call();
            } catch (IOException e) {
                disconnected = true;
                announceIfDisconnected();
            }
        }
        return local.get();
    }

    private void announceIfDisconnected() {
        if (disconnected && !announced) {
            announced = true;
            announcer.onEvent(new PlayerDisconnected(player));
        }
    }

    private int ask(String text, List<String> options) throws IOException {
        int id = nextId.incrementAndGet();
        channel.send(Message.ask(id, text, options));
        while (true) {
            Message reply = channel.receive();
            if (isAnswer(reply, id, options)) {
                return reply.answerIndex();
            }
        }
    }

    private Optional<Integer> askWithin(String text, List<String> options, Duration limit) throws IOException {
        int id = nextId.incrementAndGet();
        long deadline = System.nanoTime() + limit.toNanos();
        channel.send(Message.askTimed(id, text, options, (int) Math.max(1, limit.toSeconds())));
        while (true) {
            Duration left = Duration.ofNanos(deadline - System.nanoTime());
            if (left.isNegative() || left.isZero()) {
                return Optional.empty();
            }
            Optional<Message> reply = channel.receive(left);
            if (reply.isEmpty()) {
                return Optional.empty();
            }
            if (isAnswer(reply.get(), id, options)) {
                return Optional.of(reply.get().answerIndex());
            }
        }
    }

    /** Only an in-range ANSWER to the open question counts; stale ones are dropped. */
    private static boolean isAnswer(Message reply, int id, List<String> options) {
        return reply.type() == MessageType.ANSWER && reply.id() == id
                && reply.answerIndex() >= 0 && reply.answerIndex() < options.size();
    }
}
