package main.io;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import main.core.events.GameEvent;
import main.core.model.Card;
import main.core.model.Player;
import main.core.players.GameView;
import main.core.players.NopeOffer;

/**
 * Turns events and game views into text for one viewer, applying {@link GameEvent#privateTo()}.
 * Shared by the console and remote players; every event, known or not, is shown by {@code describe()}.
 */
public final class TextRenderer {

    /** The event's text, or empty if it is private to someone other than {@code viewer}; a spectator (empty viewer) sees public events only. */
    public Optional<String> render(GameEvent event, Optional<Player> viewer) {
        Optional<Player> onlyFor = event.privateTo();
        if (onlyFor.isPresent() && !onlyFor.equals(viewer)) {
            return Optional.empty();
        }
        return Optional.of(event.describe());
    }

    public String render(GameView view) {
        String others = view.handSizes().entrySet().stream()
                .filter(e -> e.getKey() != view.self())
                .map(e -> e.getKey().name() + " (" + e.getValue() + " cards)")
                .collect(Collectors.joining(", "));
        String turns = view.turnsLeft() > 1 ? "\nTurns left: " + view.turnsLeft() : "";
        return "Your hand: " + view.hand().stream().map(this::name).collect(Collectors.joining(", "))
                + "\nDeck: " + view.deckSize() + " cards | Others: " + others + turns;
    }

    public String name(Card card) {
        return card.type().name();
    }

    /** The two answers to a Nope prompt; index 1 plays the Nope. */
    public static final List<String> NOPE_OPTIONS = List.of("Let it happen", "Play NOPE");

    public String nopePrompt(NopeOffer offer) {
        String state = offer.currentlyNoped() ? "It is currently Noped. " : "";
        return new GameEvent.CardPlayed(offer.play()).describe() + ". " + state
                + "Nope it? You have " + Math.max(1, offer.timeLeft().toSeconds()) + " seconds.";
    }

    public String favorPrompt(Player requester) {
        return requester.name() + " asked you for a Favor. Which card do you give?";
    }

    public String deckPositionPrompt(int deckSize) {
        return "Where do you put the Exploding Kitten back? Enter 0 (top) to " + deckSize + " (bottom):";
    }
}
