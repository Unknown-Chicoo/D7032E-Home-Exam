package main.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Cards that have been played or discarded. */
public final class DiscardPile {
    private final List<Card> cards;

    public DiscardPile() {
        this.cards = new ArrayList<>();
    }

    public void add(Card card) {
        cards.add(card);
    }

    public void addAll(Collection<Card> newCards) {
        cards.addAll(newCards);
    }

    /** The most recently discarded card. */
    public Optional<Card> top() {
        return cards.isEmpty() ? Optional.empty() : Optional.of(cards.get(cards.size() - 1));
    }

    /** Unmodifiable view of the cards, oldest first. */
    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }
}
