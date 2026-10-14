package main.core.model;

/** What a player chose to do on their turn: end it by drawing, or play cards. */
public sealed interface TurnAction permits Pass, Play {
}
