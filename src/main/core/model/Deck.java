package main.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import main.core.util.Randomizer;

/** The draw pile; index 0 is the top. */
public final class Deck {
    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public Card drawTop() {
        throw new UnsupportedOperationException("TODO: remove and return the top card (rule 10)");
    }

    public void insertAt(int position, Card card) {
        throw new UnsupportedOperationException("TODO: insert a card at a position (Defuse)");
    }

    public List<Card> peek(int n) {
        throw new UnsupportedOperationException("TODO: return the top n cards without removing them");
    }

    public void shuffle(Randomizer random) {
        throw new UnsupportedOperationException("TODO: shuffle the deck");
    }

    public void addAll(Collection<Card> newCards) {
        throw new UnsupportedOperationException("TODO: add cards to the deck");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: number of cards in the deck");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: whether the deck is empty");
    }
}
