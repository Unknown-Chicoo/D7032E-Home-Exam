package main.core.engine;

import java.util.Optional;

import main.core.cards.CardRegistry;
import main.core.cards.DrawEffect;
import main.core.cards.GameContext;
import main.core.events.GameEvent.CardDrawn;
import main.core.events.GameEvent.PlayerDrew;
import main.core.model.Card;
import main.core.model.Player;
import main.core.model.TurnState;

/** Draws the top card, triggers its draw effect or puts it in the hand, and ends the turn (rules 9, 10). */
public final class DrawResolver {
    private final CardRegistry registry;

    public DrawResolver(CardRegistry registry) {
        this.registry = registry;
    }

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
