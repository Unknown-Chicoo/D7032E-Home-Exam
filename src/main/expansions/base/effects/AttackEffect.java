package main.expansions.base.effects;

import main.core.rules.CardEffect;
import main.core.rules.GameContext;
import main.core.model.Play;
import main.core.model.TurnState;

/**
 * Attack: end the turn without drawing; the next player takes 2 turns, or, if the attacker was given
 * more than one turn (was attacked), its remaining turns + 2 (rule 11a: 2, 4, 6, ...; 3 after one turn used).
 */
public final class AttackEffect implements CardEffect {

    /** Ends the turn through {@code passTo}, so the attacker does not draw. */
    @Override
    public void apply(GameContext ctx, Play play) {
        TurnState turns = ctx.state().turns();
        // rule 11a: an attacked player who plays Attack passes their remaining turns + 2
        int given = turns.turnsGiven() > 1 ? turns.turnsLeft() + 2 : 2;
        turns.passTo(turns.nextAlive(play.player()), given);
    }
}
