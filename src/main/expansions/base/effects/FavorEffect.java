package main.expansions.base.effects;

import java.util.List;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/** Favor: the target gives the player one card of the target's choice (rule 11b). */
public final class FavorEffect implements CardEffect {

    @Override
    public Play prepare(GameContext ctx, Play play) throws InvalidMoveException {
        Play targeted = Targeting.withTarget(ctx, play);
        Targeting.requireTarget(ctx, targeted);
        return targeted;
    }

    @Override
    public void apply(GameContext ctx, Play play) {
        Player target = play.target().orElseThrow();
        List<Card> hand = target.hand().cards();
        if (hand.isEmpty()) {
            return;
        }
        Card card = ctx.controllerOf(target).chooseCardToGive(ctx.viewFor(target), play.player());
        ctx.transfer(hand.contains(card) ? card : hand.get(0), target, play.player());
    }
}
