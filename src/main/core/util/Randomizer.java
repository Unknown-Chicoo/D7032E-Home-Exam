package main.core.util;

import java.util.List;

/** Source of randomness, injected so that games can be seeded and tested. */
public interface Randomizer {
    int nextInt(int bound);

    <T> void shuffle(List<T> list);
}
