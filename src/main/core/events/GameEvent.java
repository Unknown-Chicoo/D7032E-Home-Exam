package main.core.events;

import java.util.List;
import java.util.Optional;

import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/** Something that happened in the game, published to views (Observer). Open so expansions can add events. */
public interface GameEvent {

    /** Human-readable text for views and remote clients. */
    String describe();

    /** The only player allowed to see this event, or empty if it is public. */
    default Optional<Player> privateTo() {
        return Optional.empty();
    }

    /** A player's turn began. */
    record TurnStarted(Player player, int turnsLeft) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe TurnStarted");
        }
    }

    /** A player played one or more cards. */
    record CardPlayed(Player player, List<Card> cards) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe CardPlayed");
        }
    }

    /** A play was cancelled by a Nope (rule 12). */
    record PlayNoped(Player by, Play play) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe PlayNoped");
        }
    }

    /** A player drew a card; only they see which. */
    record CardDrawn(Player player, Card card) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe CardDrawn");
        }

        @Override
        public Optional<Player> privateTo() {
            return Optional.of(player);
        }
    }

    /** A card moved from one hand to another (Favor, combos). */
    record CardTransferred(Player from, Player to, Card card) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe CardTransferred");
        }
    }

    /** A player looked at the top of the deck; only they see the cards. */
    record FutureSeen(Player player, List<Card> top) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe FutureSeen");
        }

        @Override
        public Optional<Player> privateTo() {
            return Optional.of(player);
        }
    }

    /** The deck was shuffled. */
    record DeckShuffled() implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe DeckShuffled");
        }
    }

    /** A player defused an Exploding Kitten. */
    record KittenDefused(Player player) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe KittenDefused");
        }
    }

    /** A player exploded and is out. */
    record PlayerExploded(Player player) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe PlayerExploded");
        }
    }

    /** The game is over (rule 13). */
    record GameWon(Player winner) implements GameEvent {
        @Override
        public String describe() {
            throw new UnsupportedOperationException("TODO: describe GameWon");
        }
    }
}
