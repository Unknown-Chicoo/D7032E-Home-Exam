package main.core.engine;

import java.util.Comparator;
import java.util.List;

import main.core.cards.CardRegistry;
import main.core.expansion.DeckFactory;
import main.core.expansion.Expansion;
import main.core.model.Deck;
import main.core.model.DiscardPile;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.model.TurnState;
import main.core.util.Randomizer;

/** Builds the initial game state and card registry, following rules 1–8 in order. */
public final class GameSetup {
    private static final int HAND_SIZE = 7;

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
        List<Expansion> ordered = expansions.stream().sorted(Comparator.comparingInt(Expansion::order)).toList();
        int n = playerNames.size();
        int max = ordered.stream().mapToInt(Expansion::maxPlayers).max().orElse(5);
        if (n < 2 || n > max) {
            throw new IllegalArgumentException("A game needs 2-" + max + " players, not " + n + " (rule 1)");
        }
        List<Player> players = playerNames.stream().map(Player::new).toList();

        CardRegistry registry = new CardRegistry();
        ordered.forEach(e -> e.registerEffects(registry));

        for (Player player : players) {                                     // rule 2
            deckFactory.startingHand(ordered, n).forEach(player.hand()::add);
        }
        Deck deck = new Deck();
        deck.addAll(deckFactory.deckCards(ordered, n));                     // rule 3
        deck.shuffle(random);                                               // rule 4
        for (int i = 0; i < HAND_SIZE; i++) {                               // rule 5
            for (Player player : players) {
                player.hand().add(deck.drawTop());
            }
        }
        deck.addAll(deckFactory.kittens(ordered, n));                     // rule 6
        deck.shuffle(random);                                               // rule 7
        TurnState turns = new TurnState(players, random.nextInt(n));        // rule 8

        return new Result(new GameState(players, deck, new DiscardPile(), turns), registry);
    }
}
