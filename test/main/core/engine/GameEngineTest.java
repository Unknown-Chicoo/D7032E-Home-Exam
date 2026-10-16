package main.core.engine;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.EXPLODING_KITTEN;
import static main.expansions.base.BaseCardType.NOPE;
import static main.expansions.base.BaseCardType.SEE_THE_FUTURE;
import static main.expansions.base.BaseCardType.SKIP;
import static main.expansions.base.BaseCardType.TACOCAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import main.core.events.CardPlayed;
import main.core.events.GameEvent;
import main.core.events.GameWon;
import main.core.events.MoveRejected;
import main.core.events.PlayerDrew;
import main.core.events.TurnStarted;
import main.core.model.Player;
import main.testing.Table;

class GameEngineTest {

    @Test
    void rule9_aPlayerMayPlaySeveralCardsBeforeDrawing() {
        Table t = new Table(2);
        t.give(1, SEE_THE_FUTURE, SEE_THE_FUTURE).stack(TACOCAT, EXPLODING_KITTEN);
        t.controller(1).plays(SEE_THE_FUTURE).plays(SEE_THE_FUTURE).passes();
        t.engine().run();
        List<TurnStarted> turns = t.events.of(TurnStarted.class);
        assertEquals(List.of(t.p(1), t.p(2)), turns.stream().map(TurnStarted::player).toList());
        assertEquals(2, t.events.of(CardPlayed.class).size(), "both cards were played in P1's one turn");
        assertEquals(Table.cards(TACOCAT), t.p(1).hand().cards(), "the turn ended with one draw");
    }

    @Test
    void rule9_rejectedPlayIsReportedPrivatelyAndThePlayerChoosesAgain() {
        Table t = new Table(2);
        t.give(1, TACOCAT).stack(SKIP, EXPLODING_KITTEN);
        t.controller(1).plays(TACOCAT).passes();
        t.engine().run();
        MoveRejected rejected = t.events.of(MoveRejected.class).getFirst();
        assertEquals(Optional.of(t.p(1)), rejected.privateTo());
        assertTrue(t.p(1).hand().cards().containsAll(Table.cards(TACOCAT, SKIP)));
    }

    @Test
    void rule12_loneNopeOnYourTurnIsRejected() {
        Table t = new Table(2);
        t.give(1, NOPE).stack(SKIP, EXPLODING_KITTEN);
        t.controller(1).plays(NOPE);
        t.engine().run();
        assertTrue(t.events.of(MoveRejected.class).getFirst().describe().contains("Nope"));
    }

    @Test
    void rule10_explodedPlayerTakesNoMoreTurns() {
        Table t = new Table(3);
        t.stack(EXPLODING_KITTEN, SKIP, SKIP, SKIP, EXPLODING_KITTEN);
        Player winner = t.engine().run();
        // P1 explodes; P2, P3, P2 draw; P3 explodes on the second kitten.
        assertSame(t.p(2), winner);
        List<Player> turnsAfterExplosion = t.events.of(TurnStarted.class).stream().skip(1).map(TurnStarted::player).toList();
        assertTrue(!turnsAfterExplosion.contains(t.p(1)), "P1 had turns after exploding: " + turnsAfterExplosion);
    }

    @Test
    void rule10_playerWhoExplodesUnderAttackTakesNoMoreTurns() {
        Table t = new Table(3);
        t.give(1, ATTACK).stack(EXPLODING_KITTEN, SKIP, EXPLODING_KITTEN);
        t.controller(1).plays(ATTACK);
        Player winner = t.engine().run();
        // P1 attacks, P2 explodes on its first of two turns, P3 draws, P1 explodes.
        assertSame(t.p(3), winner);
        List<Player> order = t.events.of(TurnStarted.class).stream().map(TurnStarted::player).toList();
        assertEquals(List.of(t.p(1), t.p(2), t.p(3), t.p(1)), order);
    }

    @Test
    void rule13_lastPlayerAliveWinsAndEveryoneIsTold() {
        Table t = new Table(2);
        t.stack(EXPLODING_KITTEN);
        Player winner = t.engine().run();
        assertSame(t.p(2), winner);
        List<GameEvent> all = t.events.all();
        GameWon won = assertInstanceOf(GameWon.class, all.getLast());
        assertSame(t.p(2), won.winner());
        assertEquals(Optional.empty(), won.privateTo());
        assertEquals(1, t.events.of(GameWon.class).size());
        assertTrue(t.events.of(PlayerDrew.class).isEmpty());
    }
}
