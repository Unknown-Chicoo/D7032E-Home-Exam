package main.core.engine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import main.core.cards.GameContext;
import main.core.events.EventBus;
import main.core.events.GameEvent;
import main.core.events.GameEvent.CardTransferred;
import main.core.model.Card;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.model.TurnState;
import main.core.players.GameView;
import main.core.players.PlayerController;
import main.core.util.Randomizer;

/** The engine's implementation of {@link GameContext}, handed to card effects; used only on the game thread. */
public final class DefaultGameContext implements GameContext {
    private final GameState state;
    private final Map<Player, PlayerController> controllers;
    private final EventBus bus;
    private final Randomizer random;

    public DefaultGameContext(GameState state, Map<Player, PlayerController> controllers, EventBus bus,
            Randomizer random) {
        this.state = state;
        this.controllers = Map.copyOf(controllers);
        this.bus = bus;
        this.random = random;
    }

    @Override
    public GameState state() {
        return state;
    }

    @Override
    public PlayerController controllerOf(Player player) {
        return controllers.get(player);
    }

    @Override
    public GameView viewFor(Player player) {
        Map<Player, Integer> handSizes = new LinkedHashMap<>();
        state.alivePlayers().forEach(p -> handSizes.put(p, p.hand().size()));
        TurnState turns = state.turns();
        return new GameView(player, List.copyOf(player.hand().cards()), state.deck().size(),
                Collections.unmodifiableMap(handSizes), turns.current() == player ? turns.turnsLeft() : 0);
    }

    @Override
    public void transfer(Card card, Player from, Player to) {
        if (!from.hand().remove(card)) {
            throw new IllegalArgumentException(from.name() + " does not hold " + card.type().name());
        }
        to.hand().add(card);
        publish(new CardTransferred(from, to, card));
    }

    @Override
    public void publish(GameEvent event) {
        bus.publish(event);
    }

    @Override
    public Randomizer random() {
        return random;
    }
}
