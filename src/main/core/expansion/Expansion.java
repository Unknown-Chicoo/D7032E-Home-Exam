package main.core.expansion;

import java.util.List;

import main.core.rules.CardRegistry;
import main.core.model.Card;

/**
 * A set of cards and their effects that can be added to a game. List the implementing class in
 * {@code META-INF/services/main.core.expansion.Expansion} so {@link ExpansionLoader} finds it.
 * In every method, {@code players} is the number of seats.
 */
public interface Expansion {

    /** The name shown in the start menu; unique among installed expansions. */
    String name();

    /** The cards this expansion adds to the deck before dealing (rule 3); called once during setup. */
    List<Card> createDeckCards(int players);

    /**
     * The cards one player gets before dealing (rule 2); called once per player during setup. The
     * default adds none.
     */
    default List<Card> createStartingHand(int players) {
        return List.of();
    }

    /**
     * The Exploding Kittens (or similar) this expansion inserts after dealing (rule 6); called once
     * during setup. The default adds none.
     */
    default List<Card> createKittens(int players) {
        return List.of();
    }

    /**
     * Changes the kittens all expansions created, before they are inserted (rule 6); called once during
     * setup, after every {@link #createKittens}. The default does nothing.
     *
     * @param kittens the combined, modifiable list; add, remove or replace cards in place
     */
    default void adjustKittens(List<Card> kittens, int players) {
    }

    /**
     * Registers this expansion's play, combo and draw effects; called once during setup, after every
     * expansion with a lower {@link #order()}. A type may be registered only once across all expansions;
     * to change an earlier expansion's draw effect, use {@link CardRegistry#decorateOnDraw}.
     */
    void registerEffects(CardRegistry registry);

    /**
     * The most players this expansion supports; a game allows the highest value among the selected
     * expansions (rule 1). The default is 5.
     */
    default int maxPlayers() {
        return 5;
    }

    /**
     * Setup order, lowest first: the base game is 0 and add-ons 10, so add-ons can decorate what the
     * base registered. The menu always includes the expansions with order 0.
     */
    default int order() {
        return 0;
    }
}
