package main.network;

import java.util.List;

/** Listens on a port and accepts the remote players' connections (Req. 17). */
public final class GameServer {
    private final int port;

    public GameServer(int port) {
        this.port = port;
    }

    public List<MessageChannel> acceptClients(int count) {
        throw new UnsupportedOperationException("TODO: accept count clients and wrap them in channels");
    }
}
