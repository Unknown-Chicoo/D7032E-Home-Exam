package main.core.engine;

import java.util.Comparator;
import java.util.List;

import main.core.rules.CardRegistry;
import main.core.expansion.DeckFactory;
import main.core.expansion.Expansion;
import main.core.model.Deck;
import main.core.model.DiscardPile;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.model.TurnState;
import main.core.util.Randomizer;

/** Builds the initial game state and card registry from the chosen expansions, following rules 1–8 in order. */
public final class GameSetup {
    private static final int HAND_SIZE = 7;

    private final DeckFactory deckFactory;
    private final Randomizer random;

    /**
     * Creates the setup.
     *
     * @param random the game's randomizer, used for both shuffles and the start player
     */
    public GameSetup(DeckFactory deckFactory, Randomizer random) {
        this.deckFactory = deckFactory;
        this.random = random;
    }

    /**
     * The prepared game.
     *
     * @param state the dealt state with the kittens shuffled in and a random start player
     * @param registry the effects registered by the expansions, in {@code order()} order
     */
    public record Result(GameState state, CardRegistry registry) {
    }

    /**
     * Sets up a new game.
     *
     * @param expansions the chosen expansions, in any order; sorted here by {@link Expansion#order()}
     * @param playerNames one name per seat, in seat order
     * @throws IllegalArgumentException if there are fewer than 2 players or more than the highest
     *         {@link Expansion#maxPlayers()} allows (rule 1), or if two expansions register the same type
     */
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
