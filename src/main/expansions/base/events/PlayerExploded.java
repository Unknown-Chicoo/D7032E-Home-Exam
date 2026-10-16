package main.expansions.base.events;

import main.core.events.GameEvent;
import main.core.model.Player;

/**
 * A player exploded and is out (rule 10).
 *
 * @param player who exploded
 */
public record PlayerExploded(Player player) implements GameEvent {
    @Override
    public String describe() {
        return player.name() + " drew an Exploding Kitten and EXPLODED!";
    }
}
