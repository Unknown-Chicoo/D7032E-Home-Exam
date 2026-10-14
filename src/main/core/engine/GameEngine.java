package main.core.engine;

import main.core.cards.GameContext;
import main.core.model.Player;

/** The game loop: turns until one player is left (rule 13). */
public final class GameEngine {
    private final GameContext ctx;
    private final PlayResolver playResolver;
    private final DrawResolver drawResolver;

    public GameEngine(GameContext ctx, PlayResolver playResolver, DrawResolver drawResolver) {
        this.ctx = ctx;
        this.playResolver = playResolver;
        this.drawResolver = drawResolver;
    }

    public Player run() {
        throw new UnsupportedOperationException("TODO: play turns until there is a winner (rule 13)");
    }
}
