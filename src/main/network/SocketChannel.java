package main.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.Optional;

/** {@link MessageChannel} over a TCP socket using object streams. */
public final class SocketChannel implements MessageChannel {
    private static final long POLL_MILLIS = 20;

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    /**
     * Opens both object streams, writing this side's header before reading the other's, so the two
     * ends never deadlock.
     *
     * @throws IOException if the socket is already closed or the other side's header cannot be read
     */
    public SocketChannel(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void send(Message message) throws IOException {
        synchronized (sendLock) {
            out.writeObject(message);
            out.flush();
            out.reset();
        }
    }

    @Override
    public Message receive() throws IOException {
        synchronized (receiveLock) {
            try {
                return (Message) in.readObject();
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new IOException("Unexpected data from the other side", e);
            }
        }
    }

    /** Polls for incoming bytes, so it never reads half a message and returns promptly when interrupted. */
    @Override
    public Optional<Message> receive(Duration timeout) throws IOException {
        long deadline = System.nanoTime() + timeout.toNanos();
        InputStream raw = socket.getInputStream();
        try {
            while (in.available() == 0 && raw.available() == 0) {
                if (System.nanoTime() >= deadline) {
                    return Optional.empty();
                }
                Thread.sleep(POLL_MILLIS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
        return Optional.of(receive());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
