package main.core.events;

import java.util.Optional;

import main.core.model.Player;

/**
 * Something that happened in the game, published on the {@link EventBus}. Core events live in this
 * package; expansions define their own event records in their own packages.
 */
public interface GameEvent {

    /** The text shown to viewers; a private event is phrased to its one viewer. */
    String describe();

    /**
     * The only player allowed to see this event.
     *
     * @return empty if the event is public
     */
    default Optional<Player> privateTo() {
        return Optional.empty();
    }
}
