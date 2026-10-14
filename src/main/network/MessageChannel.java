package main.network;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

/** A two-way connection that carries {@link Message}s; failures surface as checked {@link IOException}. */
public interface MessageChannel extends AutoCloseable {
    void send(Message message) throws IOException;

    Message receive() throws IOException;

    /** Waits at most {@code timeout}; used for the Nope window (Req. 20). */
    Optional<Message> receive(Duration timeout) throws IOException;

    @Override
    void close() throws IOException;
}
