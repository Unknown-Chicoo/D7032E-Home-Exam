package main.core.model;

import java.util.List;

/**
 * Whose turn it is and how many turns they still owe; replaces the original static counter.
 * Only primitive operations: the rules that use them (Attack, Skip) live in the effects.
 */
public final class TurnState {
    private final List<Player> players;
    private int currentIndex;
    private int turnsLeft;
    private int direction;
    private int turnNumber;

    public TurnState(List<Player> players, int startIndex) {
        this.players = List.copyOf(players);
        this.currentIndex = startIndex;
        this.turnsLeft = 1;
        this.direction = 1;
        this.turnNumber = 0;
    }

    public Player current() {
        return players.get(currentIndex);
    }

    public int turnsLeft() {
        return turnsLeft;
    }

    /** Increments whenever a turn ends, so the engine can tell that an effect ended the turn. */
    public int turnNumber() {
        return turnNumber;
    }

    /** Finishes one turn; when none are left the next living player gets one (rules 9, 11d). */
    public void endTurn() {
        turnNumber++;
        turnsLeft--;
        if (turnsLeft <= 0) {
            currentIndex = players.indexOf(nextAlive(current()));
            turnsLeft = 1;
        }
    }

    /** Ends the current player's turns and gives {@code next} the given number of turns. */
    public void passTo(Player next, int turns) {
        turnNumber++;
        currentIndex = players.indexOf(next);
        turnsLeft = turns;
    }

    /** Flips the order of play. */
    public void reverse() {
        direction = -direction;
    }

    /** +1 for clockwise, -1 for counter-clockwise. */
    public int direction() {
        return direction;
    }

    /** An exploded current player forfeits any turns still owed; the next living player gets one (rule 10). */
    public void playerEliminated(Player player) {
        if (player == current()) {
            passTo(nextAlive(player), 1);
        }
    }

    /** The next living player after {@code player} in the direction of play; itself if it is the only one left. */
    public Player nextAlive(Player player) {
        int start = players.indexOf(player);
        for (int step = 1; step <= players.size(); step++) {
            Player candidate = players.get(Math.floorMod(start + step * direction, players.size()));
            if (candidate.isAlive()) {
                return candidate;
            }
        }
        throw new IllegalStateException("No player is alive");
    }
}
