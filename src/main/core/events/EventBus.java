package main.core.events;

import java.util.ArrayList;
import java.util.List;

/** Delivers each published event to every subscribed listener. */
public final class EventBus {
    private final List<GameEventListener> listeners;

    public EventBus() {
        this.listeners = new ArrayList<>();
    }

    public void subscribe(GameEventListener listener) {
        throw new UnsupportedOperationException("TODO: register a listener");
    }

    public void publish(GameEvent event) {
        throw new UnsupportedOperationException("TODO: deliver the event to all listeners");
    }
}
