package main.core.expansion;

import java.util.ArrayList;
import java.util.List;

import main.core.model.Card;

/** Collects the cards that the chosen expansions contribute to a game (rules 2, 3, 6). */
public final class DeckFactory {

    /** The deck before dealing (rule 3). */
    public List<Card> deckCards(List<Expansion> expansions, int players) {
        List<Card> cards = new ArrayList<>();
        expansions.forEach(e -> cards.addAll(e.createDeckCards(players)));
        return cards;
    }

    /** Cards handed out before dealing, e.g. one Defuse each (rule 2). */
    public List<Card> startingCards(List<Expansion> expansions, int players) {
        List<Card> cards = new ArrayList<>();
        expansions.forEach(e -> cards.addAll(e.createStartingCards(players)));
        return cards;
    }

    /** The kittens to insert after dealing, after every expansion could adjust them (rule 6). */
    public List<Card> kittens(List<Expansion> expansions, int players) {
        List<Card> kittens = new ArrayList<>();
        expansions.forEach(e -> kittens.addAll(e.createKittens(players)));
        expansions.forEach(e -> e.adjustKittens(kittens, players));
        return kittens;
    }
}
