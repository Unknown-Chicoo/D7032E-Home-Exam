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
 * The single reader of the console input stream: {@link #readLoop()} queues the lines and the read
 * methods take them. Create only one per program.
 */
public final class ConsoleInput {
    private static final long CLOSED_CHECK_MILLIS = 200;

    private final BufferedReader reader;
    private final ConsoleOutput out;
    private final BlockingQueue<String> lines;
    private volatile boolean closed;

    /**
     * Wraps the input stream; nothing is read until {@link #readLoop()} runs.
     *
     * @param out only used to re-ask after invalid input
     */
    public ConsoleInput(InputStream in, ConsoleOutput out) {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.out = out;
        this.lines = new LinkedBlockingQueue<>();
    }

    /**
     * Reads lines into the queue until the stream ends or fails, then marks the input closed. Blocks,
     * so the caller must run it on its own thread, e.g. by submitting it to the executor (Req. 20).
     */
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

    /**
     * Blocks for the next line.
     *
     * @throws IllegalStateException once the input has ended and every line was read, or if interrupted
     */
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

    /**
     * Waits a limited time for the next line, for the Nope prompt (rule 12).
     *
     * @return empty if no line arrives within {@code timeout} or the wait is interrupted
     */
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

    /**
     * Reads until a whole number in [min, max] is entered, re-asking after anything else.
     *
     * @throws IllegalStateException if the input ends first
     */
    public int readInt(int min, int max) {
        while (true) {
            Optional<Integer> value = parseInt(readLine(), min, max);
            if (value.isPresent()) {
                return value.get();
            }
            out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    /**
     * Reads the index of one of {@code options}, re-asking until it is valid.
     *
     * @return 0 to {@code options.size() - 1}
     * @throws IllegalStateException if the input ends first
     */
    public int choose(List<String> options) {
        return readInt(0, options.size() - 1);
    }

    /**
     * Parses a line typed by the player; surrounding spaces are ignored.
     *
     * @return the number, or empty if the line is not a whole number in [min, max]
     */
    public static Optional<Integer> parseInt(String line, int min, int max) {
        try {
            int value = Integer.parseInt(line.trim());
            return value >= min && value <= max ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
