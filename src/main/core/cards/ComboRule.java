package main.core.cards;

import java.util.List;

import main.core.model.Card;

/** Recognises a multi-card combo and supplies its effect. */
public interface ComboRule {
    boolean matches(List<Card> cards);

    CardEffect effect();
}
