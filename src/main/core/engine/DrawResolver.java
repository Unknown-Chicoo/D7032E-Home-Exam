package main.core.engine;

import java.util.Optional;

import main.core.rules.CardRegistry;
import main.core.rules.DrawEffect;
import main.core.rules.GameContext;
import main.core.events.CardDrawn;
import main.core.events.PlayerDrew;
import main.core.model.Card;
import main.core.model.Player;
import main.core.model.TurnState;

/** Resolves a {@code Pass}: the player draws the top card, which ends the turn (rules 9, 10). */
public final class DrawResolver {
    private final CardRegistry registry;

    /** Creates a resolver that looks up draw effects in {@code registry}. */
    public DrawResolver(CardRegistry registry) {
        this.registry = registry;
    }

    /**
     * Draws the top card for {@code player}; call on the game thread. Runs the card's draw effect if it
     * has one, otherwise puts it in the hand; then ends the turn unless the draw effect already did.
     *
     * @throws IllegalStateException if the deck is empty
     */
    public void draw(GameContext ctx, Player player) {
        TurnState turns = ctx.state().turns();
        int turnAtStart = turns.turnNumber();
        Card card = ctx.state().deck().drawTop();
        Optional<DrawEffect> onDraw = registry.onDrawFor(card.type());
        if (onDraw.isPresent()) {
            onDraw.get().onDraw(ctx, player, card);
        } else {
            player.hand().add(card);
            ctx.publish(new PlayerDrew(player));
            ctx.publish(new CardDrawn(player, card));
        }
        if (turns.turnNumber() == turnAtStart) {
            turns.endTurn();
        }
    }
}
