package main.core.engine;

import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.events.GameEvent.GameWon;
import main.core.events.GameEvent.MoveRejected;
import main.core.events.GameEvent.TurnStarted;
import main.core.model.GameState;
import main.core.model.Pass;
import main.core.model.Play;
import main.core.model.Player;
import main.core.model.TurnState;

/** The game loop: each turn, plays until the turn ends, until one player is left (rules 9, 13). */
public final class GameEngine {
    private final GameContext ctx;
    private final PlayResolver playResolver;
    private final DrawResolver drawResolver;

    public GameEngine(GameContext ctx, PlayResolver playResolver, DrawResolver drawResolver) {
        this.ctx = ctx;
        this.playResolver = playResolver;
        this.drawResolver = drawResolver;
    }

    public Player run() {
        GameState state = ctx.state();
        TurnState turns = state.turns();
        while (state.winner().isEmpty()) {
            Player player = turns.current();
            int turnAtStart = turns.turnNumber();
            ctx.publish(new TurnStarted(player, turns.turnsLeft()));
            while (turns.turnNumber() == turnAtStart && player.isAlive()) {
                switch (ctx.controllerOf(player).chooseAction(ctx.viewFor(player))) {
                    case Pass pass -> drawResolver.draw(ctx, player);
                    case Play play -> resolve(player, play);
                }
            }
        }
        Player winner = state.winner().orElseThrow();
        ctx.publish(new GameWon(winner));
        return winner;
    }

    /** An illegal play is reported to the player, who then chooses again (rule 9). */
    private void resolve(Player player, Play play) {
        try {
            if (play.player() != player) {
                throw new InvalidMoveException("It is not " + play.player().name() + "'s turn");
            }
            playResolver.resolve(ctx, play);
        } catch (InvalidMoveException e) {
            ctx.publish(new MoveRejected(player, e.getMessage()));
        }
    }
}
