package main.io;

import java.util.Optional;

import main.core.events.GameEvent;
import main.core.events.GameEventListener;
import main.core.model.Player;

/** Prints game events for one viewer or a spectator, hiding events that are private to someone else. */
public final class ConsoleView implements GameEventListener {
    private final ConsoleOutput out;
    private final TextRenderer renderer;
    private final Optional<Player> viewer;

    /** An empty {@code viewer} is a spectator, e.g. in a bots-only game, who sees only public events. */
    public ConsoleView(ConsoleOutput out, TextRenderer renderer, Optional<Player> viewer) {
        this.out = out;
        this.renderer = renderer;
        this.viewer = viewer;
    }

    /** Prints the event as {@link TextRenderer} renders it for this viewer, if they may see it. */
    @Override
    public void onEvent(GameEvent event) {
        renderer.render(event, viewer).ifPresent(out::println);
    }
}
