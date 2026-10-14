package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.model.Play;

/** Attack: end the turn without drawing and pass stacked turns on (rule 11). */
public final class AttackEffect implements CardEffect {

    @Override
    public void apply(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: compute stacked turns and call TurnState.passTo on the next player (rule 11)");
    }
}
