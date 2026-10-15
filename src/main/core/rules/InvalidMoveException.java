package main.core.rules;

import java.io.Serial;

/**
 * Thrown when a play breaks a rule, before any card has left the hand. The player is told the
 * message privately and chooses again.
 */
public final class InvalidMoveException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message the reason, shown to the player who tried the play
     */
    public InvalidMoveException(String message) {
        super(message);
    }
}
