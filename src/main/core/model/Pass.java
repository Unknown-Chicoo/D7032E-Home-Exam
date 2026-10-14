package main.core.model;

/** The player stops playing cards and draws (rule 10). */
public record Pass(Player player) implements TurnAction {
}
