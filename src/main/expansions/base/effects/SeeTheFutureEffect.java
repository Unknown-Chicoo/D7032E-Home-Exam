package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.events.GameEvent.FutureSeen;
import main.core.model.Play;

/** See the Future: privately show the player the top three cards (rule 11e). */
public final class SeeTheFutureEffect implements CardEffect {
    private static final int CARDS_SHOWN = 3;

    @Override
    public void apply(GameContext ctx, Play play) {
        ctx.publish(new FutureSeen(play.player(), ctx.state().deck().peek(CARDS_SHOWN)));
    }
}
