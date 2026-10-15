package main.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Pass;
import main.core.model.Play;
import main.core.model.TurnAction;
import main.core.players.GameView;

/**
 * A player's numbered choices on their turn: draw, or play one, two or three cards of a type held
 * (rules 9, 11). It does not check legality; the engine rejects illegal plays.
 *
 * @param labels the text of each choice; index 0 is always "Draw a card"
 * @param actions the action for each label, at the same index
 */
public record ActionMenu(List<String> labels, List<TurnAction> actions) {

    private static final String[] COUNT_WORDS = {"", "", "two ", "three "};

    /** Builds the menu from {@code view.hand()}, card types in the order they first appear in the hand. */
    public static ActionMenu of(GameView view) {
        List<String> labels = new ArrayList<>();
        List<TurnAction> actions = new ArrayList<>();
        labels.add("Draw a card (ends your turn)");
        actions.add(new Pass(view.self()));
        Map<CardType, Integer> counts = new LinkedHashMap<>();
        view.hand().forEach(card -> counts.merge(card.type(), 1, Integer::sum));
        counts.forEach((type, held) -> {
            for (int n = 1; n <= Math.min(held, 3); n++) {
                labels.add("Play " + COUNT_WORDS[n] + type.name());
                actions.add(new Play(view.self(), Collections.nCopies(n, new Card(type)), Optional.empty(), Optional.empty()));
            }
        });
        return new ActionMenu(List.copyOf(labels), List.copyOf(actions));
    }
}
