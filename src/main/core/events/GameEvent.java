package main.core.events;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import main.core.model.Card;
import main.core.model.Play;
import main.core.model.Player;

/**
 * Something that happened in the game, published on the {@link EventBus}. Expansions may add their
 * own event records; the base events are nested here.
 */
public interface GameEvent {

    /** The text shown to viewers; a private event is phrased to its one viewer. */
    String describe();

    /**
     * The only player allowed to see this event.
     *
     * @return empty if the event is public
     */
    default Optional<Player> privateTo() {
        return Optional.empty();
    }

    private static String names(List<Card> cards) {
        return cards.stream().map(card -> card.type().name()).collect(Collectors.joining(" + "));
    }

    /**
     * A player's turn began (rule 9).
     *
     * @param player whose turn it is
     * @param turnsLeft turns owed including this one; above 1 under attack (rule 11a)
     */
    record TurnStarted(Player player, int turnsLeft) implements GameEvent {
        @Override
        public String describe() {
            String owed = turnsLeft > 1 ? " (" + turnsLeft + " turns left)" : "";
            return "--- " + player.name() + "'s turn" + owed + " ---";
        }
    }

    /**
     * A player played one card or a combo, announced with its target and named type before the
     * Nope window (rules 11, 12).
     *
     * @param play the completed play
     */
    record CardPlayed(Play play) implements GameEvent {
        @Override
        public String describe() {
            return play.player().name() + " played " + names(play.cards())
                    + play.target().map(t -> " on " + t.name()).orElse("")
                    + play.namedType().map(t -> ", asking for " + t.name()).orElse("");
        }
    }

    /**
     * A play was rejected; only the player who tried it is told why, and they choose again.
     *
     * @param player who tried the play
     * @param reason the message of the {@code InvalidMoveException}
     */
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

    /**
     * A Nope was played against a play; a Nope on a Nope flips it back (rule 12).
     *
     * @param by who played the Nope
     * @param play the play being Noped
     * @param noped the result after this Nope: true = cancelled, false = back on (Yup)
     */
    record PlayNoped(Player by, Play play, boolean noped) implements GameEvent {
        @Override
        public String describe() {
            return by.name() + " played NOPE: " + play.player().name() + "'s " + names(play.cards())
                    + (noped ? " is cancelled" : " is back on (Yup)");
        }
    }

    /**
     * A player drew a card into their hand; the table sees that, but not which card.
     *
     * @param player who drew
     */
    record PlayerDrew(Player player) implements GameEvent {
        @Override
        public String describe() {
            return player.name() + " drew a card";
        }
    }

    /**
     * Published with {@link PlayerDrew}; only the drawer sees which card it was.
     *
     * @param player who drew
     * @param card the card drawn
     */
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

    /**
     * A card moved from one hand to another (Favor, combos). Public, so it does not say which card.
     *
     * @param from who gave up the card
     * @param to who got it
     */
    record CardTransferred(Player from, Player to) implements GameEvent {
        @Override
        public String describe() {
            return from.name() + "'s card went to " + to.name();
        }
    }

    /**
     * The receiving side of a transfer; only the receiver sees which card they got.
     *
     * @param to who got the card, and the only one who sees this event
     * @param from who gave it up
     * @param card the card
     */
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

    /**
     * The losing side of a transfer; only the loser sees which card they lost.
     *
     * @param from who gave up the card, and the only one who sees this event
     * @param to who got it
     * @param card the card
     */
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

    /**
     * A player looked at the top of the deck; only they see the cards (rule 11e).
     *
     * @param player who played See the Future
     * @param top up to three cards, top first; empty if the deck is empty
     */
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

    /**
     * A player defused an Exploding Kitten and put it back; the position stays secret (rule 10).
     *
     * @param player who defused
     */
    record KittenDefused(Player player) implements GameEvent {
        @Override
        public String describe() {
            return player.name() + " drew an Exploding Kitten, defused it and put it back in the deck";
        }
    }

    /**
     * A player exploded and is out (rule 10).
     *
     * @param player who exploded
     */
    record PlayerExploded(Player player) implements GameEvent {
        @Override
        public String describe() {
            return player.name() + " drew an Exploding Kitten and EXPLODED!";
        }
    }

    /**
     * The game is over; told to everyone (rule 13).
     *
     * @param winner the last player alive
     */
    record GameWon(Player winner) implements GameEvent {
        @Override
        public String describe() {
            return winner.name() + " is the last player alive and wins the game!";
        }
    }
}
