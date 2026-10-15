package main.core.engine;

import main.core.rules.GameContext;
import main.core.rules.InvalidMoveException;
import main.core.events.GameEvent.GameWon;
import main.core.events.GameEvent.MoveRejected;
import main.core.events.GameEvent.TurnStarted;
import main.core.model.GameState;
import main.core.model.Pass;
import main.core.model.Play;
import main.core.model.Player;
import main.core.model.TurnState;

/** The game loop: runs turns until one player is left (rules 9, 13). */
public final class GameEngine {
    private final GameContext ctx;
    private final PlayResolver playResolver;
    private final DrawResolver drawResolver;

    /** Creates the engine for the game in {@code ctx}; nothing runs until {@link #run()}. */
    public GameEngine(GameContext ctx, PlayResolver playResolver, DrawResolver drawResolver) {
        this.ctx = ctx;
        this.playResolver = playResolver;
        this.drawResolver = drawResolver;
    }

    /**
     * Plays the game to the end on the calling thread, which becomes the game thread. Asks the current
     * player for actions until their turn ends, then moves on; publishes {@code GameWon} at the end (rule 13).
     *
     * @return the winner
     */
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
