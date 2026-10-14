package main.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import main.core.util.Randomizer;

/** The draw pile; index 0 is the top. Offers draw, insert, peek and shuffle so effects never touch the list. */
public final class Deck {
    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    /** Removes and returns the top card (rule 10); the deck is never empty while kittens remain. */
    public Card drawTop() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.remove(0);
    }

    /** Inserts at {@code position}, where 0 is the top and {@link #size()} the bottom (rule 10, Defuse). */
    public void insertAt(int position, Card card) {
        cards.add(position, card);
    }

    /** The top {@code n} cards (fewer if the deck is smaller), top first, without removing them. */
    public List<Card> peek(int n) {
        return List.copyOf(cards.subList(0, Math.min(n, cards.size())));
    }

    public void shuffle(Randomizer random) {
        random.shuffle(cards);
    }

    public void addAll(Collection<Card> newCards) {
        cards.addAll(newCards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
