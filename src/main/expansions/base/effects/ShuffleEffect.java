package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.events.GameEvent.DeckShuffled;
import main.core.model.Play;

/** Shuffle: shuffle the deck (rule 11c). */
public final class ShuffleEffect implements CardEffect {

    @Override
    public void apply(GameContext ctx, Play play) {
        ctx.state().deck().shuffle(ctx.random());
        ctx.publish(new DeckShuffled());
    }
}
