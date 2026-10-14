package main.network;

import main.io.ConsoleInput;
import main.io.ConsoleOutput;

/** Thin client: shows server messages and sends the player's answers back (Req. 17). */
public final class GameClient {
    private final MessageChannel channel;
    private final ConsoleInput in;
    private final ConsoleOutput out;

    public GameClient(MessageChannel channel, ConsoleInput in, ConsoleOutput out) {
        this.channel = channel;
        this.in = in;
        this.out = out;
    }

    public void run() {
        throw new UnsupportedOperationException("TODO: loop: receive, show, answer questions, stop on GAME_OVER");
    }
}
