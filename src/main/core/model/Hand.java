package main.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import main.core.util.Randomizer;

/** The cards a player currently holds. */
public final class Hand {
    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void add(Card card) {
        throw new UnsupportedOperationException("TODO: add a card to the hand");
    }

    public boolean remove(Card card) {
        throw new UnsupportedOperationException("TODO: remove a specific card from the hand");
    }

    public Optional<Card> removeFirst(CardType type) {
        throw new UnsupportedOperationException("TODO: remove the first card of the given type");
    }

    public boolean contains(CardType type) {
        throw new UnsupportedOperationException("TODO: check whether the hand holds the given type");
    }

    public int count(CardType type) {
        throw new UnsupportedOperationException("TODO: count cards of the given type");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: number of cards in the hand");
    }

    /** Unmodifiable view of the cards. */
    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    public Card removeRandom(Randomizer random) {
        throw new UnsupportedOperationException("TODO: remove and return a random card (two of a kind)");
    }

    public List<Card> clear() {
        throw new UnsupportedOperationException("TODO: remove and return all cards");
    }
}
