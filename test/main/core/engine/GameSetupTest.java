package main.core.engine;

import static main.expansions.base.BaseCardType.DEFUSE;
import static main.expansions.base.BaseCardType.EXPLODING_KITTEN;
import static main.testing.Table.count;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import main.core.expansion.DeckFactory;
import main.core.expansion.Expansion;
import main.core.model.Card;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.rules.CardRegistry;
import main.core.util.JavaRandomizer;
import main.expansions.base.BaseGame;

class GameSetupTest {

    private static GameState setUp(int players, long seed) {
        return setUp(List.of(new BaseGame()), players, seed);
    }

    private static GameState setUp(List<Expansion> expansions, int players, long seed) {
        List<String> names = IntStream.rangeClosed(1, players).mapToObj(i -> "P" + i).toList();
        return new GameSetup(new DeckFactory(), new JavaRandomizer(seed)).setUp(expansions, names).state();
    }

    private static List<Card> allCards(GameState state) {
        List<Card> all = new ArrayList<>(state.deck().peek(state.deck().size()));
        state.players().forEach(p -> all.addAll(p.hand().cards()));
        return all;
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 6})
    void rule1_playerCountOutside2To5IsRejected(int players) {
        assertThrows(IllegalArgumentException.class, () -> setUp(players, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void rule1_twoToFivePlayersAreAccepted(int players) {
        assertEquals(players, setUp(players, 1).players().size());
    }

    @Test
    void rule1_highestMaxPlayersAmongExpansionsRaisesTheLimit() {
        Expansion bigTable = new Expansion() {
            @Override
            public String name() {
                return "Big table";
            }

            @Override
            public List<Card> createDeckCards(int players) {
                return List.of();
            }

            @Override
            public void registerEffects(CardRegistry registry) {
            }

            @Override
            public int maxPlayers() {
                return 6;
            }

            @Override
            public int order() {
                return 10;
            }
        };
        assertEquals(6, setUp(List.of(bigTable, new BaseGame()), 6, 1).players().size());
        assertThrows(IllegalArgumentException.class, () -> setUp(List.of(bigTable, new BaseGame()), 7, 1));
    }

    @ParameterizedTest
    @CsvSource({"2, 2", "3, 2", "4, 2", "5, 1"})
    void rule2and3_everyPlayerGetsADefuseAndTheDeckGetsTwoOrOne(int players, int defusesInDeck) {
        GameState state = setUp(players, 7);
        assertEquals(players + defusesInDeck, count(allCards(state), DEFUSE));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void rule5_everyHandHasSevenDealtCardsPlusTheDefuse(int players) {
        GameState state = setUp(players, 7);
        for (Player player : state.players()) {
            assertEquals(8, player.hand().size(), player.name());
            assertTrue(player.hand().containsMatching(DEFUSE::equals), player.name() + " has a Defuse");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void rule6_playersMinusOneKittensAreInTheDeckAndNoneInHands(int players) {
        GameState state = setUp(players, 7);
        assertEquals(players - 1, count(state.deck().peek(state.deck().size()), EXPLODING_KITTEN));
        state.players().forEach(p -> assertTrue(!p.hand().containsMatching(EXPLODING_KITTEN::equals)));
    }

    /** 46 action and cat cards + 2 or 1 Defuse − 7 dealt per player + (players − 1) kittens. */
    @ParameterizedTest
    @CsvSource({"2, 35", "3, 29", "4, 23", "5, 16"})
    void rules3to6_deckSizeAfterSetup(int players, int deckSize) {
        assertEquals(deckSize, setUp(players, 7).deck().size());
    }

    @Test
    void rules4and7_sameSeedGivesTheSameDealAndDeck() {
        GameState a = setUp(4, 99);
        GameState b = setUp(4, 99);
        assertEquals(a.deck().peek(a.deck().size()), b.deck().peek(b.deck().size()));
        for (int i = 0; i < 4; i++) {
            assertEquals(a.players().get(i).hand().cards(), b.players().get(i).hand().cards());
        }
    }

    @Test
    void rule8_startPlayerIsReproducibleForASeed() {
        for (long seed = 0; seed < 20; seed++) {
            GameState a = setUp(4, seed);
            GameState b = setUp(4, seed);
            assertEquals(a.players().indexOf(a.turns().current()), b.players().indexOf(b.turns().current()));
        }
    }

    @Test
    void rule8_startPlayerVariesWithTheSeed() {
        Set<Integer> starts = new HashSet<>();
        for (long seed = 0; seed < 50; seed++) {
            GameState state = setUp(4, seed);
            starts.add(state.players().indexOf(state.turns().current()));
        }
        assertTrue(starts.size() > 1, "start seats seen: " + starts);
    }
}
