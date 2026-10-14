package main.core.expansion;

import java.util.List;

import main.core.model.Card;

/** Collects the cards that the chosen expansions contribute to a game (rules 2, 3, 6). */
public final class DeckFactory {

    public List<Card> deckCards(List<Expansion> expansions, int players) {
        throw new UnsupportedOperationException("TODO: gather deck cards from all expansions (rule 2)");
    }

    public List<Card> startingCards(List<Expansion> expansions, int players) {
        throw new UnsupportedOperationException("TODO: gather starting cards from all expansions (rule 3)");
    }

    public List<Card> kittens(List<Expansion> expansions, int players) {
        throw new UnsupportedOperationException("TODO: gather kittens from all expansions (rule 6)");
    }
}
