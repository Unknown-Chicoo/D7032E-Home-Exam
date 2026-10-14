package main.core.expansion;

import java.util.List;
import java.util.Optional;

/** Discovers installed expansions through {@link java.util.ServiceLoader}. */
public final class ExpansionLoader {

    public List<Expansion> available() {
        throw new UnsupportedOperationException("TODO: ServiceLoader.load(Expansion.class)");
    }

    public Optional<Expansion> byName(String name) {
        throw new UnsupportedOperationException("TODO: find an available expansion by name");
    }
}
