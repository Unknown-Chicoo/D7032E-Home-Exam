package main.core.players;

import java.util.List;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Player;
import main.core.model.TurnAction;

/**
 * Where a player's decisions come from; humans, bots and remote players implement it (Req. 16, Req. 17).
 * In every method, {@code view} is what this player may see right now. Implementations must not
 * change game state and must answer rather than throw.
 */
public interface PlayerController {

    /**
     * Called on the game thread, repeatedly during the player's turn until it ends (rule 9).
     *
     * @return a {@code Pass} to draw and end the turn, or a {@code Play} with only the cards set;
     *         the effect asks for the target later. An illegal play is rejected and this is called again.
     */
    TurnAction chooseAction(GameView view);

    /**
     * Which player to aim a Favor or two/three of a kind at (rules 11b, 11f, 11g). Called on the game
     * thread before the Nope window.
     *
     * @param candidates the other living players who hold cards; never empty
     * @return one of {@code candidates}; any other player is rejected and the turn's player chooses again
     */
    Player chooseTarget(GameView view, List<Player> candidates);

    /**
     * Which card to hand over when this player is the target of a Favor (rule 11b). Called on the game thread.
     *
     * @param requester who played the Favor
     * @return a card from {@code view.hand()}; any other card is replaced by the first card in the hand
     */
    Card chooseCardToGive(GameView view, Player requester);

    /**
     * Which card type to ask the target of a three of a kind for (rule 11g). Called on the game thread.
     *
     * @param options every card type in the game, sorted by name
     * @return one of {@code options}; null is rejected and the turn's player chooses again
     */
    CardType chooseCardTypeToName(GameView view, List<CardType> options);

    /**
     * Where to put a defused Exploding Kitten back (rule 10). Called on the game thread; the answer is not announced.
     *
     * @param deckSize the number of cards in the deck now
     * @return 0 = top to {@code deckSize} = bottom; values outside are clamped
     */
    int chooseDeckPosition(GameView view, int deckSize);

    /**
     * Whether to answer the offered play with a Nope (rule 12). Called only while the player holds
     * a Nope, and possibly on a worker thread in parallel with other players, so it must only read
     * {@code view} and {@code offer}. An answer after {@code offer.timeLeft()}, or an exception,
     * counts as "no".
     *
     * @return true to play a Nope
     */
    boolean wantsToNope(GameView view, NopeOffer offer);

    /**
     * Answers a question from an expansion that the other methods do not cover. Called on the game thread.
     *
     * @return an index into {@code question.options()}, from 0 to {@code options().size() - 1}
     */
    int choose(GameView view, Question question);
}
