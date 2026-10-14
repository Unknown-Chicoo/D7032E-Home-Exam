package main.core.expansion;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/** Discovers installed expansions through {@link java.util.ServiceLoader} (Plugin), sorted by {@link Expansion#order()}. */
public final class ExpansionLoader {

    /** The installed expansions; fails fast if none are registered, since a game needs cards. */
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

    public Optional<Expansion> byName(String name) {
        return available().stream().filter(e -> e.name().equalsIgnoreCase(name)).findFirst();
    }
}
