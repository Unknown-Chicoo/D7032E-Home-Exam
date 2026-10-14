package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.model.Play;

/** Skip: end one turn without drawing; under attack only one of the turns (rule 11d). */
public final class SkipEffect implements CardEffect {

    @Override
    public void apply(GameContext ctx, Play play) {
        ctx.state().turns().endTurn();
    }
}
