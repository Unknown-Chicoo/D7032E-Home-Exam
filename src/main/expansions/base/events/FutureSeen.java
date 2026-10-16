package main.expansions.base.events;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import main.core.events.GameEvent;
import main.core.model.Card;
import main.core.model.Player;

/**
 * A player looked at the top of the deck; only they see the cards (rule 11e).
 *
 * @param player who played See the Future
 * @param top up to three cards, top first; empty if the deck is empty
 */
public record FutureSeen(Player player, List<Card> top) implements GameEvent {
    @Override
    public String describe() {
        return "Top of the deck: " + (top.isEmpty() ? "(empty)"
                : top.stream().map(card -> card.type().name()).collect(Collectors.joining(" + ")));
    }

    @Override
    public Optional<Player> privateTo() {
        return Optional.of(player);
    }
}
