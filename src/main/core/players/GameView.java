package main.core.players;

import java.util.List;
import java.util.Map;

import main.core.model.Card;
import main.core.model.Player;

/** What one player is allowed to see of the game when making a decision. */
public record GameView(Player self, List<Card> hand, int deckSize, Map<Player, Integer> handSizes, int turnsLeft) {
}
