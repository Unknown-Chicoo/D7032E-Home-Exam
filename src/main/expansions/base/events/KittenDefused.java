package main.expansions.base.events;

import main.core.events.GameEvent;
import main.core.model.Player;

/**
 * A player defused an Exploding Kitten and put it back; the position stays secret (rule 10).
 *
 * @param player who defused
 */
public record KittenDefused(Player player) implements GameEvent {
    @Override
    public String describe() {
        return player.name() + " drew an Exploding Kitten, defused it and put it back in the deck";
    }
}
