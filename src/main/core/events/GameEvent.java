package main.core.events;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static String names(List<Card> cards) {
        return cards.stream().map(card -> card.type().name()).collect(Collectors.joining(" + "));
    }

    /** A player's turn began. */
    record TurnStarted(Player player, int turnsLeft) implements GameEvent {
        @Override
        public String describe() {
            String owed = turnsLeft > 1 ? " (" + turnsLeft + " turns left)" : "";
            return "--- " + player.name() + "'s turn" + owed + " ---";
        }
    }

    /** A player played one card or a combo, announced with its target and named type (rule 11). */
    record CardPlayed(Play play) implements GameEvent {
        @Override
        public String describe() {
            return play.player().name() + " played " + names(play.cards())
                    + play.target().map(t -> " on " + t.name()).orElse("")
                    + play.namedType().map(t -> ", asking for " + t.name()).orElse("");
        }
    }

    /** A play was rejected; only the player who tried it is told why. */
    record MoveRejected(Player player, String reason) implements GameEvent {
        @Override
        public String describe() {
            return "Not allowed: " + reason;
        }

        @Override
        public Optional<Player> privateTo() {
            return Optional.of(player);
        }
    }

    /** A Nope was played against a play; a Nope on a Nope flips it back (rule 12). {@code noped} is the result. */
    record PlayNoped(Player by, Play play, boolean noped) implements GameEvent {
        @Override
        public String describe() {
            return by.name() + " played NOPE: " + play.player().name() + "'s " + names(play.cards())
                    + (noped ? " is cancelled" : " is back on (Yup)");
        }
    }

    /** A player drew a card into their hand; the table sees that, but not which card. */
    record PlayerDrew(Player player) implements GameEvent {
        @Override
        public String describe() {
            return player.name() + " drew a card";
        }
    }

    /** A player drew a card; only they see which. */
    record CardDrawn(Player player, Card card) implements GameEvent {
        @Override
        public String describe() {
            return "You drew " + card.type().name();
        }

        @Override
        public Optional<Player> privateTo() {
            return Optional.of(player);
        }
    }

    /** A card moved from one hand to another (Favor, combos); which card stays hidden from the table. */
    record CardTransferred(Player from, Player to) implements GameEvent {
        @Override
        public String describe() {
            return from.name() + "'s card went to " + to.name();
        }
    }

    /** The receiving side of a transfer; only the receiver sees which card they got. */
    record CardReceived(Player to, Player from, Card card) implements GameEvent {
        @Override
        public String describe() {
            return "You got " + card.type().name() + " from " + from.name();
        }

        @Override
        public Optional<Player> privateTo() {
            return Optional.of(to);
        }
    }

    /** The losing side of a transfer; only the loser sees which card they lost. */
    record CardLost(Player from, Player to, Card card) implements GameEvent {
        @Override
        public String describe() {
            return "You lost " + card.type().name() + " to " + to.name();
        }

        @Override
        public Optional<Player> privateTo() {
            return Optional.of(from);
        }
    }

    /** A player looked at the top of the deck; only they see the cards (rule 11e). */
    record FutureSeen(Player player, List<Card> top) implements GameEvent {
        @Override
        public String describe() {
            return "Top of the deck: " + (top.isEmpty() ? "(empty)" : names(top));
        }

        @Override
        public Optional<Player> privateTo() {
            return Optional.of(player);
        }
    }

    /** The deck was shuffled (rule 11c). */
    record DeckShuffled() implements GameEvent {
        @Override
        public String describe() {
            return "The deck was shuffled";
        }
    }

    /** A player defused an Exploding Kitten and put it back (rule 10). */
    record KittenDefused(Player player) implements GameEvent {
        @Override
        public String describe() {
            return player.name() + " drew an Exploding Kitten, defused it and put it back in the deck";
        }
    }

    /** A player exploded and is out (rule 10). */
    record PlayerExploded(Player player) implements GameEvent {
        @Override
        public String describe() {
            return player.name() + " drew an Exploding Kitten and EXPLODED!";
        }
    }

    /** The game is over (rule 13). */
    record GameWon(Player winner) implements GameEvent {
        @Override
        public String describe() {
            return winner.name() + " is the last player alive and wins the game!";
        }
    }
}
