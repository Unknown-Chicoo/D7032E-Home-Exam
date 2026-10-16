package main.expansions.base;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.DEFUSE;
import static main.expansions.base.BaseCardType.EXPLODING_KITTEN;
import static main.expansions.base.BaseCardType.FAVOR;
import static main.expansions.base.BaseCardType.NOPE;
import static main.expansions.base.BaseCardType.SEE_THE_FUTURE;
import static main.expansions.base.BaseCardType.SHUFFLE;
import static main.expansions.base.BaseCardType.SKIP;
import static main.testing.Table.count;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;
import main.core.rules.CardRegistry;
import main.core.rules.InvalidMoveException;
import main.testing.Table;

class BaseGameTest {
    private final BaseGame base = new BaseGame();

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void rule3_deckBeforeDealingHasTheFixedCards(int players) {
        List<Card> deck = base.createDeckCards(players);
        assertEquals(players <= 4 ? 2 : 1, count(deck, DEFUSE));
        assertEquals(4, count(deck, ATTACK));
        assertEquals(4, count(deck, FAVOR));
        assertEquals(5, count(deck, NOPE));
        assertEquals(4, count(deck, SHUFFLE));
        assertEquals(4, count(deck, SKIP));
        assertEquals(5, count(deck, SEE_THE_FUTURE));
        for (BaseCardType type : BaseCardType.values()) {
            if (type.isCat()) {
                assertEquals(4, count(deck, type), type.name());
            }
        }
        assertEquals(0, count(deck, EXPLODING_KITTEN));
    }

    @Test
    void rule3_thereAreExactlyFiveCatTypes() {
        assertEquals(5, List.of(BaseCardType.values()).stream().filter(BaseCardType::isCat).count());
    }

    @Test
    void rule2_startingHandIsOneDefuse() {
        assertEquals(Table.cards(DEFUSE), base.createStartingHand(4));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void rule6_kittensArePlayersMinusOne(int players) {
        List<Card> kittens = base.createKittens(players);
        assertEquals(players - 1, kittens.size());
        assertEquals(players - 1, count(kittens, EXPLODING_KITTEN));
    }

    @Test
    void rule12_nopeHasNoPlayEffectOfItsOwn() {
        CardRegistry registry = new CardRegistry();
        base.registerEffects(registry);
        Play loneNope = new Play(new Player("P1"), Table.cards(NOPE), Optional.empty(), Optional.empty());
        assertThrows(InvalidMoveException.class, () -> registry.effectFor(loneNope));
    }

    @Test
    void rule12_traitsOfNopeDefuseAndKitten() {
        assertEquals(true, NOPE.isNope());
        assertEquals(false, DEFUSE.isNopeable());
        assertEquals(false, EXPLODING_KITTEN.isNopeable());
        assertEquals(true, ATTACK.isNopeable());
    }
}
