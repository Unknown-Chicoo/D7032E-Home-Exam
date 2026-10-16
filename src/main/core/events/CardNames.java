package main.core.events;

import java.util.List;
import java.util.stream.Collectors;

import main.core.model.Card;

/** Formats a list of cards for event text, e.g. "TacoCat + TacoCat". */
final class CardNames {

    private CardNames() {
    }

    static String of(List<Card> cards) {
        return cards.stream().map(card -> card.type().name()).collect(Collectors.joining(" + "));
    }
}
