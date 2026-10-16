package main.core.events;

import main.core.model.Player;

/**
 * A player drew a card into their hand; the table sees that, but not which card.
 *
 * @param player who drew
 */
public record PlayerDrew(Player player) implements GameEvent {
    @Override
    public String describe() {
        return player.name() + " drew a card";
    }
}
