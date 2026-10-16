package main.core.engine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import main.core.rules.GameContext;
import main.core.events.PlayNoped;
import main.core.model.CardType;
import main.core.model.Play;
import main.core.model.Player;
import main.core.model.TurnState;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.core.players.PlayerController;

/** Runs the timed Nope window for a play, in rounds, asking eligible players in parallel (rule 12, Req. 20). */
public final class NopeResolver {
    private final Duration window;
    private final ExecutorService pool;

    /**
     * Creates the resolver.
     *
     * @param window how long each round stays open, e.g. 5 seconds in play and a few milliseconds in tests
     * @param pool runs the {@code wantsToNope} calls; the caller owns it and shuts it down
     */
    public NopeResolver(Duration window, ExecutorService pool) {
        this.window = window;
        this.pool = pool;
    }

    /**
     * Runs the Nope window for an already announced play. Call on the game thread, which alone changes
     * state; pool threads only call {@code wantsToNope} on a {@code GameView} snapshot. Each Nope is
     * discarded, flips the result, is published as {@code PlayNoped} and opens a new round in which its
     * player cannot Nope. Each round blocks for at most the window; returns at once if nobody holds a Nope.
     *
     * @return true if the play ends up cancelled
     */
    public boolean isNoped(GameContext ctx, Play play) {
        boolean noped = false;
        Player lastActor = play.player();
        while (true) {
            Optional<Player> by = firstToNope(ctx, new NopeOffer(lastActor, play, noped, window));
            if (by.isEmpty()) {
                return noped;
            }
            by.get().hand().removeFirstMatching(CardType::isNope).ifPresent(ctx.state().discard()::add);
            noped = !noped;
            ctx.publish(new PlayNoped(by.get(), play, noped));
            lastActor = by.get();
        }
    }

    /**
     * One round: asks every eligible player in parallel and waits for all answers or the window's end.
     * Of those who said yes, the first in turn order after the actor wins, never the fastest thread, so a
     * seeded game repeats. A late or failed answer counts as "no".
     */
    private Optional<Player> firstToNope(GameContext ctx, NopeOffer offer) {
        List<Player> eligible = othersInTurnOrder(ctx.state().turns(), offer.actor()).stream()
                .filter(p -> p.hand().containsMatching(CardType::isNope))
                .toList();
        if (eligible.isEmpty()) {
            return Optional.empty();
        }
        List<Future<Boolean>> asked = new ArrayList<>();
        for (Player player : eligible) {
            GameView view = ctx.viewFor(player);
            PlayerController controller = ctx.controllerOf(player);
            asked.add(pool.submit(() -> controller.wantsToNope(view, offer)));
        }
        long deadline = System.nanoTime() + window.toNanos();
        List<Boolean> answers = new ArrayList<>();
        try {
            for (Future<Boolean> answer : asked) {
                answers.add(answerBy(answer, deadline));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            asked.forEach(future -> future.cancel(true));
        }
        int first = answers.indexOf(true);
        return first < 0 ? Optional.empty() : Optional.of(eligible.get(first));
    }

    /** The other living players, starting with the one after {@code actor} in the direction of play. */
    private static List<Player> othersInTurnOrder(TurnState turns, Player actor) {
        List<Player> order = new ArrayList<>();
        for (Player p = turns.nextAlive(actor); p != actor; p = turns.nextAlive(p)) {
            order.add(p);
        }
        return order;
    }

    private static boolean answerBy(Future<Boolean> answer, long deadline) throws InterruptedException {
        try {
            return answer.get(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
        } catch (ExecutionException | TimeoutException e) {
            return false;
        }
    }
}
