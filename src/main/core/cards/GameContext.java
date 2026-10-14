package main.core.cards;

import main.core.events.GameEvent;
import main.core.model.Card;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.players.GameView;
import main.core.players.PlayerController;
import main.core.util.Randomizer;

/** Everything a card effect may touch; effects see only this, never the engine (Dependency Inversion). */
// TODO(design): Skip and Attack end the turn without a draw; decide how the engine learns that an
//  effect ended the turn (e.g. compare TurnState before/after, or a flag exposed here).
public interface GameContext {
    GameState state();

    PlayerController controllerOf(Player player);

    GameView viewFor(Player player);

    void transfer(Card card, Player from, Player to);

    void publish(GameEvent event);

    Randomizer random();
}
