package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Play;

/** Three of a kind: name a card type and take it from the target if they have one. */
public final class ThreeOfAKindEffect implements CardEffect {

    @Override
    public void validate(GameContext ctx, Play play) throws InvalidMoveException {
        throw new UnsupportedOperationException("TODO: require three matching cards, a target and a named type");
    }
    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: take the named card type from the target");
    }
}
