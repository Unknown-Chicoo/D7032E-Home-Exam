package main.core.events;

import main.core.model.Player;

/**
 * A player's turn began (rule 9).
 *
 * @param player whose turn it is
 * @param turnsLeft turns owed including this one; above 1 under attack (rule 11a)
 */
public record TurnStarted(Player player, int turnsLeft) implements GameEvent {
    @Override
    public String describe() {
        String owed = turnsLeft > 1 ? " (" + turnsLeft + " turns left)" : "";
        return "--- " + player.name() + "'s turn" + owed + " ---";
    }
}
