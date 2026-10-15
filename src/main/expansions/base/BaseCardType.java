package main.expansions.base;

import main.core.model.CardType;

/** The card types of the base game, with their traits (rules 2–13). */
public enum BaseCardType implements CardType {
    /** Explodes the player who draws it unless they Defuse (rule 10). */
    EXPLODING_KITTEN,
    /** Saves a player from an Exploding Kitten; never played on its own (rules 2, 10). */
    DEFUSE,
    /** Ends the turn without drawing and gives the next player 2 turns (rule 11a). */
    ATTACK,
    /** The target gives the player a card of their choice (rule 11b). */
    FAVOR,
    /** Cancels a play, or a Nope, during the Nope window; never played on its own (rule 12). */
    NOPE,
    /** Shuffles the deck (rule 11c). */
    SHUFFLE,
    /** Ends one turn without drawing (rule 11d). */
    SKIP,
    /** Privately shows the top 3 cards (rule 11e). */
    SEE_THE_FUTURE,
    /** Cat card: no effect alone, played as two or three of a kind (rules 11f, 11g). */
    TACOCAT,
    /** Cat card, like {@link #TACOCAT}. */
    HAIRY_POTATO_CAT,
    /** Cat card, like {@link #TACOCAT}. */
    CATTERMELON,
    /** Cat card, like {@link #TACOCAT}. */
    OVERWEIGHT_BIKINI_CAT,
    /** Cat card, like {@link #TACOCAT}. */
    RAINBOW_RALPHING_CAT;

    /** True for the five cat cards. */
    @Override
    public boolean isCat() {
        return switch (this) {
            case TACOCAT, HAIRY_POTATO_CAT, CATTERMELON, OVERWEIGHT_BIKINI_CAT, RAINBOW_RALPHING_CAT -> true;
            default -> false;
        };
    }

    /** True only for {@link #NOPE}. */
    @Override
    public boolean isNope() {
        return this == NOPE;
    }

    /** False for {@link #EXPLODING_KITTEN} and {@link #DEFUSE}, which are never played as actions. */
    @Override
    public boolean isNopeable() {
        return this != EXPLODING_KITTEN && this != DEFUSE;
    }
}
