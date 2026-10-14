package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.model.Play;

/** Shuffle: shuffle the deck. */
public final class ShuffleEffect implements CardEffect {

    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: shuffle the deck");
    }
}
