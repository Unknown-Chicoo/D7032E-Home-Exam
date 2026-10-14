package main.core.engine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import main.core.cards.GameContext;
import main.core.events.GameEvent.PlayNoped;
import main.core.model.CardType;
import main.core.model.Play;
import main.core.model.Player;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.core.players.PlayerController;

/**
 * Runs the timed Nope window in rounds, asking eligible players in parallel (rule 12, Req. 20).
 * Only the calling game thread mutates state; workers only call {@code wantsToNope} on a snapshot.
 */
public final class NopeResolver {
    private final Duration window;
    private final ExecutorService pool;

    public NopeResolver(Duration window, ExecutorService pool) {
        this.window = window;
        this.pool = pool;
    }

    /** Whether the play ends up Noped; each Nope flips the result and opens a new round. */
    public boolean isNoped(GameContext ctx, Play play) {
        boolean noped = false;
        Player lastActor = play.player();
        while (true) {
            Optional<Player> by = firstToNope(ctx, new NopeOffer(lastActor, play, noped, window));
            if (by.isEmpty()) {
                return noped;
            }
            by.get().hand().removeFirstMatching(CardType::isNope).ifPresent(ctx.state().discard()::add);
            ctx.publish(new PlayNoped(by.get(), play));
            noped = !noped;
            lastActor = by.get();
        }
    }

    /** One round: the first eligible player to say yes within the window, if any. */
    private Optional<Player> firstToNope(GameContext ctx, NopeOffer offer) {
        List<Player> eligible = ctx.state().alivePlayers().stream()
                .filter(p -> p != offer.actor() && p.hand().containsMatching(CardType::isNope))
                .toList();
        if (eligible.isEmpty()) {
            return Optional.empty();
        }
        CompletionService<Optional<Player>> answers = new ExecutorCompletionService<>(pool);
        List<Future<Optional<Player>>> asked = new ArrayList<>();
        for (Player player : eligible) {
            GameView view = ctx.viewFor(player);
            PlayerController controller = ctx.controllerOf(player);
            asked.add(answers.submit(() -> controller.wantsToNope(view, offer) ? Optional.of(player) : Optional.empty()));
        }
        long deadline = System.nanoTime() + window.toNanos();
        try {
            for (int i = 0; i < eligible.size(); i++) {
                Future<Optional<Player>> answer = answers.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                if (answer == null) {
                    return Optional.empty();
                }
                Optional<Player> yes = yesOrEmpty(answer);
                if (yes.isPresent()) {
                    return yes;
                }
            }
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            asked.forEach(future -> future.cancel(true));
        }
    }

    /** A controller that fails to answer counts as "no". */
    private static Optional<Player> yesOrEmpty(Future<Optional<Player>> answer) throws InterruptedException {
        try {
            return answer.get();
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }
}
