package main.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.Optional;

/** {@link MessageChannel} over a TCP socket using Object streams. */
public final class SocketChannel implements MessageChannel {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public SocketChannel(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void send(Message message) {
        throw new UnsupportedOperationException("TODO: write the message to the object stream");
    }

    @Override
    public Message receive() {
        throw new UnsupportedOperationException("TODO: block until a message arrives");
    }

    @Override
    public Optional<Message> receive(Duration timeout) {
        throw new UnsupportedOperationException("TODO: wait for a message with a timeout (Req. 20)");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("TODO: close the streams and socket");
    }
}
