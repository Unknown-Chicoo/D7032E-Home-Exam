package main.expansions.base.effects;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import main.core.rules.CardEffect;
import main.core.rules.GameContext;
import main.core.rules.InvalidMoveException;
import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/** Favor: the target gives the player one card of the target's choice (rule 11b). */
public final class FavorEffect implements CardEffect {
    private static final Predicate<Player> HAS_CARDS = p -> p.hand().size() > 0;

    /**
     * Asks for the target before the Nope window. This effect decides who is eligible (a player with
     * cards); the context does the asking. A target already on the play is kept only if eligible.
     */
    @Override
    public Play prepare(GameContext ctx, Play play) throws InvalidMoveException {
        Optional<Player> preset = play.target().filter(t -> ctx.isEligibleTarget(play.player(), t, HAS_CARDS));
        Player target = preset.isPresent() ? preset.get() : ctx.chooseTarget(play.player(), HAS_CARDS);
        return new Play(play.player(), play.cards(), Optional.of(target), play.namedType());
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
