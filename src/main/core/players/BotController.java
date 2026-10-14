package main.core.players;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Pass;
import main.core.model.Play;
import main.core.model.Player;
import main.core.model.TurnAction;
import main.core.util.Randomizer;

/** Computer player that decides only through {@link CardType} traits, never concrete card enums (Req. 16). */
public final class BotController implements PlayerController {
    private static final int PAIR_CHANCE = 35;
    private static final int ACTION_CHANCE = 40;
    private static final int NOPE_CHANCE = 15;
    private static final int NOPE_CHANCE_WHEN_TARGETED = 60;

    private final Randomizer random;

    public BotController(Randomizer random) {
        this.random = random;
    }

    /** Sometimes plays a pair of cats or an action card; otherwise draws. */
    @Override
    public TurnAction chooseAction(GameView view) {
        Optional<CardType> pairType = catPair(view.hand());
        if (pairType.isPresent() && chance(PAIR_CHANCE)) {
            Card cat = new Card(pairType.get());
            return play(view, List.of(cat, cat));
        }
        List<Card> actions = view.hand().stream().filter(BotController::isAction).toList();
        if (!actions.isEmpty() && chance(ACTION_CHANCE)) {
            return play(view, List.of(actions.get(random.nextInt(actions.size()))));
        }
        return new Pass(view.self());
    }

    /** Targets the player with the most cards. */
    @Override
    public Player chooseTarget(GameView view, List<Player> candidates) {
        return candidates.stream()
                .max(Comparator.comparingInt(p -> view.handSizes().getOrDefault(p, 0)))
                .orElseThrow();
    }

    /** Gives away the least useful card: a cat, then an action, then a Nope, then anything else. */
    @Override
    public Card chooseCardToGive(GameView view, Player requester) {
        return view.hand().stream().min(Comparator.comparingInt(BotController::usefulness)).orElseThrow();
    }

    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        return options.get(random.nextInt(options.size()));
    }

    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        return random.nextInt(deckSize + 1);
    }

    @Override
    public int choose(GameView view, Question question) {
        return random.nextInt(question.options().size());
    }

    /** Defends its own plays (Yup); otherwise Nopes now and then, more often when targeted. */
    @Override
    public boolean wantsToNope(GameView view, NopeOffer offer) {
        if (offer.play().player() == view.self()) {
            return offer.currentlyNoped();
        }
        if (offer.currentlyNoped()) {
            return false;
        }
        boolean targeted = offer.play().target().filter(t -> t == view.self()).isPresent();
        return chance(targeted ? NOPE_CHANCE_WHEN_TARGETED : NOPE_CHANCE);
    }

    private boolean chance(int percent) {
        return random.nextInt(100) < percent;
    }

    private static Play play(GameView view, List<Card> cards) {
        return new Play(view.self(), cards, Optional.empty(), Optional.empty());
    }

    private static Optional<CardType> catPair(List<Card> hand) {
        Map<CardType, Long> cats = hand.stream()
                .map(Card::type)
                .filter(CardType::isCat)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        return cats.entrySet().stream().filter(e -> e.getValue() >= 2).map(Map.Entry::getKey).findFirst();
    }

    /** A playable action card: not a cat, not a Nope, and Nopeable (which excludes Defuse). */
    private static boolean isAction(Card card) {
        CardType type = card.type();
        return !type.isCat() && !type.isNope() && type.isNopeable();
    }

    private static int usefulness(Card card) {
        if (card.type().isCat()) {
            return 0;
        }
        if (isAction(card)) {
            return 1;
        }
        return card.type().isNope() ? 2 : 3;
    }
}
