package main.io;

import main.core.events.GameEvent;
import main.core.events.GameEventListener;
import main.core.model.Player;

/** Prints game events for one viewer, hiding events that are private to someone else. */
public final class ConsoleView implements GameEventListener {
    private final ConsoleOutput out;
    private final Player viewer;

    public ConsoleView(ConsoleOutput out, Player viewer) {
        this.out = out;
        this.viewer = viewer;
    }

    @Override
    public void onEvent(GameEvent event) {
        throw new UnsupportedOperationException("TODO: print the event unless privateTo() excludes the viewer");
    }
}
