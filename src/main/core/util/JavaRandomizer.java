package main.core.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/** {@link Randomizer} backed by {@link java.util.Random}. */
public final class JavaRandomizer implements Randomizer {
    private final Random random;

    public JavaRandomizer() {
        this.random = new Random();
    }

    public JavaRandomizer(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public <T> void shuffle(List<T> list) {
        Collections.shuffle(list, random);
    }
}
