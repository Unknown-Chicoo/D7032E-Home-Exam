package main.testing;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Pass;
import main.core.model.Play;
import main.core.model.Player;
import main.core.model.TurnAction;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.core.players.PlayerController;
import main.core.players.Question;

/**
 * Test controller that answers from queues filled by the test, and records what it was asked.
 * With no action queued it draws; with no Nope answer queued it says no; any other unscripted
 * question fails the test.
 */
public final class ScriptedController implements PlayerController {
    private final Player self;
    private final Deque<TurnAction> actions = new ArrayDeque<>();
    private final Deque<Player> targets = new ArrayDeque<>();
    private final Deque<CardType> gifts = new ArrayDeque<>();
    private final Deque<CardType> names = new ArrayDeque<>();
    private final Deque<Integer> positions = new ArrayDeque<>();
    private final Deque<Integer> choices = new ArrayDeque<>();
    private final Deque<Boolean> nopes = new ConcurrentLinkedDeque<>();
    private volatile Duration nopeDelay = Duration.ZERO;

    /** Candidate lists passed to {@link #chooseTarget}, in call order. */
    public final List<List<Player>> targetCandidates = new ArrayList<>();
    /** Options passed to {@link #chooseCardTypeToName}, in call order. */
    public final List<List<CardType>> nameOptions = new ArrayList<>();
    /** Offers passed to {@link #wantsToNope}; written from worker threads. */
    public final List<NopeOffer> nopeOffers = Collections.synchronizedList(new ArrayList<>());

    public ScriptedController(Player self) {
        this.self = self;
    }

    /** Queues a play of these cards, with no target or named type. */
    public ScriptedController plays(CardType... types) {
        List<Card> cards = new ArrayList<>();
        for (CardType type : types) {
            cards.add(new Card(type));
        }
        actions.add(new Play(self, List.copyOf(cards), Optional.empty(), Optional.empty()));
        return this;
    }

    /** Queues a draw. */
    public ScriptedController passes() {
        actions.add(new Pass(self));
        return this;
    }

    public ScriptedController targets(Player target) {
        targets.add(target);
        return this;
    }

    public ScriptedController gives(CardType type) {
        gifts.add(type);
        return this;
    }

    public ScriptedController names(CardType type) {
        names.add(type);
        return this;
    }

    public ScriptedController putsKittenAt(int position) {
        positions.add(position);
        return this;
    }

    public ScriptedController answers(int index) {
        choices.add(index);
        return this;
    }

    /** Queues one answer per Nope round this player is asked in. */
    public ScriptedController nopes(boolean... answers) {
        for (boolean answer : answers) {
            nopes.add(answer);
        }
        return this;
    }

    /** Makes every Nope answer arrive this late, e.g. to test the window. */
    public ScriptedController answersNopeAfter(Duration delay) {
        this.nopeDelay = delay;
        return this;
    }

    @Override
    public TurnAction chooseAction(GameView view) {
        TurnAction next = actions.poll();
        return next != null ? next : new Pass(self);
    }

    @Override
    public Player chooseTarget(GameView view, List<Player> candidates) {
        targetCandidates.add(List.copyOf(candidates));
        return required(targets.poll(), "chooseTarget");
    }

    @Override
    public Card chooseCardToGive(GameView view, Player requester) {
        return new Card(required(gifts.poll(), "chooseCardToGive"));
    }

    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        nameOptions.add(List.copyOf(options));
        return required(names.poll(), "chooseCardTypeToName");
    }

    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        return required(positions.poll(), "chooseDeckPosition");
    }

    @Override
    public boolean wantsToNope(GameView view, NopeOffer offer) {
        nopeOffers.add(offer);
        Boolean answer = nopes.poll();
        if (!nopeDelay.isZero()) {
            try {
                Thread.sleep(nopeDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return answer != null && answer;
    }

    @Override
    public int choose(GameView view, Question question) {
        return required(choices.poll(), "choose");
    }

    private <T> T required(T answer, String method) {
        if (answer == null) {
            throw new AssertionError(self.name() + ": unscripted call to " + method);
        }
        return answer;
    }
}
