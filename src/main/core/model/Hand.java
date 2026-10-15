package main.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The cards a player holds. Query by type with predicates, e.g. {@code CardType::isNope} or
 * {@code DEFUSE::equals}.
 */
public final class Hand {
    private final List<Card> cards;

    /** Creates an empty hand. */
    public Hand() {
        this.cards = new ArrayList<>();
    }

    /** Adds a card at the end. */
    public void add(Card card) {
        cards.add(card);
    }

    /**
     * Removes one card equal to {@code card}, i.e. one card of the same type.
     *
     * @return false if the hand held no such card
     */
    public boolean remove(Card card) {
        return cards.remove(card);
    }

    /**
     * Removes the first card whose type matches.
     *
     * @param type the test on the card's type
     * @return the removed card, or empty if none matched (the hand is then unchanged)
     */
    public Optional<Card> removeFirstMatching(Predicate<CardType> type) {
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card card = it.next();
            if (type.test(card.type())) {
                it.remove();
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }

    /** Whether any card's type matches the test. */
    public boolean containsMatching(Predicate<CardType> type) {
        return cards.stream().anyMatch(card -> type.test(card.type()));
    }

    /** The number of cards held. */
    public int size() {
        return cards.size();
    }

    /** Unmodifiable live view of the cards, in the order they were added. */
    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    /**
     * Removes all cards (rule 10: an exploded player discards their hand).
     *
     * @return the removed cards, as an unmodifiable copy
     */
    public List<Card> clear() {
        List<Card> all = List.copyOf(cards);
        cards.clear();
        return all;
    }
}
