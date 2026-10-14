package main.expansions.base.effects;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import main.core.cards.CardEffect;
import main.core.cards.GameContext;
import main.core.cards.InvalidMoveException;
import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.GameState;
import main.core.model.Play;
import main.core.model.Player;

/** Three of a kind: name a card type and take one from the target if they have it (rule 11g). */
public final class ThreeOfAKindEffect implements CardEffect {

    @Override
    public Play prepare(GameContext ctx, Play play) {
        Play targeted = Targeting.withTarget(ctx, play);
        if (targeted.namedType().isPresent() || targeted.target().isEmpty()) {
            return targeted;
        }
        CardType named = ctx.controllerOf(play.player()).chooseCardTypeToName(ctx.viewFor(play.player()), typesInGame(ctx.state()));
        return new Play(targeted.player(), targeted.cards(), targeted.target(), Optional.of(named));
    }

    @Override
    public void validate(GameContext ctx, Play play) throws InvalidMoveException {
        Targeting.requireTarget(ctx, play);
        if (play.namedType().isEmpty()) {
            throw new InvalidMoveException("Name the card type you want");
        }
    }

    @Override
    public void apply(GameContext ctx, Play play) {
        Player target = play.target().orElseThrow();
        CardType named = play.namedType().orElseThrow();
        target.hand().cards().stream()
                .filter(card -> card.type().equals(named))
                .findFirst()
                .ifPresent(card -> ctx.transfer(card, target, play.player()));
    }

    /** Every type still in play (hands and deck), from any expansion, so no hard-coded card list is needed. */
    private static List<CardType> typesInGame(GameState state) {
        Stream<Card> inHands = state.players().stream().flatMap(p -> p.hand().cards().stream());
        Stream<Card> inDeck = state.deck().peek(state.deck().size()).stream();
        return Stream.concat(inHands, inDeck)
                .map(Card::type)
                .distinct()
                .sorted(Comparator.comparing(CardType::name))
                .toList();
    }
}
