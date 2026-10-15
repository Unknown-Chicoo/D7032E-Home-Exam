package main.core.model;

/**
 * The player stops playing cards and draws, which ends the turn (rules 9, 10).
 *
 * @param player the player who passes
 */
public record Pass(Player player) implements TurnAction {
}
