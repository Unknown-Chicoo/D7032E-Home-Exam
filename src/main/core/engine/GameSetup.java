package main.core.engine;

import java.util.List;

import main.core.cards.CardRegistry;
import main.core.expansion.DeckFactory;
import main.core.expansion.Expansion;
import main.core.model.GameState;
import main.core.util.Randomizer;

/** Builds the initial game state and card registry (rules 1–8). */
public final class GameSetup {
    private final DeckFactory deckFactory;
    private final Randomizer random;

    public GameSetup(DeckFactory deckFactory, Randomizer random) {
        this.deckFactory = deckFactory;
        this.random = random;
    }

    /** The prepared state and the registry wired by the expansions. */
    public record Result(GameState state, CardRegistry registry) {
    }

    public Result setUp(List<Expansion> expansions, List<String> playerNames) {
        throw new UnsupportedOperationException("TODO: deal hands, build deck, pick start player (rules 1-8)");
    }
}
