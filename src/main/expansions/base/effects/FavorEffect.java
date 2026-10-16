package main.expansions.base.effects;

import java.util.List;

import main.core.rules.CardEffect;
import main.core.rules.GameContext;
import main.core.rules.InvalidMoveException;
import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/** Favor: the target gives the player one card of the target's choice (rule 11b). */
public final class FavorEffect implements CardEffect {

    /**
     * Asks for the target before the Nope window. This effect decides who is eligible (a player with
     * cards); the context does the asking.
     */
    @Override
    public Play prepare(GameContext ctx, Play play) throws InvalidMoveException {
        return play.withTarget(ctx.chooseTarget(play.player(), p -> p.hand().size() > 0));
    }

    /**
     * Asks the target which card to give. If the target's hand is empty by now, nothing happens; if
     * the answer is not in their hand, their first card is given.
     */
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
