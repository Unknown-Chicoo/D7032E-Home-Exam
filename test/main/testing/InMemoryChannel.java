package main.testing;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import main.network.Message;
import main.network.MessageChannel;

/**
 * Test channel: the test queues the client's replies up front and reads what the server sent. A
 * blocking receive gives up after two seconds so a broken test fails instead of hanging. After
 * {@link #breakConnection()} every call throws {@link IOException}.
 */
public final class InMemoryChannel implements MessageChannel {
    private static final Duration GIVE_UP = Duration.ofSeconds(2);

    private final BlockingQueue<Message> incoming = new LinkedBlockingQueue<>();
    private final List<Message> sent = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean broken;
    private volatile boolean closed;

    /** Queues a message as if the client had sent it. */
    public InMemoryChannel reply(Message message) {
        incoming.add(message);
        return this;
    }

    public void breakConnection() {
        broken = true;
    }

    public List<Message> sent() {
        synchronized (sent) {
            return List.copyOf(sent);
        }
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void send(Message message) throws IOException {
        check();
        sent.add(message);
    }

    @Override
    public Message receive() throws IOException {
        return receive(GIVE_UP).orElseThrow(() -> new IOException("No reply queued"));
    }

    @Override
    public Optional<Message> receive(Duration timeout) throws IOException {
        check();
        try {
            return Optional.ofNullable(incoming.poll(timeout.toNanos(), TimeUnit.NANOSECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    private void check() throws IOException {
        if (broken || closed) {
            throw new IOException("Connection lost");
        }
    }
}
