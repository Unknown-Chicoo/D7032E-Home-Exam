package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Play;

/** Nope: cancels another play; only playable inside the Nope window, never as a turn action (rule 12). */
public final class NopeEffect implements CardEffect {

    @Override
    public void validate(GameContext ctx, Play play) throws InvalidMoveException {
        throw new InvalidMoveException("A Nope can only be played in response to another play (rule 12)");
    }

    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: cancel the noped play (rule 12)");
    }
}
