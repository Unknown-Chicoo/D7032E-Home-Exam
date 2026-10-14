package main.core.events;

/** Receives game events (Observer). */
public interface GameEventListener {
    void onEvent(GameEvent event);
}
