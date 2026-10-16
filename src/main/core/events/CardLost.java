package main.core.events;

import java.util.Optional;

import main.core.model.Card;
import main.core.model.Player;

/**
 * The losing side of a transfer; only the loser sees which card they lost.
 *
 * @param from who gave up the card, and the only one who sees this event
 * @param to who got it
 * @param card the card
 */
public record CardLost(Player from, Player to, Card card) implements GameEvent {
    @Override
    public String describe() {
        return "You lost " + card.type().name() + " to " + to.name();
    }

    @Override
    public Optional<Player> privateTo() {
        return Optional.of(from);
    }
}
