package main.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class TurnStateTest {
    private final Player p1 = new Player("P1");
    private final Player p2 = new Player("P2");
    private final Player p3 = new Player("P3");
    private final TurnState turns = new TurnState(List.of(p1, p2, p3), 0);

    @Test
    void rule8_startsWithOneTurnForTheStartPlayer() {
        TurnState fromSecond = new TurnState(List.of(p1, p2, p3), 1);
        assertSame(p2, fromSecond.current());
        assertEquals(1, fromSecond.turnsLeft());
        assertEquals(0, fromSecond.turnNumber());
    }

    @Test
    void rule9_endTurnMovesToTheNextPlayerAndCountsTheTurn() {
        turns.endTurn();
        assertSame(p2, turns.current());
        assertEquals(1, turns.turnsLeft());
        assertEquals(1, turns.turnNumber());
    }

    @Test
    void rule11a_passToGivesTheNextPlayerTheirTurns() {
        turns.passTo(p2, 2);
        assertSame(p2, turns.current());
        assertEquals(2, turns.turnsLeft());
        assertEquals(1, turns.turnNumber());
    }

    @Test
    void rule11d_endTurnUnderAttackKeepsThePlayer() {
        turns.passTo(p2, 2);
        turns.endTurn();
        assertSame(p2, turns.current());
        assertEquals(1, turns.turnsLeft());
        turns.endTurn();
        assertSame(p3, turns.current());
    }

    @Test
    void rule10_nextAliveSkipsExplodedPlayers() {
        p2.eliminate();
        assertSame(p3, turns.nextAlive(p1));
        turns.endTurn();
        assertSame(p3, turns.current());
    }

    @Test
    void rule10_eliminatedCurrentPlayerForfeitsOwedTurns() {
        turns.passTo(p2, 4);
        p2.eliminate();
        turns.playerEliminated(p2);
        assertSame(p3, turns.current());
        assertEquals(1, turns.turnsLeft());
    }

    @Test
    void rule10_eliminatingSomeoneElseDoesNotChangeTheTurn() {
        p3.eliminate();
        turns.playerEliminated(p3);
        assertSame(p1, turns.current());
        assertEquals(0, turns.turnNumber());
    }

    @Test
    void nextAliveIsThePlayerItselfWhenAlone() {
        p2.eliminate();
        p3.eliminate();
        assertSame(p1, turns.nextAlive(p1));
    }

    @Test
    void nextAliveThrowsWhenNobodyIsAlive() {
        List.of(p1, p2, p3).forEach(Player::eliminate);
        assertThrows(IllegalStateException.class, () -> turns.nextAlive(p1));
    }

    @Test
    void reverseFlipsTheDirection() {
        turns.reverse();
        assertEquals(-1, turns.direction());
        assertSame(p3, turns.nextAlive(p1));
    }
}
