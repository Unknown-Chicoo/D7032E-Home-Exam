package main.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/** The cards a player currently holds; queried by type traits, e.g. {@code CardType::isNope}. */
public final class Hand {
    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void add(Card card) {
        cards.add(card);
    }

    public boolean remove(Card card) {
        return cards.remove(card);
    }

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

    public boolean containsMatching(Predicate<CardType> type) {
        return cards.stream().anyMatch(card -> type.test(card.type()));
    }

    public int size() {
        return cards.size();
    }

    /** Unmodifiable view of the cards. */
    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    /** Removes and returns all cards (rule 10: an exploded player discards their hand). */
    public List<Card> clear() {
        List<Card> all = List.copyOf(cards);
        cards.clear();
        return all;
    }
}
