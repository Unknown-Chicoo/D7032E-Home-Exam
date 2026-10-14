package main.core.players;

import java.util.List;

import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Player;
import main.core.model.TurnAction;
import main.core.util.Randomizer;

/** Computer player that decides only through {@link CardType} traits, never concrete card enums (Req. 16). */
public final class BotController implements PlayerController {
    private final Randomizer random;

    public BotController(Randomizer random) {
        this.random = random;
    }

    @Override
    public TurnAction chooseAction(GameView view) {
        throw new UnsupportedOperationException("TODO: bot picks a play or passes (Req. 16)");
    }

    @Override
    public Player chooseTarget(GameView view, List<Player> candidates) {
        throw new UnsupportedOperationException("TODO: bot picks a target (Req. 16)");
    }

    @Override
    public Card chooseCardToGive(GameView view, Player requester) {
        throw new UnsupportedOperationException("TODO: bot picks a card to give away (Req. 16)");
    }

    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        throw new UnsupportedOperationException("TODO: bot names a card type (Req. 16)");
    }

    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        throw new UnsupportedOperationException("TODO: bot picks where to reinsert the kitten (Req. 16)");
    }

    @Override
    public int choose(GameView view, Question question) {
        throw new UnsupportedOperationException("TODO: bot picks an option (Req. 16)");
    }

    @Override
    public boolean wantsToNope(GameView view, NopeOffer offer) {
        throw new UnsupportedOperationException("TODO: bot decides whether to Nope (rule 12, Req. 16)");
    }
}
