package main.core.events;

import main.core.model.Player;

/**
 * A card moved from one hand to another (Favor, combos). Public, so it does not say which card.
 *
 * @param from who gave up the card
 * @param to who got it
 */
public record CardTransferred(Player from, Player to) implements GameEvent {
    @Override
    public String describe() {
        return from.name() + "'s card went to " + to.name();
    }
}
