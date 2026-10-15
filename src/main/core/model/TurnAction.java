package main.core.model;

/** What a player chose to do on their turn: draw ({@link Pass}) or play cards ({@link Play}) (rule 9). */
public sealed interface TurnAction permits Pass, Play {
}
