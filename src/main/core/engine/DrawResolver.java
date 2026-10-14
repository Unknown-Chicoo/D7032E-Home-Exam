package main.core.engine;

import main.core.cards.CardRegistry;
import main.core.cards.GameContext;
import main.core.model.Player;

/** Draws the top card and either triggers its draw effect or puts it in the hand (rule 10). */
public final class DrawResolver {
    private final CardRegistry registry;

    public DrawResolver(CardRegistry registry) {
        this.registry = registry;
    }

    public void draw(GameContext ctx, Player player) {
        throw new UnsupportedOperationException("TODO: draw, then draw effect or hand (rule 10)");
    }
}
