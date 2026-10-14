package main.core.model;

/**
 * Kind of a card, described only through traits so that the engine and bots never depend on a
 * concrete expansion's card enum (Req. 16).
 */
public interface CardType {
    String name();

    default boolean isCat() {
        return false;
    }

    default boolean isNope() {
        return false;
    }

    default boolean isNopeable() {
        return true;
    }
}
