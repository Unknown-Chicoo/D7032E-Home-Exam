package main.expansions.base.effects;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.SKIP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import main.core.model.TurnState;
import main.core.rules.InvalidMoveException;
import main.testing.Table;

class AttackEffectTest {
    private final Table t = new Table(3);
    private final TurnState turns = t.state.turns();

    @Test
    void rule11a_attackGivesNextPlayerTwoTurns() throws InvalidMoveException {
        t.give(1, ATTACK).stack(SKIP);
        t.resolve(t.play(1, ATTACK));
        assertSame(t.p(2), turns.current());
        assertEquals(2, turns.turnsLeft());
        assertEquals(1, t.state.deck().size(), "the attacker does not draw");
        assertEquals(0, t.p(1).hand().size());
    }

    @Test
    void rule11a_attackedPlayerWhoAttacksPassesOnFourThenSix() throws InvalidMoveException {
        t.give(1, ATTACK).give(2, ATTACK).give(3, ATTACK);
        t.resolve(t.play(1, ATTACK));
        assertEquals(2, turns.turnsLeft());
        t.resolve(t.play(2, ATTACK));
        assertSame(t.p(3), turns.current());
        assertEquals(4, turns.turnsLeft());
        t.resolve(t.play(3, ATTACK));
        assertSame(t.p(1), turns.current());
        assertEquals(6, turns.turnsLeft());
    }

    /** Decision 2: stacking applies only while more than one turn is owed. */
    @Test
    void rule11a_attackOnTheLastOwedTurnGivesTwo() throws InvalidMoveException {
        t.give(2, ATTACK);
        turns.passTo(t.p(2), 2);
        turns.endTurn();
        t.resolve(t.play(2, ATTACK));
        assertSame(t.p(3), turns.current());
        assertEquals(2, turns.turnsLeft());
    }

    @Test
    void rule11a_attackSkipsExplodedPlayers() throws InvalidMoveException {
        t.give(1, ATTACK);
        t.p(2).eliminate();
        t.resolve(t.play(1, ATTACK));
        assertSame(t.p(3), turns.current());
    }
}
