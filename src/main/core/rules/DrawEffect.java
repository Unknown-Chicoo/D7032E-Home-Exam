package main.core.rules;

import main.core.model.Card;
import main.core.model.Player;

/**
 * What happens when a card of a given type is drawn, e.g. an
 * Exploding Kitten (rule 10). Registered per type with {@link CardRegistry#registerOnDraw}.
 */
public interface DrawEffect {

    /**
     * Called on the game thread right after {@code card} was taken from the top of the deck. The card
     * is in nobody's hand yet: the effect decides where it goes (back into the deck, the discard pile,
     * a hand) and publishes the matching event. If the effect does not end the turn, {@code DrawResolver}
     * ends it (rule 9).
     *
     * @param ctx the effect's only access to the game
     * @param player who drew the card
     * @param card the drawn card
     */
    void onDraw(GameContext ctx, Player player, Card card);
}
