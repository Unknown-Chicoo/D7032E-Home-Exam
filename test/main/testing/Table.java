package main.testing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.core.engine.DrawResolver;
import main.core.engine.GameEngine;
import main.core.engine.NopeResolver;
import main.core.engine.PlayResolver;
import main.core.events.EventBus;
import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Deck;
import main.core.model.DiscardPile;
import main.core.model.GameState;
import main.core.model.Play;
import main.core.model.Player;
import main.core.model.TurnState;
import main.core.players.PlayerController;
import main.core.rules.CardRegistry;
import main.core.rules.GameContext;
import main.core.rules.InvalidMoveException;
import main.core.util.JavaRandomizer;
import main.expansions.base.BaseGame;

/**
 * Test fixture: a hand-built base game with players "P1".."Pn", empty hands and an empty deck, P1 to
 * start. Every seat has a {@link ScriptedController}; every event is recorded. The Nope window is 50 ms.
 */
public final class Table {
    public static final Duration NOPE_WINDOW = Duration.ofMillis(50);
    public static final long SEED = 42;

    private static final ExecutorService POOL = Executors.newCachedThreadPool(task -> {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        return thread;
    });

    public final List<Player> players = new ArrayList<>();
    public final Map<Player, ScriptedController> controllers = new LinkedHashMap<>();
    public final RecordingListener events = new RecordingListener();
    public final GameState state;
    public final CardRegistry registry = new CardRegistry();
    public final GameContext ctx;
    public final PlayResolver plays;
    public final DrawResolver draws;

    public Table(int playerCount) {
        for (int i = 1; i <= playerCount; i++) {
            Player player = new Player("P" + i);
            players.add(player);
            controllers.put(player, new ScriptedController(player));
        }
        state = new GameState(players, new Deck(), new DiscardPile(), new TurnState(players, 0));
        new BaseGame().registerEffects(registry);
        EventBus bus = new EventBus();
        bus.subscribe(events);
        ctx = new GameContext(state, Map.<Player, PlayerController>copyOf(controllers), bus, new JavaRandomizer(SEED));
        plays = new PlayResolver(registry, new NopeResolver(NOPE_WINDOW, POOL));
        draws = new DrawResolver(registry);
    }

    public Player p(int seat) {
        return players.get(seat - 1);
    }

    public ScriptedController controller(int seat) {
        return controllers.get(p(seat));
    }

    /** Adds cards to a player's hand. */
    public Table give(int seat, CardType... types) {
        for (CardType type : types) {
            p(seat).hand().add(new Card(type));
        }
        return this;
    }

    /** Puts cards at the bottom of the deck; on an empty deck the first one ends up on top. */
    public Table stack(CardType... types) {
        state.deck().addAll(cards(types));
        return this;
    }

    /** A play by {@code seat} of these cards, as a controller would return it. */
    public Play play(int seat, CardType... types) {
        return new Play(p(seat), cards(types), Optional.empty(), Optional.empty());
    }

    public void resolve(Play play) throws InvalidMoveException {
        plays.resolve(ctx, play);
    }

    public GameEngine engine() {
        return new GameEngine(ctx, plays, draws);
    }

    public List<Card> deckTopFirst() {
        return state.deck().peek(state.deck().size());
    }

    public static List<Card> cards(CardType... types) {
        List<Card> cards = new ArrayList<>();
        for (CardType type : types) {
            cards.add(new Card(type));
        }
        return cards;
    }

    public static long count(Collection<Card> cards, CardType type) {
        return cards.stream().filter(card -> card.type().equals(type)).count();
    }
}
