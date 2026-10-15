package main.expansions.base.effects;

import java.util.List;
import java.util.Optional;

import main.core.rules.GameContext;
import main.core.rules.InvalidMoveException;
import main.core.model.Play;
import main.core.model.Player;

/**
 * Shared target handling for effects aimed at another player (rules 11b, 11f, 11g). Candidates are
 * the other living players who hold at least one card.
 */
final class Targeting {

    private Targeting() {
    }

    /**
     * Asks the player for a target if the play has none yet.
     *
     * @return the play with a target, which {@link #requireTarget} must still check
     * @throws InvalidMoveException if nobody can be targeted; the player chooses again
     */
    static Play withTarget(GameContext ctx, Play play) throws InvalidMoveException {
        List<Player> candidates = candidates(ctx, play.player());
        if (candidates.isEmpty()) {
            throw new InvalidMoveException("No other player has any cards");
        }
        if (play.target().isPresent()) {
            return play;
        }
        Player target = ctx.controllerOf(play.player()).chooseTarget(ctx.viewFor(play.player()), candidates);
        return new Play(play.player(), play.cards(), Optional.of(target), play.namedType());
    }

    /**
     * Checks that the play's target is one of the candidates.
     *
     * @throws InvalidMoveException if it is missing or not a candidate; the player chooses again
     */
    static void requireTarget(GameContext ctx, Play play) throws InvalidMoveException {
        Optional<Player> target = play.target();
        if (target.isEmpty() || !candidates(ctx, play.player()).contains(target.get())) {
            throw new InvalidMoveException("Choose another living player who has cards");
        }
    }

    private static List<Player> candidates(GameContext ctx, Player player) {
        return ctx.state().alivePlayers().stream()
                .filter(p -> p != player && p.hand().size() > 0)
                .toList();
    }
}
