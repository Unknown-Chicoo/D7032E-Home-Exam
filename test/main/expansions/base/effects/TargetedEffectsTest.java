package main.expansions.base.effects;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.DEFUSE;
import static main.expansions.base.BaseCardType.FAVOR;
import static main.expansions.base.BaseCardType.NOPE;
import static main.expansions.base.BaseCardType.SKIP;
import static main.expansions.base.BaseCardType.TACOCAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import main.core.events.CardLost;
import main.core.events.CardPlayed;
import main.core.events.CardReceived;
import main.core.events.CardTransferred;
import main.core.model.Card;
import main.core.model.CardType;
import main.core.rules.InvalidMoveException;
import main.core.util.JavaRandomizer;
import main.testing.Table;

/** Favor and two/three of a kind: the effects that pick a target before the Nope window (rules 11b, 11f, 11g). */
class TargetedEffectsTest {
    private final Table t = new Table(3);

    @Test
    void rule11b_favorTargetGivesACardOfTheirChoice() throws InvalidMoveException {
        t.give(1, FAVOR).give(2, SKIP, ATTACK).give(3, SKIP);
        t.controller(1).targets(t.p(2));
        t.controller(2).gives(ATTACK);
        t.resolve(t.play(1, FAVOR));
        assertEquals(Table.cards(ATTACK), t.p(1).hand().cards());
        assertEquals(Table.cards(SKIP), t.p(2).hand().cards());
        assertEquals(List.of(t.p(2), t.p(3)), t.controller(1).targetCandidates.getFirst());
    }

    @Test
    void rule11b_transferIsPublicButOnlyTheTwoPlayersSeeTheCard() throws InvalidMoveException {
        t.give(1, FAVOR).give(2, ATTACK);
        t.controller(1).targets(t.p(2));
        t.controller(2).gives(ATTACK);
        t.resolve(t.play(1, FAVOR));
        CardTransferred transfer = t.events.of(CardTransferred.class).getFirst();
        assertEquals(Optional.empty(), transfer.privateTo());
        assertTrue(!transfer.describe().contains("ATTACK"));
        assertEquals(Optional.of(t.p(2)), t.events.of(CardLost.class).getFirst().privateTo());
        assertEquals(Optional.of(t.p(1)), t.events.of(CardReceived.class).getFirst().privateTo());
    }

    @Test
    void rule11b_targetIsAnnouncedBeforeTheNopeWindow() throws InvalidMoveException {
        t.give(1, FAVOR).give(2, ATTACK);
        t.controller(1).targets(t.p(2));
        t.controller(2).gives(ATTACK);
        t.resolve(t.play(1, FAVOR));
        assertEquals(Optional.of(t.p(2)), t.events.of(CardPlayed.class).getFirst().play().target());
    }

    @Test
    void rule11b_candidatesExcludeExplodedPlayersAndEmptyHands() throws InvalidMoveException {
        Table four = new Table(4);
        four.give(1, FAVOR).give(2, SKIP).give(4, SKIP);
        four.p(4).eliminate();
        four.controller(1).targets(four.p(2));
        four.controller(2).gives(SKIP);
        four.resolve(four.play(1, FAVOR));
        assertEquals(List.of(four.p(2)), four.controller(1).targetCandidates.getFirst());
    }

    @Test
    void rule11f_twoOfAKindStealsARandomCardChosenByTheSeededRandomizer() throws InvalidMoveException {
        t.give(1, TACOCAT, TACOCAT).give(2, SKIP, ATTACK, DEFUSE);
        List<Card> targetHand = List.copyOf(t.p(2).hand().cards());
        t.controller(1).targets(t.p(2));
        t.resolve(t.play(1, TACOCAT, TACOCAT));
        Card expected = targetHand.get(new JavaRandomizer(Table.SEED).nextInt(targetHand.size()));
        assertEquals(List.of(expected), t.p(1).hand().cards());
        assertEquals(2, t.p(2).hand().size());
        assertEquals(Table.cards(TACOCAT, TACOCAT), t.state.discard().cards());
    }

    @Test
    void rule11f_aPairOfAnyTypeCountsNotOnlyCats() throws InvalidMoveException {
        t.give(1, SKIP, SKIP).give(2, ATTACK);
        t.controller(1).targets(t.p(2));
        t.resolve(t.play(1, SKIP, SKIP));
        assertEquals(Table.cards(ATTACK), t.p(1).hand().cards());
    }

    @Test
    void rule11g_threeOfAKindTakesTheNamedCard() throws InvalidMoveException {
        t.give(1, TACOCAT, TACOCAT, TACOCAT).give(2, SKIP, DEFUSE);
        t.controller(1).targets(t.p(2)).names(DEFUSE);
        t.resolve(t.play(1, TACOCAT, TACOCAT, TACOCAT));
        assertEquals(Table.cards(DEFUSE), t.p(1).hand().cards());
        assertEquals(Table.cards(SKIP), t.p(2).hand().cards());
        assertEquals(Optional.of(DEFUSE), t.events.of(CardPlayed.class).getFirst().play().namedType());
    }

    @Test
    void rule11g_threeOfAKindGetsNothingIfTheTargetLacksTheCard() throws InvalidMoveException {
        t.give(1, TACOCAT, TACOCAT, TACOCAT).give(2, SKIP);
        t.controller(1).targets(t.p(2)).names(NOPE);
        t.resolve(t.play(1, TACOCAT, TACOCAT, TACOCAT));
        assertEquals(0, t.p(1).hand().size());
        assertEquals(Table.cards(SKIP), t.p(2).hand().cards());
        assertEquals(3, t.state.discard().size(), "the cats are spent anyway");
        assertTrue(t.events.of(CardTransferred.class).isEmpty());
    }

    @Test
    void rule11g_namingOptionsAreTheTypesInTheGameSortedByName() throws InvalidMoveException {
        t.give(1, TACOCAT, TACOCAT, TACOCAT).give(2, SKIP).stack(ATTACK);
        t.controller(1).targets(t.p(2)).names(SKIP);
        t.resolve(t.play(1, TACOCAT, TACOCAT, TACOCAT));
        List<CardType> options = t.controller(1).nameOptions.getFirst();
        assertEquals(List.<CardType>of(ATTACK, SKIP, TACOCAT), options);
    }

    @Test
    void rule11bfg_noValidTargetIsInvalidAndTheCardsStayInHand() {
        t.give(1, FAVOR, TACOCAT, TACOCAT);
        InvalidMoveException favor = assertThrows(InvalidMoveException.class, () -> t.resolve(t.play(1, FAVOR)));
        assertEquals("No valid target", favor.getMessage());
        assertThrows(InvalidMoveException.class, () -> t.resolve(t.play(1, TACOCAT, TACOCAT)));
        assertEquals(Table.cards(FAVOR, TACOCAT, TACOCAT), t.p(1).hand().cards());
        assertEquals(0, t.state.discard().size());
        assertTrue(t.events.of(CardPlayed.class).isEmpty());
    }

    @Test
    void rule11b_choosingAPlayerWhoIsNotACandidateIsInvalid() {
        t.give(1, FAVOR).give(2, SKIP);
        t.controller(1).targets(t.p(3));
        assertThrows(InvalidMoveException.class, () -> t.resolve(t.play(1, FAVOR)));
        assertEquals(Table.cards(FAVOR), t.p(1).hand().cards());
        assertEquals(0, t.state.discard().size());
    }

    @Test
    void rule11_playingCardsYouDoNotHoldIsInvalid() {
        t.give(1, TACOCAT).give(2, SKIP);
        assertThrows(InvalidMoveException.class, () -> t.resolve(t.play(1, TACOCAT, TACOCAT)));
        assertEquals(Table.cards(TACOCAT), t.p(1).hand().cards());
    }
}
