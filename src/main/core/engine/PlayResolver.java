package main.core.engine;

import java.util.ArrayList;
import java.util.List;

import main.core.cards.CardEffect;
import main.core.cards.CardRegistry;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.events.GameEvent.CardPlayed;
import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/**
 * Resolves one play (rules 11, 12): reject a lone Nope, find the effect, check the hand, let the
 * effect complete and validate the play, discard the cards, open the Nope window, then apply.
 */
public final class PlayResolver {
    private final CardRegistry registry;
    private final NopeResolver nopeResolver;

    public PlayResolver(CardRegistry registry, NopeResolver nopeResolver) {
        this.registry = registry;
        this.nopeResolver = nopeResolver;
    }

    public void resolve(GameContext ctx, Play play) throws InvalidMoveException {
        List<Card> cards = play.cards();
        if (cards.size() == 1 && cards.get(0).type().isNope()) {
            throw new InvalidMoveException("Nope can only be played in response to another card");
        }
        CardEffect effect = registry.effectFor(play);
        requireInHand(play.player(), cards);
        Play prepared = effect.prepare(ctx, play);
        effect.validate(ctx, prepared);

        cards.forEach(card -> play.player().hand().remove(card));
        ctx.state().discard().addAll(cards);
        ctx.publish(new CardPlayed(prepared));

        boolean nopeable = cards.stream().allMatch(card -> card.type().isNopeable());
        if (nopeable && nopeResolver.isNoped(ctx, prepared)) {
            return;
        }
        effect.apply(ctx, prepared);
    }

    private static void requireInHand(Player player, List<Card> cards) throws InvalidMoveException {
        List<Card> remaining = new ArrayList<>(player.hand().cards());
        for (Card card : cards) {
            if (!remaining.remove(card)) {
                throw new InvalidMoveException("You do not hold " + card.type().name());
            }
        }
    }
}
