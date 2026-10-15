package main.core.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Delivers each published event to every subscribed listener, in subscription order. */
public final class EventBus {
    private final List<GameEventListener> listeners;

    /** Creates a bus with no listeners. */
    public EventBus() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    /** Adds a listener; safe to call while an event is being delivered. */
    public void subscribe(GameEventListener listener) {
        listeners.add(listener);
    }

    /** Delivers {@code event} synchronously on the calling thread, which is the game thread. */
    public void publish(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
