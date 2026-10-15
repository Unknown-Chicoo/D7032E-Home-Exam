package main.core.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/** {@link Randomizer} backed by {@link java.util.Random}; always seeded, so every game can be replayed. */
public final class JavaRandomizer implements Randomizer {
    private final Random random;

    /**
     * Creates a randomizer whose sequence is fully determined by {@code seed}.
     *
     * @param seed the same seed always gives the same sequence of numbers and shuffles
     */
    public JavaRandomizer(long seed) {
        this.random = new Random(seed);
    }

    /** Delegates to {@link Random#nextInt(int)}. */
    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /** Delegates to {@link Collections#shuffle(List, Random)}. */
    @Override
    public <T> void shuffle(List<T> list) {
        Collections.shuffle(list, random);
    }
}
