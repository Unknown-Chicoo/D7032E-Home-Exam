package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.model.Play;

/** Skip: end one turn without drawing. */
public final class SkipEffect implements CardEffect {

    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: end one turn without drawing");
    }
}
