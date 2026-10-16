package main.io;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import main.core.events.CardPlayed;
import main.core.events.GameEvent;
import main.core.model.Card;
import main.core.model.Player;
import main.core.players.GameView;
import main.core.players.NopeOffer;

/**
 * Turns events, game views and prompts into text for one viewer. Stateless, so one instance can be
 * shared.
 */
public final class TextRenderer {

    /**
     * The event's {@code describe()} text, for any event including an expansion's.
     *
     * @param viewer the player reading; empty for a spectator, who sees public events only
     * @return empty if {@link GameEvent#privateTo()} names someone other than {@code viewer}
     */
    public Optional<String> render(GameEvent event, Optional<Player> viewer) {
        Optional<Player> onlyFor = event.privateTo();
        if (onlyFor.isPresent() && !onlyFor.equals(viewer)) {
            return Optional.empty();
        }
        return Optional.of(event.describe());
    }

    /** The player's hand, the deck size, the other players' hand sizes and, under attack, the turns left. */
    public String render(GameView view) {
        String others = view.handSizes().entrySet().stream()
                .filter(e -> e.getKey() != view.self())
                .map(e -> e.getKey().name() + " (" + e.getValue() + " cards)")
                .collect(Collectors.joining(", "));
        String turns = view.turnsLeft() > 1 ? "\nTurns left: " + view.turnsLeft() : "";
        return "Your hand: " + view.hand().stream().map(this::name).collect(Collectors.joining(", "))
                + "\nDeck: " + view.deckSize() + " cards | Others: " + others + turns;
    }

    /** The card's display name. */
    public String name(Card card) {
        return card.type().name();
    }

    /** The two answers to a Nope prompt; index 1 plays the Nope. */
    public static final List<String> NOPE_OPTIONS = List.of("Let it happen", "Play NOPE");

    /** Describes the play, whether it is currently Noped, and the whole seconds left (at least 1). */
    public String nopePrompt(NopeOffer offer) {
        String state = offer.currentlyNoped() ? "It is currently Noped. " : "";
        return new CardPlayed(offer.play()).describe() + ". " + state
                + "Nope it? You have " + Math.max(1, offer.timeLeft().toSeconds()) + " seconds.";
    }

    /** Asks the Favor target which card to give {@code requester}. */
    public String favorPrompt(Player requester) {
        return requester.name() + " asked you for a Favor. Which card do you give?";
    }

    /** Asks where to put a defused kitten, from 0 (top) to {@code deckSize} (bottom). */
    public String deckPositionPrompt(int deckSize) {
        return "Where do you put the Exploding Kitten back? Enter 0 (top) to " + deckSize + " (bottom):";
    }
}
