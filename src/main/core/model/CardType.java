package main.core.model;

/**
 * Kind of a card; expansions implement it, usually as an enum (Req. 16). Override the traits that
 * differ from the defaults, which describe a plain, Nopeable action card.
 */
public interface CardType {

    /** The name shown to players, e.g. {@code ATTACK}; unique among the types in one game. */
    String name();

    /** Whether this is a cat card, with no effect of its own. */
    default boolean isCat() {
        return false;
    }

    /** Whether this card can cancel a play during the Nope window (rule 12). */
    default boolean isNope() {
        return false;
    }

    /** Whether playing this card on its own opens a Nope window (rule 12); false for e.g. Defuse. */
    default boolean isNopeable() {
        return true;
    }
}
