package main.network;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

/**
 * A two-way connection that carries {@link Message}s between server and client. Implementations must
 * be thread-safe: calls come from the game thread and from Nope worker threads.
 */
public interface MessageChannel extends AutoCloseable {

    /**
     * Sends one message, in order with earlier ones.
     *
     * @throws IOException if the connection is lost
     */
    void send(Message message) throws IOException;

    /**
     * Blocks until the next message arrives.
     *
     * @throws IOException if the connection is lost or the other side sends something that is not a {@code Message}
     */
    Message receive() throws IOException;

    /**
     * Waits at most {@code timeout} for the next message; used for the Nope window (Req. 20). Must
     * never consume part of a message on timeout, and should return promptly when the thread is interrupted.
     *
     * @return empty if no message arrived in time or the wait was interrupted
     * @throws IOException if the connection is lost
     */
    Optional<Message> receive(Duration timeout) throws IOException;

    /**
     * Closes the connection; a blocked {@code receive} on the other side then fails.
     *
     * @throws IOException if closing fails
     */
    @Override
    void close() throws IOException;
}
