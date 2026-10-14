package main.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The single reader of the console input stream. {@link #readLoop()} moves lines into a queue;
 * it starts no thread itself, {@code Main} submits the loop to the shared executor (Req. 20).
 */
public final class ConsoleInput {
    private static final long CLOSED_CHECK_MILLIS = 200;

    private final BufferedReader reader;
    private final ConsoleOutput out;
    private final BlockingQueue<String> lines;
    private volatile boolean closed;

    /** {@code out} is only used to re-ask after invalid input. */
    public ConsoleInput(InputStream in, ConsoleOutput out) {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.out = out;
        this.lines = new LinkedBlockingQueue<>();
    }

    /** Reads lines into the queue until the stream ends. */
    public void readLoop() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.put(line);
            }
        } catch (IOException e) {
            // treated like end of input
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            closed = true;
        }
    }

    /** Blocks for the next line; throws {@link IllegalStateException} once the input has ended. */
    public String readLine() {
        try {
            while (true) {
                String line = lines.poll(CLOSED_CHECK_MILLIS, TimeUnit.MILLISECONDS);
                if (line != null) {
                    return line;
                }
                if (closed && lines.isEmpty()) {
                    throw new IllegalStateException("Console input closed");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for input", e);
        }
    }

    /** The next line, or empty if none arrives within {@code timeout} or the wait is interrupted (rule 12). */
    public Optional<String> readLineWithin(Duration timeout) {
        try {
            return Optional.ofNullable(lines.poll(timeout.toMillis(), TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    /** Drops lines typed before the current prompt, e.g. a late answer to a closed Nope window. */
    public void discardPending() {
        lines.clear();
    }

    /** Reads until a whole number in [min, max] is entered, re-asking after anything else. */
    public int readInt(int min, int max) {
        while (true) {
            Optional<Integer> value = parseInt(readLine(), min, max);
            if (value.isPresent()) {
                return value.get();
            }
            out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    public int choose(List<String> options) {
        return readInt(0, options.size() - 1);
    }

    /** The line as a whole number in [min, max], if it is one. */
    public static Optional<Integer> parseInt(String line, int min, int max) {
        try {
            int value = Integer.parseInt(line.trim());
            return value >= min && value <= max ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
