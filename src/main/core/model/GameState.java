package main.core.model;

import java.util.List;
import java.util.Optional;

/** All mutable state of one game: players, piles and turn order. */
public final class GameState {
    private final List<Player> players;
    private final Deck deck;
    private final DiscardPile discard;
    private final TurnState turns;

    public GameState(List<Player> players, Deck deck, DiscardPile discard, TurnState turns) {
        this.players = players;
        this.deck = deck;
        this.discard = discard;
        this.turns = turns;
    }

    public List<Player> players() {
        return players;
    }

    public Deck deck() {
        return deck;
    }

    public DiscardPile discard() {
        return discard;
    }

    public TurnState turns() {
        return turns;
    }

    public List<Player> alivePlayers() {
        throw new UnsupportedOperationException("TODO: players still in the game");
    }

    public Optional<Player> winner() {
        throw new UnsupportedOperationException("TODO: the last player alive, if any (rule 13)");
    }
}
