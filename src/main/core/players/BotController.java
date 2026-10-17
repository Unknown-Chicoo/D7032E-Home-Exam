package main.core.players;

import java.util.Collections;
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

/**
 * Computer player that decides only through {@link CardType} traits, so it can play any expansion's
 * cards (Req. 16).
 */
public final class BotController implements PlayerController {
    private static final int TRIPLE_CHANCE = 50;
    private static final int PAIR_CHANCE = 35;
    private static final int ACTION_CHANCE = 40;
    private static final int NOPE_CHANCE = 15;
    private static final int NOPE_CHANCE_WHEN_TARGETED = 60;

    private final Randomizer random;

    /**
     * Creates a bot with its own random stream.
     *
     * @param random must not be shared with the game or other bots, or thread timing in the Nope window changes its choices
     */
    public BotController(Randomizer random) {
        this.random = random;
    }

    /** Sometimes plays three or two of a kind of cats, or a random action card; otherwise draws. */
    @Override
    public TurnAction chooseAction(GameView view) {
        Optional<CardType> tripleType = catGroup(view.hand(), 3);
        if (tripleType.isPresent() && chance(TRIPLE_CHANCE)) {
            return play(view, Collections.nCopies(3, new Card(tripleType.get())));
        }
        Optional<CardType> pairType = catGroup(view.hand(), 2);
        if (pairType.isPresent() && chance(PAIR_CHANCE)) {
            return play(view, Collections.nCopies(2, new Card(pairType.get())));
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

    /** Names a random type from the options. */
    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        return options.get(random.nextInt(options.size()));
    }

    /** Puts the kitten back at a random position, top and bottom included. */
    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        return random.nextInt(deckSize + 1);
    }

    /** Picks a random option. */
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

    /** The first cat type held at least {@code size} times. */
    private static Optional<CardType> catGroup(List<Card> hand, int size) {
        Map<CardType, Long> cats = hand.stream()
                .map(Card::type)
                .filter(CardType::isCat)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        return cats.entrySet().stream().filter(e -> e.getValue() >= size).map(Map.Entry::getKey).findFirst();
    }

    /** A playable action card: not a cat, not a Nope, and Nopeable (which excludes Defuse). */
    private static boolean isAction(Card card) {
        CardType type = card.type();
        return !type.isCat() && !type.isNope() && type.isNopeable();
    }

    /** Lower is less useful: cat 0, action 1, Nope 2, anything else (e.g. Defuse) 3. */
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
