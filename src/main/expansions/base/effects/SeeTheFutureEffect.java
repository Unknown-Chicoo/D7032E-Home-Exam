package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.model.Play;

/** See the Future: privately show the player the top three cards. */
public final class SeeTheFutureEffect implements CardEffect {

    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: show the top three cards to the player");
    }
}
