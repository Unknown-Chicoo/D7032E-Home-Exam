package main.core.model;

import java.util.List;
import java.util.Optional;

/** All mutable state of one game: players, piles and turn order. */
public final class GameState {
    private final List<Player> players;
    private final Deck deck;
    private final DiscardPile discard;
    private final TurnState turns;

    /**
     * Creates the state of a game ready to play.
     *
     * @param players every seat in seat order, including players who later explode; copied
     */
    public GameState(List<Player> players, Deck deck, DiscardPile discard, TurnState turns) {
        this.players = List.copyOf(players);
        this.deck = deck;
        this.discard = discard;
        this.turns = turns;
    }

    /** Every seat in seat order, alive or not; unmodifiable. */
    public List<Player> players() {
        return players;
    }

    /** The draw pile. */
    public Deck deck() {
        return deck;
    }

    /** The discard pile. */
    public DiscardPile discard() {
        return discard;
    }

    /** Whose turn it is and how many turns they owe. */
    public TurnState turns() {
        return turns;
    }

    /** The players who have not exploded, in seat order. */
    public List<Player> alivePlayers() {
        return players.stream().filter(Player::isAlive).toList();
    }

    /**
     * The last player alive (rule 13).
     *
     * @return empty while two or more players are alive
     */
    public Optional<Player> winner() {
        List<Player> alive = alivePlayers();
        return alive.size() == 1 ? Optional.of(alive.getFirst()) : Optional.empty();
    }
}
