package main.core.expansion;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Finds the installed expansions with {@link java.util.ServiceLoader}: the classes listed in
 * {@code META-INF/services/main.core.expansion.Expansion} on the classpath.
 */
public final class ExpansionLoader {

    /**
     * The installed expansions, sorted by {@link Expansion#order()}; each call creates new instances.
     *
     * @throws IllegalStateException if none are registered, i.e. the {@code resources} folder is not on
     *         the classpath
     */
    public List<Expansion> available() {
        List<Expansion> expansions = ServiceLoader.load(Expansion.class).stream()
                .map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt(Expansion::order))
                .toList();
        if (expansions.isEmpty()) {
            throw new IllegalStateException("No expansions found: put the resources folder "
                    + "(META-INF/services/" + Expansion.class.getName() + ") on the classpath");
        }
        return expansions;
    }

    /**
     * The installed expansion with this name, ignoring case.
     *
     * @return empty if none matches
     * @throws IllegalStateException if no expansions are installed at all
     */
    public Optional<Expansion> byName(String name) {
        return available().stream().filter(e -> e.name().equalsIgnoreCase(name)).findFirst();
    }
}
