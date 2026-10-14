package main.core.expansion;

import java.util.List;

import main.core.cards.CardRegistry;
import main.core.model.Card;

/** A pluggable set of cards and their effects (Plugin, Abstract Factory). */
public interface Expansion {
    String name();

    List<Card> createDeckCards(int players);

    default List<Card> createStartingCards(int players) {
        return List.of();
    }

    default List<Card> createKittens(int players) {
        return List.of();
    }

    void registerEffects(CardRegistry registry);

    default int maxPlayers() {
        return 5;
    }
}
