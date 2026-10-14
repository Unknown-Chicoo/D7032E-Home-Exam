package main.core.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Play;

/** Maps card types and combos to their effects (Registry); one per game, never static. */
public final class CardRegistry {
    private final Map<CardType, CardEffect> effects;
    private final List<Combo> combos;
    private final Map<CardType, DrawEffect> drawEffects;

    public CardRegistry() {
        this.effects = new HashMap<>();
        this.combos = new ArrayList<>();
        this.drawEffects = new HashMap<>();
    }

    /** A multi-card play recognised by a predicate. */
    private record Combo(Predicate<List<Card>> matches, CardEffect effect) {
    }

    public void register(CardType type, CardEffect effect) {
        if (effects.putIfAbsent(type, effect) != null) {
            throw new IllegalArgumentException("A play effect is already registered for " + type.name());
        }
    }

    /** Combos are checked in registration order. */
    public void registerCombo(Predicate<List<Card>> matches, CardEffect effect) {
        combos.add(new Combo(matches, effect));
    }

    public void registerOnDraw(CardType type, DrawEffect effect) {
        if (drawEffects.putIfAbsent(type, effect) != null) {
            throw new IllegalArgumentException("A draw effect is already registered for " + type.name());
        }
    }

    /** Lets a later expansion wrap an already registered draw effect. */
    public void decorateOnDraw(CardType type, UnaryOperator<DrawEffect> decorator) {
        DrawEffect existing = drawEffects.get(type);
        if (existing == null) {
            throw new IllegalArgumentException("No draw effect to decorate for " + type.name());
        }
        drawEffects.put(type, decorator.apply(existing));
    }

    /** The single-card effect, else the first matching combo (rule 11). */
    public CardEffect effectFor(Play play) throws InvalidMoveException {
        List<Card> cards = play.cards();
        if (cards.size() == 1 && effects.containsKey(cards.get(0).type())) {
            return effects.get(cards.get(0).type());
        }
        for (Combo combo : combos) {
            if (combo.matches().test(cards)) {
                return combo.effect();
            }
        }
        throw new InvalidMoveException(cards.size() == 1
                ? cards.get(0).type().name() + " cannot be played on its own"
                : "Those cards are not a combo that can be played");
    }

    public Optional<DrawEffect> onDrawFor(CardType type) {
        return Optional.ofNullable(drawEffects.get(type));
    }
}
