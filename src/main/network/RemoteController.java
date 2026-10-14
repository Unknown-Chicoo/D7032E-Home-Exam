package main.network;

import java.util.List;

import main.core.events.GameEvent;
import main.core.events.GameEventListener;
import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.Player;
import main.core.model.TurnAction;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.core.players.PlayerController;
import main.core.players.Question;

/** Remote proxy of a human player: decisions and events travel as typed messages (Req. 17). */
public final class RemoteController implements PlayerController, GameEventListener {
    private final MessageChannel channel;
    private final Player player;

    public RemoteController(MessageChannel channel, Player player) {
        this.channel = channel;
        this.player = player;
    }

    @Override
    public TurnAction chooseAction(GameView view) {
        throw new UnsupportedOperationException("TODO: send ASK_ACTION and map the answer (Req. 17)");
    }

    @Override
    public Player chooseTarget(GameView view, List<Player> candidates) {
        throw new UnsupportedOperationException("TODO: send ASK_TARGET and map the answer (Req. 17)");
    }

    @Override
    public Card chooseCardToGive(GameView view, Player requester) {
        throw new UnsupportedOperationException("TODO: send ASK_CARD and map the answer (Req. 17)");
    }

    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        throw new UnsupportedOperationException("TODO: send ASK_CARD_TYPE and map the answer (Req. 17)");
    }

    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        throw new UnsupportedOperationException("TODO: send ASK_POSITION and map the answer (Req. 17)");
    }

    @Override
    public int choose(GameView view, Question question) {
    // TODO(design): no MessageType for a generic Question yet; add one or reuse an ASK_* type.
        throw new UnsupportedOperationException("TODO: send the question and map the answer (Req. 17)");
    }

    @Override
    public boolean wantsToNope(GameView view, NopeOffer offer) {
        throw new UnsupportedOperationException("TODO: send ASK_NOPE, wait until the deadline (rule 12, Req. 20)");
    }

    @Override
    public void onEvent(GameEvent event) {
        throw new UnsupportedOperationException("TODO: forward visible events as EVENT messages");
    }
}
