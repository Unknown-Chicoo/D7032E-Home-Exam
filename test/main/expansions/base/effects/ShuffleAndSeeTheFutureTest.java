package main.expansions.base.effects;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.CATTERMELON;
import static main.expansions.base.BaseCardType.DEFUSE;
import static main.expansions.base.BaseCardType.EXPLODING_KITTEN;
import static main.expansions.base.BaseCardType.FAVOR;
import static main.expansions.base.BaseCardType.SEE_THE_FUTURE;
import static main.expansions.base.BaseCardType.SHUFFLE;
import static main.expansions.base.BaseCardType.SKIP;
import static main.expansions.base.BaseCardType.TACOCAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import main.core.model.Card;
import main.core.rules.InvalidMoveException;
import main.expansions.base.events.DeckShuffled;
import main.expansions.base.events.FutureSeen;
import main.testing.Table;

class ShuffleAndSeeTheFutureTest {
    private final Table t = new Table(3);

    private static List<Card> sorted(List<Card> cards) {
        return cards.stream().sorted(Comparator.comparing(card -> card.type().name())).toList();
    }

    @Test
    void rule11c_shuffleReordersTheSameCardsAndIsAnnounced() throws InvalidMoveException {
        t.give(1, SHUFFLE).stack(ATTACK, CATTERMELON, DEFUSE, EXPLODING_KITTEN, FAVOR, SKIP, TACOCAT);
        List<Card> before = t.deckTopFirst();
        t.resolve(t.play(1, SHUFFLE));
        assertNotEquals(before, t.deckTopFirst(), "seed 42 changes the order");
        assertEquals(sorted(before), sorted(t.deckTopFirst()));
        assertEquals(1, t.events.of(DeckShuffled.class).size());
        assertSame(t.p(1), t.state.turns().current(), "shuffling does not end the turn");
    }

    @Test
    void rule11e_seeTheFutureShowsTheTopThreeOnlyToThePlayer() throws InvalidMoveException {
        t.give(1, SEE_THE_FUTURE).stack(EXPLODING_KITTEN, SKIP, ATTACK, TACOCAT);
        List<Card> before = t.deckTopFirst();
        t.resolve(t.play(1, SEE_THE_FUTURE));
        FutureSeen seen = t.events.of(FutureSeen.class).getFirst();
        assertEquals(Table.cards(EXPLODING_KITTEN, SKIP, ATTACK), seen.top());
        assertEquals(Optional.of(t.p(1)), seen.privateTo());
        assertEquals(before, t.deckTopFirst(), "the deck is unchanged");
        assertSame(t.p(1), t.state.turns().current());
    }

    @Test
    void rule11e_seeTheFutureShowsFewerCardsWhenTheDeckIsSmall() throws InvalidMoveException {
        t.give(1, SEE_THE_FUTURE).stack(SKIP, EXPLODING_KITTEN);
        t.resolve(t.play(1, SEE_THE_FUTURE));
        assertEquals(Table.cards(SKIP, EXPLODING_KITTEN), t.events.of(FutureSeen.class).getFirst().top());
    }
}
