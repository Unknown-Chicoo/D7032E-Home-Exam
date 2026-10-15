package main.expansions.base.effects;

import java.util.List;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/** Two of a kind: steal a random card from the target (rule 11f). */
public final class TwoOfAKindEffect implements CardEffect {

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
        if (!hand.isEmpty()) {
            ctx.transfer(hand.get(ctx.random().nextInt(hand.size())), target, play.player());
        }
    }
}
