package main.io;

import java.time.Duration;
import java.util.List;

import main.core.expansion.Expansion;

/** The choices made in the start menu (Req. 15); {@code humans} counts the local player plus remote ones. */
public record GameConfig(List<Expansion> expansions, int humans, int bots, boolean online, int port,
        Duration nopeWindow) {
}
