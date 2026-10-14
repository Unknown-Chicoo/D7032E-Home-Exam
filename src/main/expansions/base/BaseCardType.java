package main.expansions.base;

import main.core.model.CardType;

/** The card types of the base game, with their traits (rules 2–13). */
public enum BaseCardType implements CardType {
    EXPLODING_KITTEN,
    DEFUSE,
    ATTACK,
    FAVOR,
    NOPE,
    SHUFFLE,
    SKIP,
    SEE_THE_FUTURE,
    TACOCAT,
    HAIRY_POTATO_CAT,
    CATTERMELON,
    BEARD_CAT,
    RAINBOW_RALPHING_CAT;

    @Override
    public boolean isCat() {
        return switch (this) {
            case TACOCAT, HAIRY_POTATO_CAT, CATTERMELON, BEARD_CAT, RAINBOW_RALPHING_CAT -> true;
            default -> false;
        };
    }

    @Override
    public boolean isNope() {
        return this == NOPE;
    }

    @Override
    public boolean isNopeable() {
        return this != EXPLODING_KITTEN && this != DEFUSE;
    }
}
