package main.core.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import main.core.model.CardType;
import main.core.model.Play;

/** Maps card types and combo rules to their effects (Registry); one per game, never static. */
public final class CardRegistry {
    private final Map<CardType, CardEffect> effects;
    private final List<ComboRule> combos;
    private final Map<CardType, DrawEffect> drawEffects;

    public CardRegistry() {
        this.effects = new HashMap<>();
        this.combos = new ArrayList<>();
        this.drawEffects = new HashMap<>();
    }

    public void register(CardType type, CardEffect effect) {
        throw new UnsupportedOperationException("TODO: register the effect of a single card");
    }

    public void registerCombo(ComboRule rule) {
        throw new UnsupportedOperationException("TODO: register a combo rule");
    }

    public void registerOnDraw(CardType type, DrawEffect effect) {
        throw new UnsupportedOperationException("TODO: register what happens when a type is drawn");
    }

    public CardEffect effectFor(Play play) throws InvalidMoveException {
        throw new UnsupportedOperationException("TODO: single-card effect, or the effect of the first matching combo rule");
    }

    public Optional<DrawEffect> onDrawFor(CardType type) {
        throw new UnsupportedOperationException("TODO: look up the draw effect of a type");
    }
}
