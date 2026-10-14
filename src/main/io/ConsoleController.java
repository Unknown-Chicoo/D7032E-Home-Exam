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

/** A local human player who answers through the console by picking numbered options (Req. 16). */
public final class ConsoleController implements PlayerController {
    private final ConsoleInput in;
    private final ConsoleOutput out;
    private final TextRenderer renderer;

    public ConsoleController(ConsoleInput in, ConsoleOutput out, TextRenderer renderer) {
        this.in = in;
        this.out = out;
        this.renderer = renderer;
    }

    @Override
    public TurnAction chooseAction(GameView view) {
        ActionMenu menu = ActionMenu.of(view);
        return menu.actions().get(ask(renderer.render(view) + "\nYour move:", menu.labels()));
    }

    @Override
    public Player chooseTarget(GameView view, List<Player> candidates) {
        return candidates.get(ask("Choose a target:", candidates.stream().map(Player::name).toList()));
    }

    @Override
    public Card chooseCardToGive(GameView view, Player requester) {
        List<String> hand = view.hand().stream().map(renderer::name).toList();
        return view.hand().get(ask(renderer.favorPrompt(requester), hand));
    }

    @Override
    public CardType chooseCardTypeToName(GameView view, List<CardType> options) {
        return options.get(ask("Name the card type you want:", options.stream().map(CardType::name).toList()));
    }

    @Override
    public int chooseDeckPosition(GameView view, int deckSize) {
        out.println(renderer.deckPositionPrompt(deckSize));
        in.discardPending();
        return in.readInt(0, deckSize);
    }

    @Override
    public int choose(GameView view, Question question) {
        return ask(question.prompt(), question.options());
    }

    /** Runs on a worker thread; answering "1" within the window plays the Nope (rule 12). */
    @Override
    public boolean wantsToNope(GameView view, NopeOffer offer) {
        out.println(renderer.nopePrompt(offer));
        out.showOptions(TextRenderer.NOPE_OPTIONS);
        in.discardPending();
        return in.readLineWithin(offer.timeLeft())
                .flatMap(line -> ConsoleInput.parseInt(line, 0, 1))
                .filter(choice -> choice == 1)
                .isPresent();
    }

    private int ask(String prompt, List<String> options) {
        out.println(prompt);
        out.showOptions(options);
        in.discardPending();
        return in.choose(options);
    }
}
