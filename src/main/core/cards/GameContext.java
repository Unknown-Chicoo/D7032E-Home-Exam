package main.core.cards;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import main.core.events.EventBus;
import main.core.events.GameEvent;
import main.core.events.GameEvent.CardLost;
import main.core.events.GameEvent.CardReceived;
import main.core.events.GameEvent.CardTransferred;
import main.core.model.Card;
import main.core.model.GameState;
import main.core.model.Player;
import main.core.model.TurnState;
import main.core.players.GameView;
import main.core.players.PlayerController;
import main.core.util.Randomizer;

/** Everything a card effect may touch; effects see only this, never the engine. Used only on the game thread. */
public final class GameContext {
    private final GameState state;
    private final Map<Player, PlayerController> controllers;
    private final EventBus bus;
    private final Randomizer random;

    public GameContext(GameState state, Map<Player, PlayerController> controllers, EventBus bus,
            Randomizer random) {
        this.state = state;
        this.controllers = Map.copyOf(controllers);
        this.bus = bus;
        this.random = random;
    }

    public GameState state() {
        return state;
    }

    public PlayerController controllerOf(Player player) {
        return Objects.requireNonNull(controllers.get(player),
                () -> "No controller registered for player " + player.name());
    }

    public GameView viewFor(Player player) {
        Map<Player, Integer> handSizes = new LinkedHashMap<>();
        state.alivePlayers().forEach(p -> handSizes.put(p, p.hand().size()));
        TurnState turns = state.turns();
        return new GameView(player, List.copyOf(player.hand().cards()), state.deck().size(),
                Collections.unmodifiableMap(handSizes), turns.current() == player ? turns.turnsLeft() : 0);
    }

    public void transfer(Card card, Player from, Player to) {
        if (!from.hand().remove(card)) {
            throw new IllegalArgumentException(from.name() + " does not hold " + card.type().name());
        }
        to.hand().add(card);
        publish(new CardTransferred(from, to));
        publish(new CardLost(from, to, card));
        publish(new CardReceived(to, from, card));
    }

    public void publish(GameEvent event) {
        bus.publish(event);
    }

    public Randomizer random() {
        return random;
    }
}
