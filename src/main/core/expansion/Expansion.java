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

    /** Lets an expansion change the kittens other expansions created, before they are inserted (rule 6). */
    default void adjustKittens(List<Card> kittens, int players) {
    }

    void registerEffects(CardRegistry registry);

    default int maxPlayers() {
        return 5;
    }

    /** Setup order: the base game is 0 and add-ons 10, so add-ons can decorate what the base registered. */
    default int order() {
        return 0;
    }
}
