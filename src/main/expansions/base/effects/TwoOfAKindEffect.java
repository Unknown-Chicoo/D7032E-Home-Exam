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

/** Two of a kind: steal a random card from the target (rule 11f). */
public final class TwoOfAKindEffect implements CardEffect {
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

    /** Moves a card picked with the game's randomizer; nothing happens if the target's hand is empty by now. */
    @Override
    public void apply(GameContext ctx, Play play) {
        Player target = play.target().orElseThrow();
        List<Card> hand = target.hand().cards();
        if (!hand.isEmpty()) {
            ctx.transfer(hand.get(ctx.random().nextInt(hand.size())), target, play.player());
        }
    }
}
