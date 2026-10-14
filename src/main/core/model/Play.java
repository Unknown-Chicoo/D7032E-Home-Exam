package main.core.model;

import java.util.List;
import java.util.Optional;

/** The player plays one card or a combo, optionally at a target and naming a card type. */
public record Play(Player player, List<Card> cards, Optional<Player> target, Optional<CardType> namedType)
        implements TurnAction {
}
