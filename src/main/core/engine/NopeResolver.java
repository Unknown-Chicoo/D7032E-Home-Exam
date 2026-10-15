package main.core.engine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import main.core.cards.GameContext;
import main.core.events.GameEvent.PlayNoped;
import main.core.model.CardType;
import main.core.model.Play;
import main.core.model.Player;
import main.core.model.TurnState;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.core.players.PlayerController;

/**
 * Runs the timed Nope window in rounds, asking eligible players in parallel (rule 12, Req. 20).
 * Only the calling game thread mutates state; workers only call {@code wantsToNope} on a snapshot.
 */
public final class NopeResolver {
    private final Duration window;
    private final ExecutorService pool;

    public NopeResolver(Duration window, ExecutorService pool) {
        this.window = window;
        this.pool = pool;
    }

    /** Whether the play ends up Noped; each Nope flips the result and opens a new round. */
    public boolean isNoped(GameContext ctx, Play play) {
        boolean noped = false;
        Player lastActor = play.player();
        while (true) {
            Optional<Player> by = firstToNope(ctx, new NopeOffer(lastActor, play, noped, window));
            if (by.isEmpty()) {
                return noped;
            }
            by.get().hand().removeFirstMatching(CardType::isNope).ifPresent(ctx.state().discard()::add);
            noped = !noped;
            ctx.publish(new PlayNoped(by.get(), play, noped));
            lastActor = by.get();
        }
    }

    /**
     * One round: asks every eligible player in parallel and waits for all answers or the window's end.
     * Of those who said yes, the first in turn order after the actor wins, so the outcome never depends
     * on thread timing (a seeded bots-only game is repeatable). A late or failed answer counts as "no".
     */
    private Optional<Player> firstToNope(GameContext ctx, NopeOffer offer) {
        List<Player> eligible = othersInTurnOrder(ctx.state().turns(), offer.actor()).stream()
                .filter(p -> p.hand().containsMatching(CardType::isNope))
                .toList();
        if (eligible.isEmpty()) {
            return Optional.empty();
        }
        List<Future<Boolean>> asked = new ArrayList<>();
        for (Player player : eligible) {
            GameView view = ctx.viewFor(player);
            PlayerController controller = ctx.controllerOf(player);
            asked.add(pool.submit(() -> controller.wantsToNope(view, offer)));
        }
        long deadline = System.nanoTime() + window.toNanos();
        List<Boolean> answers = new ArrayList<>();
        try {
            for (Future<Boolean> answer : asked) {
                answers.add(answerBy(answer, deadline));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            asked.forEach(future -> future.cancel(true));
        }
        int first = answers.indexOf(true);
        return first < 0 ? Optional.empty() : Optional.of(eligible.get(first));
    }

    /** The other living players, starting with the one after {@code actor} in the direction of play. */
    private static List<Player> othersInTurnOrder(TurnState turns, Player actor) {
        List<Player> order = new ArrayList<>();
        for (Player p = turns.nextAlive(actor); p != actor; p = turns.nextAlive(p)) {
            order.add(p);
        }
        return order;
    }

    private static boolean answerBy(Future<Boolean> answer, long deadline) throws InterruptedException {
        try {
            return answer.get(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
        } catch (ExecutionException | TimeoutException e) {
            return false;
        }
    }
}
