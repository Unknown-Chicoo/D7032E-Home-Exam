package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Play;

/** Favor: the target gives the player a card of the target's choice. */
public final class FavorEffect implements CardEffect {

    @Override
    public void validate(GameContext ctx, Play play) throws InvalidMoveException {
        throw new UnsupportedOperationException("TODO: require a living target other than the player");
    }
    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: target chooses a card to give the player");
    }
}
