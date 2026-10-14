package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Play;

/** Two of a kind: steal a random card from the target. */
public final class TwoOfAKindEffect implements CardEffect {

    @Override
    public void validate(GameContext ctx, Play play) throws InvalidMoveException {
        throw new UnsupportedOperationException("TODO: require two matching cards and a target");
    }
    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: steal a random card from the target");
    }
}
