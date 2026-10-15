package main.io;

import java.time.Duration;
import java.util.List;

import main.core.expansion.Expansion;

/** The choices made in the start menu (Req. 15); {@code humans} counts the local player plus remote ones. */
public record GameConfig(List<Expansion> expansions, int humans, int bots, boolean online, int port,
        Duration nopeWindow) {

    /** How long players may answer with a Nope (rule 12). */
    public static final Duration DEFAULT_NOPE_WINDOW = Duration.ofSeconds(5);

    /** The port a hosted game listens on unless the host picks another. */
    public static final int DEFAULT_PORT = 2048;
}
