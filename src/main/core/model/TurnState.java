package main.core.model;

import java.util.List;

/** Whose turn it is and how many turns they still owe; replaces the original static counter (rule 10). */
public final class TurnState {
    private final List<Player> players;
    private int currentIndex;
    private int turnsLeft;
    private int direction;

    public TurnState(List<Player> players, int startIndex) {
        this.players = players;
        this.currentIndex = startIndex;
        this.turnsLeft = 1;
        this.direction = 1;
    }

    public Player current() {
        throw new UnsupportedOperationException("TODO: player whose turn it is");
    }

    public int turnsLeft() {
        return turnsLeft;
    }

    public void endTurn() {
        throw new UnsupportedOperationException("TODO: finish one turn, advance when none are left (rule 10)");
    }

    /** Ends the current player's turns and gives {@code next} the given number of turns. */
    public void passTo(Player next, int turns) {
        throw new UnsupportedOperationException("TODO: make next the current player with the given turns");
    }

    /** Flips the order of play. */
    public void reverse() {
        throw new UnsupportedOperationException("TODO: flip the direction of play");
    }

    /** +1 for clockwise, -1 for counter-clockwise. */
    public int direction() {
        return direction;
    }

    public void playerEliminated(Player player) {
        throw new UnsupportedOperationException("TODO: drop an exploded player from the rotation");
    }

    public Player nextAlive(Player player) {
        throw new UnsupportedOperationException("TODO: next living player after the given one");
    }
}
