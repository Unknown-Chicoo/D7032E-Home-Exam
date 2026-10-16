package main.core.events;

import java.util.Optional;

import main.core.model.Player;

/**
 * A play was rejected; only the player who tried it is told why, and they choose again.
 *
 * @param player who tried the play
 * @param reason the message of the {@code InvalidMoveException}
 */
public record MoveRejected(Player player, String reason) implements GameEvent {
    @Override
    public String describe() {
        return "Not allowed: " + reason;
    }

    @Override
    public Optional<Player> privateTo() {
        return Optional.of(player);
    }
}
