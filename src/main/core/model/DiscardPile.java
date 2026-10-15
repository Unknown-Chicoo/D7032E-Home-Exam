package main.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Cards that have been played or discarded: played cards, used Defuses and Nopes, exploded hands. */
public final class DiscardPile {
    private final List<Card> cards;

    /** Creates an empty pile. */
    public DiscardPile() {
        this.cards = new ArrayList<>();
    }

    /** Puts a card on top. */
    public void add(Card card) {
        cards.add(card);
    }

    /** Puts the cards on top, the last one ending up as {@link #top()}. */
    public void addAll(Collection<Card> newCards) {
        cards.addAll(newCards);
    }

    /**
     * The most recently discarded card.
     *
     * @return empty if nothing has been discarded yet
     */
    public Optional<Card> top() {
        return cards.isEmpty() ? Optional.empty() : Optional.of(cards.get(cards.size() - 1));
    }

    /** Unmodifiable view of the cards, oldest first. */
    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    /** The number of discarded cards. */
    public int size() {
        return cards.size();
    }
}
