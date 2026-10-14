package main.network;

import main.core.events.GameEvent;
import main.core.model.Player;

/** A remote player's connection was lost and a bot now plays their seat (Req. 17). */
public record PlayerDisconnected(Player player) implements GameEvent {
    @Override
    public String describe() {
        return player.name() + " disconnected, a bot takes over";
    }
}
