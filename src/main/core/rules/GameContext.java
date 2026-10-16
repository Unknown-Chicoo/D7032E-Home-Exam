package main.core.rules;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

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

/**
 * Everything a card effect may touch: state, controllers, events and randomness. Not thread-safe:
 * use it on the game thread only.
 */
public final class GameContext {
    private final GameState state;
    private final Map<Player, PlayerController> controllers;
    private final EventBus bus;
    private final Randomizer random;

    /**
     * Creates the context for one game.
     *
     * @param controllers one controller for every player in {@code state}; copied
     * @param random the game's own randomizer, not shared with any bot
     */
    public GameContext(GameState state, Map<Player, PlayerController> controllers, EventBus bus,
            Randomizer random) {
        this.state = state;
        this.controllers = Map.copyOf(controllers);
        this.bus = bus;
        this.random = random;
    }

    /** The game's mutable state. */
    public GameState state() {
        return state;
    }

    /**
     * Who makes {@code player}'s decisions.
     *
     * @throws NullPointerException if no controller was registered for {@code player}
     */
    public PlayerController controllerOf(Player player) {
        return Objects.requireNonNull(controllers.get(player),
                () -> "No controller registered for player " + player.name());
    }

    /** A fresh immutable snapshot of what {@code player} may see, to pass to their controller. */
    public GameView viewFor(Player player) {
        Map<Player, Integer> handSizes = new LinkedHashMap<>();
        state.alivePlayers().forEach(p -> handSizes.put(p, p.hand().size()));
        TurnState turns = state.turns();
        return new GameView(player, List.copyOf(player.hand().cards()), state.deck().size(),
                Collections.unmodifiableMap(handSizes), turns.current() == player ? turns.turnsLeft() : 0);
    }

    /**
     * Asks {@code chooser} to pick a target (rules 11b, 11f, 11g). The effect decides who is eligible;
     * the context does the asking and checks the answer.
     *
     * @param eligible the effect's own condition, e.g. "holds a card"; only other living players are tested
     * @throws InvalidMoveException if nobody is eligible, or the answer is not one of the candidates;
     *         the player chooses again
     */
    public Player chooseTarget(Player chooser, Predicate<Player> eligible) throws InvalidMoveException {
        List<Player> candidates = state.alivePlayers().stream()
                .filter(p -> p != chooser && eligible.test(p))
                .toList();
        if (candidates.isEmpty()) {
            throw new InvalidMoveException("No valid target");
        }
        Player target = controllerOf(chooser).chooseTarget(viewFor(chooser), candidates);
        if (!candidates.contains(target)) {
            throw new InvalidMoveException("Choose one of the offered targets");
        }
        return target;
    }

    /**
     * Moves a card between hands (rules 11b, 11f, 11g). Publishes the public {@code CardTransferred},
     * then {@code CardLost} (private to {@code from}) and {@code CardReceived} (private to {@code to}).
     *
     * @throws IllegalArgumentException if {@code from} does not hold {@code card}
     */
    public void transfer(Card card, Player from, Player to) {
        if (!from.hand().remove(card)) {
            throw new IllegalArgumentException(from.name() + " does not hold " + card.type().name());
        }
        to.hand().add(card);
        publish(new CardTransferred(from, to));
        publish(new CardLost(from, to, card));
        publish(new CardReceived(to, from, card));
    }

    /** Sends an event to every listener on the game's bus. */
    public void publish(GameEvent event) {
        bus.publish(event);
    }

    /** The game's randomizer; the only allowed source of randomness for effects. */
    public Randomizer random() {
        return random;
    }
}
