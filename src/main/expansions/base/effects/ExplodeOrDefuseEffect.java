package main.expansions.base.effects;

import static main.expansions.base.BaseCardType.DEFUSE;

import java.util.Optional;

import main.core.rules.DrawEffect;
import main.core.rules.GameContext;
import main.core.events.GameEvent.KittenDefused;
import main.core.events.GameEvent.PlayerExploded;
import main.core.model.Card;
import main.core.model.Deck;
import main.core.model.Player;

/** Drawing an Exploding Kitten: discard a Defuse and put the kitten back anywhere, or explode (rule 10). */
public final class ExplodeOrDefuseEffect implements DrawEffect {

    /** Asks the player for the kitten's position if they defuse; the position is clamped to the deck. */
    @Override
    public void onDraw(GameContext ctx, Player player, Card card) {
        Optional<Card> defuse = player.hand().removeFirstMatching(DEFUSE::equals);
        if (defuse.isPresent()) {
            ctx.state().discard().add(defuse.get());
            Deck deck = ctx.state().deck();
            int position = ctx.controllerOf(player).chooseDeckPosition(ctx.viewFor(player), deck.size());
            deck.insertAt(Math.clamp(position, 0, deck.size()), card);
            ctx.publish(new KittenDefused(player));
        } else {
            ctx.state().discard().addAll(player.hand().clear());
            ctx.state().discard().add(card);
            player.eliminate();
            ctx.state().turns().playerEliminated(player);
            ctx.publish(new PlayerExploded(player));
        }
    }
}
