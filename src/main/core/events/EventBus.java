package main.core.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Delivers each published event to every subscribed listener (Observer). */
public final class EventBus {
    private final List<GameEventListener> listeners;

    public EventBus() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public void subscribe(GameEventListener listener) {
        listeners.add(listener);
    }

    public void publish(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
