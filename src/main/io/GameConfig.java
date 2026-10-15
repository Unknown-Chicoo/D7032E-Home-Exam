package main.io;

import java.time.Duration;
import java.util.List;

import main.core.expansion.Expansion;

/**
 * The choices made in the start menu (Req. 15).
 *
 * @param expansions the chosen expansions, the base game included
 * @param humans 0 for a bots-only game, otherwise the local player plus the remote ones
 * @param bots the number of bot seats
 * @param online true if remote players join, i.e. {@code humans > 1}
 * @param port the port a hosted game listens on; unused when not online
 * @param nopeWindow how long each Nope round stays open
 */
public record GameConfig(List<Expansion> expansions, int humans, int bots, boolean online, int port,
        Duration nopeWindow) {

    /** How long players may answer with a Nope (rule 12). */
    public static final Duration DEFAULT_NOPE_WINDOW = Duration.ofSeconds(5);

    /** The port a hosted game listens on unless the host picks another. */
    public static final int DEFAULT_PORT = 2048;
}
