package main.core.engine;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.NOPE;
import static main.expansions.base.BaseCardType.SKIP;
import static main.expansions.base.BaseCardType.TACOCAT;
import static main.testing.Table.count;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import main.core.events.PlayNoped;
import main.core.model.Play;
import main.core.model.Player;
import main.core.players.NopeOffer;
import main.core.rules.InvalidMoveException;
import main.testing.Table;

/** The Nope window (rule 12), driven through {@code PlayResolver} with a 50 ms window. */
class NopeResolverTest {
    private final Table t = new Table(4);

    private boolean attackHappened() {
        return t.state.turns().current() == t.p(2) && t.state.turns().turnsLeft() == 2;
    }

    @Test
    void rule12_playHappensWhenNobodyNopes() throws InvalidMoveException {
        t.give(1, ATTACK).give(2, NOPE).give(3, NOPE);
        t.controller(2).nopes(false);
        t.controller(3).nopes(false);
        t.resolve(t.play(1, ATTACK));
        assertTrue(attackHappened());
        assertTrue(t.events.of(PlayNoped.class).isEmpty());
        assertEquals(1, t.p(2).hand().size());
    }

    @Test
    void rule12_nopeCancelsThePlay() throws InvalidMoveException {
        t.give(1, ATTACK).give(2, NOPE);
        t.controller(2).nopes(true);
        t.resolve(t.play(1, ATTACK));
        assertSame(t.p(1), t.state.turns().current());
        assertEquals(1, t.state.turns().turnsLeft());
        assertEquals(0, t.p(2).hand().size());
        assertEquals(Table.cards(ATTACK, NOPE), t.state.discard().cards());
        PlayNoped noped = t.events.of(PlayNoped.class).getFirst();
        assertSame(t.p(2), noped.by());
        assertTrue(noped.noped());
    }

    @Test
    void rule12_nopeOnANopeIsAYupAndThePlayHappens() throws InvalidMoveException {
        t.give(1, ATTACK, NOPE).give(2, NOPE);
        t.controller(2).nopes(true);
        t.controller(1).nopes(true);
        t.resolve(t.play(1, ATTACK));
        assertTrue(attackHappened());
        List<PlayNoped> nopes = t.events.of(PlayNoped.class);
        assertEquals(2, nopes.size());
        assertSame(t.p(2), nopes.get(0).by());
        assertTrue(nopes.get(0).noped());
        assertSame(t.p(1), nopes.get(1).by());
        assertFalse(nopes.get(1).noped());
        assertEquals(2, count(t.state.discard().cards(), NOPE));
    }

    @Test
    void rule12_whoeverPlayedTheLastCardOrNopeIsNotAsked() throws InvalidMoveException {
        t.give(1, ATTACK, NOPE).give(2, NOPE, NOPE);
        t.controller(2).nopes(true);
        t.controller(1).nopes(false);
        t.resolve(t.play(1, ATTACK));
        assertEquals(1, t.controller(1).nopeOffers.size(), "P1 is asked only after P2's Nope");
        assertEquals(1, t.controller(2).nopeOffers.size(), "P2 is not asked to Nope its own Nope");
        NopeOffer offer = t.controller(1).nopeOffers.getFirst();
        assertSame(t.p(2), offer.actor());
        assertTrue(offer.currentlyNoped());
    }

    @Test
    void rule12_simultaneousNopesAreSettledByTurnOrderNotSpeed() throws InvalidMoveException {
        t.give(1, ATTACK).give(3, NOPE).give(4, NOPE);
        t.controller(3).nopes(true).answersNopeAfter(Duration.ofMillis(15));
        t.controller(4).nopes(true, false);
        t.resolve(t.play(1, ATTACK));
        List<PlayNoped> nopes = t.events.of(PlayNoped.class);
        assertEquals(1, nopes.size());
        assertSame(t.p(3), nopes.getFirst().by(), "P3 sits before P4 after P1, even though P4 answered first");
        assertEquals(1, t.p(4).hand().size(), "P4 keeps its Nope");
        assertSame(t.p(1), t.state.turns().current());
    }

    @Test
    void rule12_anAnswerAfterTheWindowCountsAsNo() throws InvalidMoveException {
        t.give(1, ATTACK).give(2, NOPE);
        t.controller(2).nopes(true).answersNopeAfter(Duration.ofMillis(300));
        t.resolve(t.play(1, ATTACK));
        assertTrue(attackHappened());
        assertEquals(1, t.p(2).hand().size());
    }

    @Test
    void rule12_withNobodyHoldingANopeTheWindowReturnsAtOnce() {
        t.give(2, SKIP);
        NopeResolver slowWindow = new NopeResolver(Duration.ofSeconds(5), Executors.newSingleThreadExecutor());
        Play play = t.play(1, ATTACK);
        long start = System.nanoTime();
        assertFalse(slowWindow.isNoped(t.ctx, play));
        assertTrue(Duration.ofNanos(System.nanoTime() - start).compareTo(Duration.ofSeconds(1)) < 0);
        t.players.forEach(p -> assertTrue(t.controllers.get(p).nopeOffers.isEmpty()));
    }

    @Test
    void rule12_explodedPlayersAreNotAsked() throws InvalidMoveException {
        t.give(1, ATTACK).give(3, NOPE);
        t.p(3).eliminate();
        t.controller(3).nopes(true);
        t.resolve(t.play(1, ATTACK));
        assertTrue(t.controller(3).nopeOffers.isEmpty());
        assertTrue(attackHappened());
    }

    @Test
    void rule12_combosCanBeNoped() throws InvalidMoveException {
        t.give(1, TACOCAT, TACOCAT).give(2, SKIP, NOPE);
        t.controller(1).targets(t.p(2));
        t.controller(2).nopes(true);
        t.resolve(t.play(1, TACOCAT, TACOCAT));
        assertEquals(0, t.p(1).hand().size(), "nothing was stolen");
        assertEquals(Table.cards(SKIP), t.p(2).hand().cards());
        assertEquals(1, t.events.of(PlayNoped.class).size());
    }

    @Test
    void rule12_aPairOfNopesIsAComboAndCanBeNoped() throws InvalidMoveException {
        t.give(1, NOPE, NOPE).give(2, SKIP).give(3, NOPE);
        t.controller(1).targets(t.p(2));
        t.controller(3).nopes(true);
        t.resolve(t.play(1, NOPE, NOPE));
        assertEquals(Table.cards(SKIP), t.p(2).hand().cards());
        assertSame(t.p(3), t.events.of(PlayNoped.class).getFirst().by());
    }

    @Test
    void rule12_aLoneNopeCannotBePlayedOnItsOwn() {
        t.give(1, NOPE);
        InvalidMoveException e = assertThrows(InvalidMoveException.class, () -> t.resolve(t.play(1, NOPE)));
        assertEquals("Nope can only be played in response to another card", e.getMessage());
        assertEquals(Table.cards(NOPE), t.p(1).hand().cards());
        assertEquals(0, t.state.discard().size());
    }

    @Test
    void rule12_offerCarriesTheCompletedPlayAndTheWindow() throws InvalidMoveException {
        t.give(1, TACOCAT, TACOCAT).give(2, SKIP, NOPE);
        t.controller(1).targets(t.p(2));
        t.resolve(t.play(1, TACOCAT, TACOCAT));
        NopeOffer offer = t.controller(2).nopeOffers.getFirst();
        Player target = offer.play().target().orElseThrow();
        assertSame(t.p(2), target);
        assertEquals(Table.NOPE_WINDOW, offer.timeLeft());
        assertFalse(offer.currentlyNoped());
    }
}
