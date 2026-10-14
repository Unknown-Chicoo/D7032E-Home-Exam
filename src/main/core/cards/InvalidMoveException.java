package main.core.cards;

/** Thrown when playing a card violates a rule; the player is asked again. */
public final class InvalidMoveException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidMoveException(String message) {
        super(message);
    }
}
