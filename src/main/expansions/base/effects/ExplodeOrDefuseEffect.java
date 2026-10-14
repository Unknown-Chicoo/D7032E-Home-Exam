package main.expansions.base.effects;

import main.core.cards.DrawEffect;
import main.core.cards.GameContext;
import main.core.model.Card;
import main.core.model.Player;

/** Drawing an Exploding Kitten: defuse and reinsert it, or explode and leave the game. */
public final class ExplodeOrDefuseEffect implements DrawEffect {

    @Override
    public void onDraw(GameContext ctx, Player player, Card card) {
        throw new UnsupportedOperationException("TODO: defuse and reinsert, or eliminate the player");
    }
}
