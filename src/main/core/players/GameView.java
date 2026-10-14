package main.core.players;

import java.util.List;
import java.util.Map;

import main.core.model.Card;
import main.core.model.Player;

/**
 * What one player is allowed to see when deciding: an immutable snapshot, safe to hand to another
 * thread. {@code handSizes} lists the living players in seat order; {@code turnsLeft} is 0 when it is not their turn.
 */
public record GameView(Player self, List<Card> hand, int deckSize, Map<Player, Integer> handSizes, int turnsLeft) {
}
