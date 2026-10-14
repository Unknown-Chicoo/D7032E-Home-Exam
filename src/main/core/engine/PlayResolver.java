package main.core.engine;

import main.core.cards.CardRegistry;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Play;

/** Resolves one play: find effect → validate → Nope window → apply (rule 11). */
public final class PlayResolver {
    private final CardRegistry registry;
    private final NopeResolver nopeResolver;

    public PlayResolver(CardRegistry registry, NopeResolver nopeResolver) {
        this.registry = registry;
        this.nopeResolver = nopeResolver;
    }

    public void resolve(GameContext ctx, Play play) throws InvalidMoveException {
        throw new UnsupportedOperationException("TODO: effect, validate, Nope window, apply (rule 11)");
    }
}
