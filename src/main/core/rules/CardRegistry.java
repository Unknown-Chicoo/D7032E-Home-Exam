package main.core.rules;

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

/**
 * Maps card types and combos to their play and draw effects; one instance per game. Expansions fill
 * it in {@code Expansion.registerEffects} during setup.
 */
public final class CardRegistry {
    private final Map<CardType, CardEffect> effects;
    private final List<Combo> combos;
    private final Map<CardType, DrawEffect> drawEffects;

    /** Creates an empty registry. */
    public CardRegistry() {
        this.effects = new HashMap<>();
        this.combos = new ArrayList<>();
        this.drawEffects = new HashMap<>();
    }

    /** A multi-card play recognized by a predicate. */
    private record Combo(Predicate<List<Card>> matches, CardEffect effect) {
    }

    /**
     * Sets what a single card of {@code type} does when played.
     *
     * @throws IllegalArgumentException if a play effect is already registered for {@code type}
     */
    public void register(CardType type, CardEffect effect) {
        if (effects.putIfAbsent(type, effect) != null) {
            throw new IllegalArgumentException("A play effect is already registered for " + type.name());
        }
    }

    /**
     * Adds a combo; combos are checked in registration order and the first match wins.
     *
     * @param matches tests the played cards, e.g. "exactly two of the same type"
     */
    public void registerCombo(Predicate<List<Card>> matches, CardEffect effect) {
        combos.add(new Combo(matches, effect));
    }

    /**
     * Sets what happens when a card of {@code type} is drawn; types without one go into the hand.
     *
     * @throws IllegalArgumentException if a draw effect is already registered for {@code type}
     */
    public void registerOnDraw(CardType type, DrawEffect effect) {
        if (drawEffects.putIfAbsent(type, effect) != null) {
            throw new IllegalArgumentException("A draw effect is already registered for " + type.name());
        }
    }

    /**
     * Replaces the draw effect registered for {@code type} with a wrapper around it, e.g. to add
     * behaviour before or after it. Call it from an expansion with a higher {@code order()} than the
     * one that registered the effect.
     *
     * @param decorator receives the current effect and returns its replacement
     * @throws IllegalArgumentException if no draw effect is registered for {@code type}
     */
    public void decorateOnDraw(CardType type, UnaryOperator<DrawEffect> decorator) {
        DrawEffect existing = drawEffects.get(type);
        if (existing == null) {
            throw new IllegalArgumentException("No draw effect to decorate for " + type.name());
        }
        drawEffects.put(type, decorator.apply(existing));
    }

    /**
     * The effect of a play (rule 11): a single card's play effect if it has one, else the first matching combo.
     *
     * @throws InvalidMoveException if neither applies, e.g. a lone cat or two different cards;
     *         the player chooses again
     */
    public CardEffect effectFor(Play play) throws InvalidMoveException {
        List<Card> cards = play.cards();
        if (cards.size() == 1 && effects.containsKey(cards.getFirst().type())) {
            return effects.get(cards.getFirst().type());
        }
        for (Combo combo : combos) {
            if (combo.matches().test(cards)) {
                return combo.effect();
            }
        }
        throw new InvalidMoveException(cards.size() == 1
                ? cards.getFirst().type().name() + " cannot be played on its own"
                : "Those cards are not a combo that can be played");
    }

    /**
     * The draw effect for a type.
     *
     * @return empty if a drawn card of this type simply goes into the hand
     */
    public Optional<DrawEffect> onDrawFor(CardType type) {
        return Optional.ofNullable(drawEffects.get(type));
    }
}
