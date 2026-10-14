package main.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Cards that have been played or discarded. */
public final class DiscardPile {
    private final List<Card> cards;

    public DiscardPile() {
        this.cards = new ArrayList<>();
    }

    public void add(Card card) {
        throw new UnsupportedOperationException("TODO: discard a card");
    }

    public void addAll(Collection<Card> newCards) {
        throw new UnsupportedOperationException("TODO: discard several cards");
    }

    public Optional<Card> top() {
        throw new UnsupportedOperationException("TODO: most recently discarded card");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: number of discarded cards");
    }
}
