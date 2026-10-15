package main.core.expansion;

import java.util.ArrayList;
import java.util.List;

import main.core.model.Card;

/**
 * Combines the cards the chosen expansions contribute to a game (rules 2, 3, 6). In every method,
 * {@code expansions} must be sorted by {@link Expansion#order()}, {@code players} is the number of
 * seats, and the result is a new modifiable list.
 */
public final class DeckFactory {

    /** The deck before dealing (rule 3). */
    public List<Card> deckCards(List<Expansion> expansions, int players) {
        List<Card> cards = new ArrayList<>();
        expansions.forEach(e -> cards.addAll(e.createDeckCards(players)));
        return cards;
    }

    /** The cards one player gets before dealing, e.g. one Defuse (rule 2); call it once per player. */
    public List<Card> startingHand(List<Expansion> expansions, int players) {
        List<Card> cards = new ArrayList<>();
        expansions.forEach(e -> cards.addAll(e.createStartingHand(players)));
        return cards;
    }

    /** The kittens to insert after dealing: created by every expansion, then adjusted by each (rule 6). */
    public List<Card> kittens(List<Expansion> expansions, int players) {
        List<Card> kittens = new ArrayList<>();
        expansions.forEach(e -> kittens.addAll(e.createKittens(players)));
        expansions.forEach(e -> e.adjustKittens(kittens, players));
        return kittens;
    }
}
