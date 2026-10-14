package main.core.players;

import java.util.List;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Player;
import main.core.model.TurnAction;

/**
 * Where a player's decisions come from (Strategy). Humans, bots and remote players all look the
 * same to the engine (Req. 16, Req. 17).
 */
public interface PlayerController {
    /** Pass, or a Play of cards from the hand; targets are asked for later by the effect (rule 9). */
    TurnAction chooseAction(GameView view);

    Player chooseTarget(GameView view, List<Player> candidates);

    /** Favor: which card to hand over to the requester. */
    Card chooseCardToGive(GameView view, Player requester);

    /** Three of a kind: which card type to ask for. */
    CardType chooseCardTypeToName(GameView view, List<CardType> options);

    /** Defuse: where to put the Exploding Kitten back, 0 = top to {@code deckSize} = bottom (rule 10). */
    int chooseDeckPosition(GameView view, int deckSize);

    /** Whether to answer the offered play with a Nope (rule 12); may be called on a worker thread. */
    boolean wantsToNope(GameView view, NopeOffer offer);

    /** Answers a generic question with the index of the chosen option. */
    int choose(GameView view, Question question);
}
