package main.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import main.core.util.Randomizer;

/** The draw pile; position 0 is the top. */
public final class Deck {
    private final List<Card> cards;

    /** Creates an empty deck. */
    public Deck() {
        this.cards = new ArrayList<>();
    }

    /**
     * Removes and returns the top card (rule 10).
     *
     * @return the card at position 0
     * @throws IllegalStateException if the deck is empty
     */
    public Card drawTop() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.remove(0);
    }

    /**
     * Inserts a card, e.g. a defused Exploding Kitten (rule 10).
     *
     * @param position 0 = top, {@link #size()} = bottom
     * @param card the card to insert
     * @throws IndexOutOfBoundsException if {@code position} is outside 0..{@link #size()}
     */
    public void insertAt(int position, Card card) {
        cards.add(position, card);
    }

    /**
     * The top cards without removing them (rule 11e).
     *
     * @param n how many cards to look at; fewer are returned if the deck is smaller
     * @return an unmodifiable copy, top card first
     */
    public List<Card> peek(int n) {
        return List.copyOf(cards.subList(0, Math.min(n, cards.size())));
    }

    /** Shuffles the whole deck with the game's {@link Randomizer} (rules 4, 7, 11c). */
    public void shuffle(Randomizer random) {
        random.shuffle(cards);
    }

    /** Puts the cards at the bottom, in the given order; used only during setup. */
    public void addAll(Collection<Card> newCards) {
        cards.addAll(newCards);
    }

    /** The number of cards left. */
    public int size() {
        return cards.size();
    }

    /** Whether no cards are left. */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
