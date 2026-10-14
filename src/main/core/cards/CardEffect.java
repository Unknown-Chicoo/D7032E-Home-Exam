package main.core.cards;

import main.core.model.Play;

/** What a card or combo does when played (Strategy). */
public interface CardEffect {

    /**
     * Completes the play before anyone may Nope it, e.g. by asking for a target, so the table
     * knows what is being Noped. Returns the play unchanged by default.
     */
    default Play prepare(GameContext ctx, Play play) {
        return play;
    }

    /** Rejects the play before the Nope window opens; accepts by default. */
    default void validate(GameContext ctx, Play play) throws InvalidMoveException {
    }

    void apply(GameContext ctx, Play play);
}
