package main.expansions.base.effects;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.model.Play;
import main.core.model.TurnState;

/**
 * Attack: end the turn without drawing; the next player takes 2 turns, or, if the attacker was
 * itself under attack, its remaining turns + 2 (rule 11a: 2, 4, 6, ...).
 */
public final class AttackEffect implements CardEffect {

    @Override
    public void apply(GameContext ctx, Play play) {
        TurnState turns = ctx.state().turns();
        int left = turns.turnsLeft();
        int given = left > 1 ? left + 2 : 2;
        turns.passTo(turns.nextAlive(play.player()), given);
    }
}
