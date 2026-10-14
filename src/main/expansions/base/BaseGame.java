package main.expansions.base;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.DEFUSE;
import static main.expansions.base.BaseCardType.EXPLODING_KITTEN;
import static main.expansions.base.BaseCardType.FAVOR;
import static main.expansions.base.BaseCardType.NOPE;
import static main.expansions.base.BaseCardType.SEE_THE_FUTURE;
import static main.expansions.base.BaseCardType.SHUFFLE;
import static main.expansions.base.BaseCardType.SKIP;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import main.core.cards.CardRegistry;
import main.core.expansion.Expansion;
import main.core.model.Card;
import main.expansions.base.effects.AttackEffect;
import main.expansions.base.effects.ExplodeOrDefuseEffect;
import main.expansions.base.effects.FavorEffect;
import main.expansions.base.effects.SeeTheFutureEffect;
import main.expansions.base.effects.ShuffleEffect;
import main.expansions.base.effects.SkipEffect;
import main.expansions.base.effects.ThreeOfAKindEffect;
import main.expansions.base.effects.TwoOfAKindEffect;

/** The base game packaged as an expansion plug-in (rules 2–13). Nope is handled by the Nope window, not registered. */
public final class BaseGame implements Expansion {

    @Override
    public String name() {
        return "Base game";
    }

    @Override
    public List<Card> createDeckCards(int players) {
        List<Card> cards = new ArrayList<>();
        add(cards, DEFUSE, players <= 4 ? 2 : 1);
        add(cards, ATTACK, 4);
        add(cards, FAVOR, 4);
        add(cards, NOPE, 5);
        add(cards, SHUFFLE, 4);
        add(cards, SKIP, 4);
        add(cards, SEE_THE_FUTURE, 5);
        for (BaseCardType type : BaseCardType.values()) {
            if (type.isCat()) {
                add(cards, type, 4);
            }
        }
        return cards;
    }

    @Override
    public List<Card> createStartingCards(int players) {
        List<Card> cards = new ArrayList<>();
        add(cards, DEFUSE, players);
        return cards;
    }

    @Override
    public List<Card> createKittens(int players) {
        List<Card> cards = new ArrayList<>();
        add(cards, EXPLODING_KITTEN, players - 1);
        return cards;
    }

    @Override
    public void registerEffects(CardRegistry registry) {
        registry.register(ATTACK, new AttackEffect());
        registry.register(SKIP, new SkipEffect());
        registry.register(FAVOR, new FavorEffect());
        registry.register(SHUFFLE, new ShuffleEffect());
        registry.register(SEE_THE_FUTURE, new SeeTheFutureEffect());
        registry.registerCombo(sameType(2), new TwoOfAKindEffect());
        registry.registerCombo(sameType(3), new ThreeOfAKindEffect());
        registry.registerOnDraw(EXPLODING_KITTEN, new ExplodeOrDefuseEffect());
    }

    private static void add(List<Card> cards, BaseCardType type, int count) {
        for (int i = 0; i < count; i++) {
            cards.add(new Card(type));
        }
    }

    /** Matches exactly {@code n} cards of one type, any type (rule 11f/g). */
    private static Predicate<List<Card>> sameType(int n) {
        return cards -> cards.size() == n && cards.stream().map(Card::type).distinct().count() == 1;
    }
}
