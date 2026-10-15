package main.core.model;

import java.util.List;
import java.util.Optional;

/**
 * The player plays one card or a combo (rule 11). Controllers return it with only the cards set;
 * the effect fills in the target and named type.
 *
 * @param player the player making the play
 * @param cards the cards played, by type; one card or a combo such as a pair
 * @param target the player aimed at (Favor, two/three of a kind); empty until the effect asks, or if it has none
 * @param namedType the card type asked for (three of a kind, rule 11g); empty otherwise
 */
public record Play(Player player, List<Card> cards, Optional<Player> target, Optional<CardType> namedType)
        implements TurnAction {
}
