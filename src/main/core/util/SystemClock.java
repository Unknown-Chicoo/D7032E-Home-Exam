package main.core.util;

import java.time.Instant;

/** {@link Clock} that reads the system time. */
public final class SystemClock implements Clock {
    @Override
    public Instant now() {
        throw new UnsupportedOperationException("TODO: return the current system time");
    }
}
