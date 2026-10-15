package main.core.players;

import java.util.List;
import java.util.Map;

import main.core.model.Card;
import main.core.model.Player;

/**
 * What one player is allowed to see when deciding: an immutable snapshot, safe to hand to another
 * thread.
 *
 * @param self the player deciding
 * @param hand that player's cards, in hand order
 * @param deckSize the number of cards left in the deck
 * @param handSizes the living players, including {@code self}, in seat order, mapped to how many cards they hold
 * @param turnsLeft turns {@code self} still owes; 0 when it is not their turn
 */
public record GameView(Player self, List<Card> hand, int deckSize, Map<Player, Integer> handSizes, int turnsLeft) {
}
