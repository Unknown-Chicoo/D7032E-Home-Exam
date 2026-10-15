package main.expansions.base.effects;

import main.core.rules.CardEffect;
import main.core.rules.GameContext;
import main.core.model.Play;

/** Skip: end one turn without drawing; under attack only one of the turns (rule 11d). */
public final class SkipEffect implements CardEffect {

    /** Ends one owed turn through {@code endTurn}, without drawing. */
    @Override
    public void apply(GameContext ctx, Play play) {
        ctx.state().turns().endTurn();
    }
}
