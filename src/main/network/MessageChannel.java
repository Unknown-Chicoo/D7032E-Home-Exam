package main.network;

import java.time.Duration;
import java.util.Optional;

/** A two-way connection that carries {@link Message}s. */
// TODO(design): decide how I/O failures surface (checked IOException vs. UncheckedIOException),
//  and whether close() should narrow AutoCloseable's "throws Exception".
public interface MessageChannel extends AutoCloseable {
    void send(Message message);

    Message receive();

    /** Waits at most {@code timeout}; used for the Nope window (Req. 20). */
    Optional<Message> receive(Duration timeout);
}
