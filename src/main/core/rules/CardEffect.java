package main.core.rules;

import main.core.model.Play;

/**
 * What a card or combo does when played; an expansion registers it in the {@link CardRegistry}.
 * In both methods, {@code ctx} is the effect's only access to the game and {@code play} is the play
 * being resolved.
 */
public interface CardEffect {

    /**
     * Completes and checks the play, e.g. by asking for a target. Called on the game thread while the
     * cards are still in the hand, before the play is announced and before the Nope window. Must not
     * change game state, since the play may still be rejected. The default returns the play unchanged.
     *
     * @return the completed play; it is announced and passed to {@link #apply}
     * @throws InvalidMoveException if the play is illegal, e.g. no player can be targeted; nothing is
     *         discarded and the player chooses again
     */
    default Play prepare(GameContext ctx, Play play) throws InvalidMoveException {
        return play;
    }

    /**
     * Carries out the play (rule 11). Called on the game thread only if the play survived the Nope
     * window; the cards are already on the discard pile. May end the turn through
     * {@code ctx.state().turns()}; if it does not, the player keeps playing.
     *
     * @param play the play returned by {@link #prepare}
     */
    void apply(GameContext ctx, Play play);
}
