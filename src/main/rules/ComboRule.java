package main.rules;

import main.Card;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Defined what effect a combination of cads should have*/
public record ComboRule(String name, int size, Predicate<List<Card>> requirement, CardEffect effect) {

    public ComboRule {
        Objects.requireNonNull(name);
        Objects.requireNonNull(requirement);
        Objects.requireNonNull(effect);

        if (size < 2) throw new IllegalArgumentException("A combo needs at least 2 cards: " + name);
    }

    public boolean isSatisfiedBy(List<Card> cards) {
        return cards.size() == size && requirement.test(cards);
    }
}
