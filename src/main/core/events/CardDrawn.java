package main.core.events;

import java.util.Optional;

import main.core.model.Card;
import main.core.model.Player;

/**
 * Published with {@link PlayerDrew}; only the drawer sees which card it was.
 *
 * @param player who drew
 * @param card the card drawn
 */
public record CardDrawn(Player player, Card card) implements GameEvent {
    @Override
    public String describe() {
        return "You drew " + card.type().name();
    }

    @Override
    public Optional<Player> privateTo() {
        return Optional.of(player);
    }
}
