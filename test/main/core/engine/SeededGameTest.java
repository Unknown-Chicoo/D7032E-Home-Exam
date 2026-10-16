package main.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import main.core.events.EventBus;
import main.core.events.GameEventListener;
import main.core.events.GameWon;
import main.core.expansion.DeckFactory;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.players.BotController;
import main.core.players.PlayerController;
import main.core.rules.GameContext;
import main.core.util.JavaRandomizer;
import main.expansions.base.BaseGame;
import main.testing.RecordingListener;
import main.testing.Table;

/** Whole bots-only games, wired like {@code Main}'s bots mode but with a 50 ms Nope window. */
class SeededGameTest {
    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    @AfterAll
    static void shutDown() {
        POOL.shutdownNow();
    }

    private record Game(GameState state, RecordingListener events, Player winner) {
    }

    private static GameSetup.Result setUp(int players, long seed) {
        List<String> names = IntStream.rangeClosed(1, players).mapToObj(i -> "Bot " + i).toList();
        return new GameSetup(new DeckFactory(), new JavaRandomizer(seed)).setUp(List.of(new BaseGame()), names);
    }

    /** Plays to the end; each bot gets its own stream, {@code seed + seat + 1}, as in {@code Main}. */
    private static Game play(GameSetup.Result setup, long seed, GameEventListener... extra) {
        Map<Player, PlayerController> controllers = new LinkedHashMap<>();
        List<Player> seats = setup.state().players();
        for (int seat = 0; seat < seats.size(); seat++) {
            controllers.put(seats.get(seat), new BotController(new JavaRandomizer(seed + seat + 1)));
        }
        EventBus bus = new EventBus();
        RecordingListener events = new RecordingListener();
        bus.subscribe(events);
        for (GameEventListener listener : extra) {
            bus.subscribe(listener);
        }
        GameContext ctx = new GameContext(setup.state(), controllers, bus, new JavaRandomizer(seed));
        PlayResolver plays = new PlayResolver(setup.registry(), new NopeResolver(Table.NOPE_WINDOW, POOL));
        Player winner = new GameEngine(ctx, plays, new DrawResolver(setup.registry())).run();
        return new Game(setup.state(), events, winner);
    }

    private static Game play(int players, long seed) {
        return play(setUp(players, seed), seed);
    }

    private static int cardsInPlay(GameState state) {
        return state.deck().size() + state.discard().size()
                + state.players().stream().mapToInt(p -> p.hand().size()).sum();
    }

    /** Total = 46 action and cat cards + Defuses in the deck + one Defuse per player + (players − 1) kittens. */
    @ParameterizedTest
    @CsvSource({"2, 1, 51", "3, 7, 53", "4, 42, 55", "5, 42, 56", "5, 1234, 56"})
    void cardsAreConservedAfterEveryEventOfASeededGame(int players, long seed, int total) {
        GameSetup.Result setup = setUp(players, seed);
        assertEquals(total, cardsInPlay(setup.state()));
        AtomicInteger checked = new AtomicInteger();
        play(setup, seed, event -> {
            assertEquals(total, cardsInPlay(setup.state()), "after: " + event.describe());
            checked.incrementAndGet();
        });
        assertTrue(checked.get() > players, "the probe saw the game");
    }

    @Test
    void rule13_aSeededBotsGameEndsWithOneWinnerAnnouncedLast() {
        Game game = play(4, 42);
        assertEquals(List.of(game.winner()), game.state().alivePlayers());
        GameWon won = assertInstanceOf(GameWon.class, game.events().all().getLast());
        assertEquals(game.winner(), won.winner());
        assertEquals(1, game.events().of(GameWon.class).size());
    }

    @Test
    void sameSeedGivesTheSameEventSequence() {
        assertEquals(play(5, 42).events().transcript(), play(5, 42).events().transcript());
        assertEquals(play(3, 7).events().transcript(), play(3, 7).events().transcript());
    }

    @Test
    void differentSeedsGiveDifferentGames() {
        assertNotEquals(play(4, 1).events().transcript(), play(4, 2).events().transcript());
    }
}
