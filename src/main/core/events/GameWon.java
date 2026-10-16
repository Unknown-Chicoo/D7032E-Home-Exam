package main.core.events;

import main.core.model.Player;

/**
 * The game is over; told to everyone (rule 13).
 *
 * @param winner the last player alive
 */
public record GameWon(Player winner) implements GameEvent {
    @Override
    public String describe() {
        return winner.name() + " is the last player alive and wins the game!";
    }
}
