package main.expansions.base.effects;

import static main.expansions.base.BaseCardType.SKIP;
import static main.expansions.base.BaseCardType.TACOCAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import main.core.model.TurnState;
import main.core.rules.InvalidMoveException;
import main.testing.Table;

class SkipEffectTest {
    private final Table t = new Table(3);
    private final TurnState turns = t.state.turns();

    @Test
    void rule11d_skipEndsTheTurnWithoutDrawing() throws InvalidMoveException {
        t.give(1, SKIP).stack(TACOCAT);
        t.resolve(t.play(1, SKIP));
        assertSame(t.p(2), turns.current());
        assertEquals(1, t.state.deck().size());
        assertEquals(0, t.p(1).hand().size());
    }

    @Test
    void rule11d_skipUnderAttackSkipsOnlyOneOfTheTurns() throws InvalidMoveException {
        t.give(2, SKIP, SKIP);
        turns.passTo(t.p(2), 2);
        t.resolve(t.play(2, SKIP));
        assertSame(t.p(2), turns.current());
        assertEquals(1, turns.turnsLeft());
        t.resolve(t.play(2, SKIP));
        assertSame(t.p(3), turns.current());
    }
}
