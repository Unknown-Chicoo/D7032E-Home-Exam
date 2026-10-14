package main.expansions.base;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.EXPLODING_KITTEN;
import static main.expansions.base.BaseCardType.FAVOR;
import static main.expansions.base.BaseCardType.NOPE;
import static main.expansions.base.BaseCardType.SEE_THE_FUTURE;
import static main.expansions.base.BaseCardType.SHUFFLE;
import static main.expansions.base.BaseCardType.SKIP;

import java.util.List;

import main.core.cards.CardEffect;
import main.core.cards.CardRegistry;
import main.core.cards.ComboRule;
import main.core.expansion.Expansion;
import main.core.model.Card;
import main.expansions.base.effects.AttackEffect;
import main.expansions.base.effects.ExplodeOrDefuseEffect;
import main.expansions.base.effects.FavorEffect;
import main.expansions.base.effects.NopeEffect;
import main.expansions.base.effects.SeeTheFutureEffect;
import main.expansions.base.effects.ShuffleEffect;
import main.expansions.base.effects.SkipEffect;
import main.expansions.base.effects.ThreeOfAKindEffect;
import main.expansions.base.effects.TwoOfAKindEffect;

/** The base game packaged as an expansion plug-in (rules 2–13). */
public final class BaseGame implements Expansion {

    @Override
    public String name() {
        return "Base game";
    }

    @Override
    public List<Card> createDeckCards(int players) {
        throw new UnsupportedOperationException("TODO: base deck card counts (rule 2)");
    }

    @Override
    public List<Card> createStartingCards(int players) {
        throw new UnsupportedOperationException("TODO: one Defuse per player (rule 3)");
    }

    @Override
    public List<Card> createKittens(int players) {
        throw new UnsupportedOperationException("TODO: Exploding Kittens for the player count (rule 6)");
    }

    @Override
    public void registerEffects(CardRegistry registry) {
        registry.register(ATTACK, new AttackEffect());
        registry.register(SKIP, new SkipEffect());
        registry.register(FAVOR, new FavorEffect());
        registry.register(SHUFFLE, new ShuffleEffect());
        registry.register(SEE_THE_FUTURE, new SeeTheFutureEffect());
        registry.register(NOPE, new NopeEffect());
        registry.registerCombo(new OfAKind(2, new TwoOfAKindEffect()));
        registry.registerCombo(new OfAKind(3, new ThreeOfAKindEffect()));
        registry.registerOnDraw(EXPLODING_KITTEN, new ExplodeOrDefuseEffect());
    }

    /** Matches exactly {@code size} cats of the same type. */
    private record OfAKind(int size, CardEffect effect) implements ComboRule {
        @Override
        public boolean matches(List<Card> cards) {
            throw new UnsupportedOperationException("TODO: size cards, all the same cat type");
        }
    }
}
