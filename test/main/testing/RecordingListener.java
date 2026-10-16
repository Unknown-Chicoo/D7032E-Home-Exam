package main.testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.core.events.GameEvent;
import main.core.events.GameEventListener;

/** Test listener that keeps every published event, public or private, in order. */
public final class RecordingListener implements GameEventListener {
    private final List<GameEvent> events = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void onEvent(GameEvent event) {
        events.add(event);
    }

    /** A copy of every event so far. */
    public List<GameEvent> all() {
        synchronized (events) {
            return List.copyOf(events);
        }
    }

    /** The events of one type, in order. */
    public <T extends GameEvent> List<T> of(Class<T> type) {
        return all().stream().filter(type::isInstance).map(type::cast).toList();
    }

    /** One line per event: its type, who may see it, and its text. */
    public List<String> transcript() {
        return all().stream()
                .map(e -> e.getClass().getSimpleName() + e.privateTo().map(p -> "@" + p.name()).orElse("")
                        + ": " + e.describe())
                .toList();
    }
}
