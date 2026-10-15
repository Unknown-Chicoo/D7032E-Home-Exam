package main.expansions.base.effects;

import java.util.List;
import java.util.Optional;

import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Play;
import main.core.model.Player;

/** Shared target handling for effects aimed at another player (rules 11b, 11f, 11g). */
final class Targeting {

    private Targeting() {
    }

    /** Asks the player for a target if the play has none yet; fails if nobody can be targeted. */
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

    /** The target must be another living player who holds cards. */
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
