package main.core.rules;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Play;
import main.core.model.Player;

class CardRegistryTest {

    private enum T implements CardType { A, B, C }

    private final CardRegistry registry = new CardRegistry();
    private final CardEffect effectA = (ctx, play) -> { };
    private final CardEffect pairEffect = (ctx, play) -> { };
    private final CardEffect anyTwoEffect = (ctx, play) -> { };

    private static Play play(CardType... types) {
        List<Card> cards = new ArrayList<>();
        for (CardType type : types) {
            cards.add(new Card(type));
        }
        return new Play(new Player("P1"), cards, Optional.empty(), Optional.empty());
    }

    @Test
    void registeringAPlayEffectTwiceThrows() {
        registry.register(T.A, effectA);
        assertThrows(IllegalArgumentException.class, () -> registry.register(T.A, effectA));
    }

    @Test
    void registeringADrawEffectTwiceThrows() {
        registry.registerOnDraw(T.A, (ctx, player, card) -> { });
        assertThrows(IllegalArgumentException.class, () -> registry.registerOnDraw(T.A, (ctx, player, card) -> { }));
    }

    @Test
    void rule11_singleCardUsesItsPlayEffect() throws InvalidMoveException {
        registry.register(T.A, effectA);
        assertSame(effectA, registry.effectFor(play(T.A)));
    }

    @Test
    void rule11fg_firstMatchingComboInRegistrationOrderWins() throws InvalidMoveException {
        registry.registerCombo(cards -> cards.size() == 2 && cards.get(0).equals(cards.get(1)), pairEffect);
        registry.registerCombo(cards -> cards.size() == 2, anyTwoEffect);
        assertSame(pairEffect, registry.effectFor(play(T.B, T.B)));
        assertSame(anyTwoEffect, registry.effectFor(play(T.B, T.C)));
    }

    @Test
    void rule11_singleCardEffectIsPreferredOverAMatchingCombo() throws InvalidMoveException {
        registry.register(T.A, effectA);
        registry.registerCombo(cards -> true, anyTwoEffect);
        assertSame(effectA, registry.effectFor(play(T.A)));
    }

    @Test
    void rule11_playWithNoEffectAndNoComboIsInvalid() {
        registry.registerCombo(cards -> cards.size() == 2 && cards.get(0).equals(cards.get(1)), pairEffect);
        assertThrows(InvalidMoveException.class, () -> registry.effectFor(play(T.C)));
        assertThrows(InvalidMoveException.class, () -> registry.effectFor(play(T.B, T.C)));
    }

    @Test
    void cardsWithoutADrawEffectGoToTheHand() {
        assertTrue(registry.onDrawFor(T.A).isEmpty());
    }

    @Test
    void decorateOnDrawWrapsTheExistingEffect() {
        List<String> calls = new ArrayList<>();
        registry.registerOnDraw(T.A, (ctx, player, card) -> calls.add("base"));
        registry.decorateOnDraw(T.A, inner -> (ctx, player, card) -> {
            calls.add("before");
            inner.onDraw(ctx, player, card);
        });
        registry.onDrawFor(T.A).orElseThrow().onDraw(null, null, null);
        assertTrue(calls.equals(List.of("before", "base")), calls.toString());
    }

    @Test
    void decorateOnDrawWithoutAnEffectThrows() {
        assertThrows(IllegalArgumentException.class, () -> registry.decorateOnDraw(T.A, inner -> inner));
    }
}
