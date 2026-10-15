package main.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/** Listens on a port and accepts the remote players' connections before the game starts (Req. 17). */
public final class GameServer {
    private final int port;

    /** Creates a server for {@code port}; nothing is opened until {@link #acceptClients}. */
    public GameServer(int port) {
        this.port = port;
    }

    /**
     * Blocks until {@code count} clients have connected, then stops listening.
     *
     * @return one open channel per client, in the order they connected
     * @throws IOException if the port cannot be opened or a connection fails
     */
    public List<MessageChannel> acceptClients(int count) throws IOException {
        List<MessageChannel> channels = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(port)) {
            while (channels.size() < count) {
                channels.add(new SocketChannel(server.accept()));
            }
        }
        return channels;
    }
}
