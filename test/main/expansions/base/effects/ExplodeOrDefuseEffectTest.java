package main.expansions.base.effects;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.DEFUSE;
import static main.expansions.base.BaseCardType.EXPLODING_KITTEN;
import static main.expansions.base.BaseCardType.FAVOR;
import static main.expansions.base.BaseCardType.SKIP;
import static main.expansions.base.BaseCardType.TACOCAT;
import static main.testing.Table.count;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import main.core.events.CardDrawn;
import main.expansions.base.events.KittenDefused;
import main.expansions.base.events.PlayerExploded;
import main.testing.Table;

class ExplodeOrDefuseEffectTest {
    private final Table t = new Table(3);

    @Test
    void rule10_defuseIsDiscardedAndKittenGoesWhereThePlayerChooses() {
        t.give(1, DEFUSE, TACOCAT).stack(EXPLODING_KITTEN, SKIP, ATTACK, FAVOR);
        t.controller(1).putsKittenAt(2);
        t.draws.draw(t.ctx, t.p(1));
        assertEquals(Table.cards(SKIP, ATTACK, EXPLODING_KITTEN, FAVOR), t.deckTopFirst());
        assertEquals(Table.cards(TACOCAT), t.p(1).hand().cards());
        assertEquals(Table.cards(DEFUSE), t.state.discard().cards());
        assertTrue(t.p(1).isAlive());
        assertEquals(1, t.events.of(KittenDefused.class).size());
        assertTrue(t.events.of(CardDrawn.class).isEmpty(), "the kitten never enters the hand");
        assertSame(t.p(2), t.state.turns().current(), "defusing still ends the turn");
    }

    @Test
    void rule10_kittenCanGoOnTopOrBottomAndOutOfRangeIsClamped() {
        t.give(1, DEFUSE, DEFUSE).stack(EXPLODING_KITTEN, SKIP, ATTACK);
        t.controller(1).putsKittenAt(0).putsKittenAt(99);
        t.draws.draw(t.ctx, t.p(1));
        assertEquals(Table.cards(EXPLODING_KITTEN, SKIP, ATTACK), t.deckTopFirst());
        t.draws.draw(t.ctx, t.p(1));
        assertEquals(Table.cards(SKIP, ATTACK, EXPLODING_KITTEN), t.deckTopFirst());
    }

    @Test
    void rule10_withoutDefuseThePlayerExplodesAndDiscardsHandAndKitten() {
        t.give(1, TACOCAT, ATTACK).stack(EXPLODING_KITTEN, SKIP);
        t.draws.draw(t.ctx, t.p(1));
        assertFalse(t.p(1).isAlive());
        assertEquals(0, t.p(1).hand().size());
        assertEquals(3, t.state.discard().size());
        assertEquals(1, count(t.state.discard().cards(), EXPLODING_KITTEN));
        assertEquals(Table.cards(SKIP), t.deckTopFirst());
        assertEquals(1, t.events.of(PlayerExploded.class).size());
        assertEquals(Optional.empty(), t.events.of(PlayerExploded.class).getFirst().privateTo());
        assertSame(t.p(2), t.state.turns().current());
    }

    @Test
    void rule10_explodingUnderAttackForfeitsTheOwedTurns() {
        t.stack(EXPLODING_KITTEN, SKIP);
        t.state.turns().passTo(t.p(1), 4);
        t.draws.draw(t.ctx, t.p(1));
        assertFalse(t.p(1).isAlive());
        assertSame(t.p(2), t.state.turns().current());
        assertEquals(1, t.state.turns().turnsLeft());
    }
}
