package main.core.cards;

import main.core.model.Play;

/** What a card or combo does when played (Strategy). */
public interface CardEffect {

    /**
     * Completes and checks the play before anyone may Nope it, e.g. by asking for a target, so the
     * table knows what is being Noped. Throws if the play is illegal; returns it unchanged by default.
     */
    default Play prepare(GameContext ctx, Play play) throws InvalidMoveException {
        return play;
    }

    void apply(GameContext ctx, Play play);
}
