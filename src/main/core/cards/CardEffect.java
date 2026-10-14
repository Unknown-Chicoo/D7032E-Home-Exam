package main.core.cards;

import main.core.model.Play;

/** What a card or combo does when played (Strategy). */
public interface CardEffect {

    /** Rejects the play before the Nope window opens; accepts by default. */
    default void validate(GameContext ctx, Play play) throws InvalidMoveException {
    }

    void apply(GameContext ctx, Play play);
}
