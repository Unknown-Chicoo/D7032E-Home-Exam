package main.core.engine;

import java.time.Duration;

import main.core.cards.GameContext;
import main.core.model.Play;
import main.core.util.Clock;

/** Runs the timed Nope window and its chains (rule 12, Req. 20). */
public final class NopeResolver {
    private final Clock clock;
    private final Duration window;

    public NopeResolver(Clock clock, Duration window) {
        this.clock = clock;
        this.window = window;
    }

    public boolean isNoped(GameContext ctx, Play play) {
        throw new UnsupportedOperationException("TODO: offer Nopes until the window closes (rule 12, Req. 20)");
    }
}
