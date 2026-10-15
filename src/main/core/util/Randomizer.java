package main.core.util;

import java.util.List;

/**
 * Source of all randomness in the game. Inject it instead of calling {@code new Random()}, so a game
 * can be seeded and replayed.
 */
public interface Randomizer {

    /**
     * A uniformly distributed number for picking among {@code bound} alternatives.
     *
     * @param bound the number of alternatives; must be positive
     * @return a value from 0 (inclusive) to {@code bound} (exclusive)
     */
    int nextInt(int bound);

    /**
     * Permutes {@code list} in place. The list must be modifiable.
     *
     * @param <T> the element type
     * @param list the list to shuffle
     */
    <T> void shuffle(List<T> list);
}
