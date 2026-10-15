package main.core.engine;

import java.util.ArrayList;
import java.util.List;

import main.core.rules.CardEffect;
import main.core.rules.CardRegistry;
import main.core.rules.GameContext;
import main.core.rules.InvalidMoveException;
import main.core.events.GameEvent.CardPlayed;
import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/** Resolves one play of one card or a combo (rules 11, 12). */
public final class PlayResolver {
    private final CardRegistry registry;
    private final NopeResolver nopeResolver;

    /** Creates a resolver that finds effects in {@code registry} and runs Nope windows with {@code nopeResolver}. */
    public PlayResolver(CardRegistry registry, NopeResolver nopeResolver) {
        this.registry = registry;
        this.nopeResolver = nopeResolver;
    }

    /**
     * Resolves {@code play}; call on the game thread. Rejects a lone Nope, finds the effect, checks that
     * the cards are in the hand, lets the effect prepare the play, discards the cards and announces the
     * play, runs the Nope window, then applies the effect unless it was Noped. A combo can always be
     * Noped; a single card only if its type {@code isNopeable()} (rule 12).
     *
     * @throws InvalidMoveException if the play is illegal: a lone Nope, no matching effect, cards not
     *         in the hand, or rejected by the effect's {@code prepare}. Nothing has been discarded and the
     *         player chooses again.
     */
    public void resolve(GameContext ctx, Play play) throws InvalidMoveException {
        List<Card> cards = play.cards();
        if (cards.size() == 1 && cards.getFirst().type().isNope()) {
            throw new InvalidMoveException("Nope can only be played in response to another card");
        }
        CardEffect effect = registry.effectFor(play);
        requireInHand(play.player(), cards);
        Play prepared = effect.prepare(ctx, play);

        cards.forEach(card -> play.player().hand().remove(card));
        ctx.state().discard().addAll(cards);
        ctx.publish(new CardPlayed(prepared));

        boolean nopeable = cards.size() > 1 || cards.getFirst().type().isNopeable();   // rule 12: combos always
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
