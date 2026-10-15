package main.core.model;

import java.util.List;

/** Whose turn it is, how many turns they still owe, and the direction of play. */
public final class TurnState {
    private final List<Player> players;
    private int currentIndex;
    private int turnsLeft;
    private int direction;
    private int turnNumber;

    /**
     * Starts with one turn for the start player, clockwise.
     *
     * @param players every seat in seat order; copied
     * @param startIndex the index in {@code players} of the player who starts (rule 8)
     */
    public TurnState(List<Player> players, int startIndex) {
        this.players = List.copyOf(players);
        this.currentIndex = startIndex;
        this.turnsLeft = 1;
        this.direction = 1;
        this.turnNumber = 0;
    }

    /** The player whose turn it is. */
    public Player current() {
        return players.get(currentIndex);
    }

    /** How many turns the current player still owes, counting this one; above 1 when under attack (rule 11a). */
    public int turnsLeft() {
        return turnsLeft;
    }

    /** Increments whenever a turn ends; compare it before and after an action to tell whether the turn ended. */
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

    /**
     * Ends the current player's turns, including any still owed, and hands the turn on (rule 11a).
     *
     * @param next the player who takes over
     * @param turns how many turns {@code next} must take
     */
    public void passTo(Player next, int turns) {
        turnNumber++;
        currentIndex = players.indexOf(next);
        turnsLeft = turns;
    }

    /** Flips the order of play; {@link #nextAlive} and {@link #endTurn} follow the new direction. */
    public void reverse() {
        direction = -direction;
    }

    /** +1 for clockwise, -1 for counter-clockwise. */
    public int direction() {
        return direction;
    }

    /**
     * An exploded current player forfeits any turns still owed; the next living player gets one
     * (rule 10). Does nothing if {@code player} is not the current player.
     */
    public void playerEliminated(Player player) {
        if (player == current()) {
            passTo(nextAlive(player), 1);
        }
    }

    /**
     * The next living player after {@code player} in the direction of play.
     *
     * @return {@code player} itself if it is the only one left alive
     * @throws IllegalStateException if nobody is alive
     */
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
