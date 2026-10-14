package main.core.engine;

import java.util.Map;

import main.core.cards.GameContext;
import main.core.events.EventBus;
import main.core.events.GameEvent;
import main.core.model.Card;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.players.GameView;
import main.core.players.PlayerController;
import main.core.util.Randomizer;

/** The engine's implementation of {@link GameContext}, handed to card effects. */
public final class DefaultGameContext implements GameContext {
    private final GameState state;
    private final Map<Player, PlayerController> controllers;
    private final EventBus bus;
    private final Randomizer random;

    public DefaultGameContext(GameState state, Map<Player, PlayerController> controllers, EventBus bus,
            Randomizer random) {
        this.state = state;
        this.controllers = controllers;
        this.bus = bus;
        this.random = random;
    }

    @Override
    public GameState state() {
        return state;
    }

    @Override
    public PlayerController controllerOf(Player player) {
        throw new UnsupportedOperationException("TODO: look up the controller of a player");
    }

    @Override
    public GameView viewFor(Player player) {
        throw new UnsupportedOperationException("TODO: build the view one player may see");
    }

    @Override
    public void transfer(Card card, Player from, Player to) {
        throw new UnsupportedOperationException("TODO: move a card between hands and publish it");
    }

    @Override
    public void publish(GameEvent event) {
        throw new UnsupportedOperationException("TODO: forward the event to the bus");
    }

    @Override
    public Randomizer random() {
        return random;
    }
}
