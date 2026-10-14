package main.core.util;

import java.time.Instant;

/** Source of the current time, injected so that the Nope window can be tested (Req. 20). */
public interface Clock {
    Instant now();
}
