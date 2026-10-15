package main.expansions.base.effects;

import main.core.rules.CardEffect;
import main.core.rules.GameContext;
import main.core.events.GameEvent.FutureSeen;
import main.core.model.Play;

/** See the Future: privately show the player the top three cards (rule 11e). */
public final class SeeTheFutureEffect implements CardEffect {
    private static final int CARDS_SHOWN = 3;

    /** Publishes a {@code FutureSeen} event private to the player; fewer cards if the deck is smaller. */
    @Override
    public void apply(GameContext ctx, Play play) {
        ctx.publish(new FutureSeen(play.player(), ctx.state().deck().peek(CARDS_SHOWN)));
    }
}
