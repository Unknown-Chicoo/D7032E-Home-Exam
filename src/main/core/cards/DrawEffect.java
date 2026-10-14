package main.core.cards;

import main.core.model.Card;
import main.core.model.Player;

/** What happens when a card is drawn instead of going into the hand (e.g. Exploding Kitten). */
public interface DrawEffect {
    void onDraw(GameContext ctx, Player player, Card card);
}
