package main.core.engine;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.SKIP;
import static main.expansions.base.BaseCardType.TACOCAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import main.core.events.CardDrawn;
import main.core.events.PlayerDrew;
import main.core.model.Card;
import main.testing.Table;

class DrawResolverTest {
    private final Table t = new Table(3);

    @Test
    void rule9_passDrawsTheTopCardIntoTheHandAndEndsTheTurn() {
        t.stack(SKIP, ATTACK);
        t.draws.draw(t.ctx, t.p(1));
        assertEquals(List.of(new Card(SKIP)), t.p(1).hand().cards());
        assertEquals(Table.cards(ATTACK), t.deckTopFirst());
        assertSame(t.p(2), t.state.turns().current());
        assertEquals(1, t.state.turns().turnNumber());
    }

    @Test
    void rule9_tableIsToldSomeoneDrewButOnlyTheDrawerSeesTheCard() {
        t.stack(TACOCAT);
        t.draws.draw(t.ctx, t.p(1));
        assertEquals(1, t.events.of(PlayerDrew.class).size());
        assertEquals(Optional.empty(), t.events.of(PlayerDrew.class).getFirst().privateTo());
        CardDrawn drawn = t.events.of(CardDrawn.class).getFirst();
        assertEquals(Optional.of(t.p(1)), drawn.privateTo());
        assertTrue(drawn.describe().contains("TACOCAT"));
        assertTrue(!t.events.of(PlayerDrew.class).getFirst().describe().contains("TACOCAT"));
    }

    @Test
    void rule11a_drawUnderAttackEndsOnlyOneTurn() {
        t.stack(TACOCAT, TACOCAT);
        t.state.turns().passTo(t.p(1), 2);
        t.draws.draw(t.ctx, t.p(1));
        assertSame(t.p(1), t.state.turns().current());
        assertEquals(1, t.state.turns().turnsLeft());
    }

    @Test
    void drawingFromAnEmptyDeckFails() {
        assertThrows(IllegalStateException.class, () -> t.draws.draw(t.ctx, t.p(1)));
    }
}
