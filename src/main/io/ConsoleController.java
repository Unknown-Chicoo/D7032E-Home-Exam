package main.io;

import java.util.List;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Player;
import main.core.model.TurnAction;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.core.players.PlayerController;
import main.core.players.Question;

/** A local human player who answers through the console (Req. 16). */
public final class ConsoleController implements PlayerController {
    private final ConsoleInput in;
    private final ConsoleOutput out;

    public ConsoleController(ConsoleInput in, ConsoleOutput out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public TurnAction chooseAction(GameView view) {
        throw new UnsupportedOperationException("TODO: ask the human for a play or pass");
    }

    @Override
    public Player chooseTarget(GameView view, List<Player> candidates) {
        throw new UnsupportedOperationException("TODO: ask the human for a target");
    }

    @Override
    public Card chooseCardToGive(GameView view, Player requester) {
        throw new UnsupportedOperationException("TODO: ask the human which card to give");
    }

    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        throw new UnsupportedOperationException("TODO: ask the human to name a card type");
    }

    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        throw new UnsupportedOperationException("TODO: ask the human where to put the kitten");
    }

    @Override
    public int choose(GameView view, Question question) {
        throw new UnsupportedOperationException("TODO: ask the human to pick an option");
    }

    @Override
    public boolean wantsToNope(GameView view, NopeOffer offer) {
        throw new UnsupportedOperationException("TODO: ask the human whether to Nope (rule 12)");
    }
}
