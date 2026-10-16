package main.core.events;

import java.util.Optional;

import main.core.model.Card;
import main.core.model.Player;

/**
 * The receiving side of a transfer; only the receiver sees which card they got.
 *
 * @param to who got the card, and the only one who sees this event
 * @param from who gave it up
 * @param card the card
 */
public record CardReceived(Player to, Player from, Card card) implements GameEvent {
    @Override
    public String describe() {
        return "You got " + card.type().name() + " from " + from.name();
    }

    @Override
    public Optional<Player> privateTo() {
        return Optional.of(to);
    }
}
