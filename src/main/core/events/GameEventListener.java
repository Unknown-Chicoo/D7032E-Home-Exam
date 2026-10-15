package main.core.events;

/** Receives every game event published on the {@link EventBus} it subscribed to. */
public interface GameEventListener {

    /**
     * Called on the game thread for every published event, public or private. The listener
     * must itself drop events whose {@link GameEvent#privateTo()} names another player, and
     * must not change game state.
     */
    void onEvent(GameEvent event);
}
