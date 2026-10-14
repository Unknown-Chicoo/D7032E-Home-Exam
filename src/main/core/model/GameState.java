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
        this.players = List.copyOf(players);
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
        return players.stream().filter(Player::isAlive).toList();
    }

    /** The last player alive, if only one is left (rule 13). */
    public Optional<Player> winner() {
        List<Player> alive = alivePlayers();
        return alive.size() == 1 ? Optional.of(alive.get(0)) : Optional.empty();
    }
}
