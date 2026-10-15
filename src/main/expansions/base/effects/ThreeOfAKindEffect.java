package main.expansions.base.effects;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import main.core.rules.CardEffect;
import main.core.rules.GameContext;
import main.core.rules.InvalidMoveException;
import main.core.model.Card;
import main.core.model.CardType;
import main.core.model.GameState;
import main.core.model.Play;
import main.core.model.Player;

/** Three of a kind: name a card type and take one from the target if they have it (rule 11g). */
public final class ThreeOfAKindEffect implements CardEffect {

    /** Asks for the target, then for a card type among every type in the game, before the Nope window. */
    @Override
    public Play prepare(GameContext ctx, Play play) throws InvalidMoveException {
        Play targeted = Targeting.withTarget(ctx, play);
        Targeting.requireTarget(ctx, targeted);
        if (targeted.namedType().isPresent()) {
            return targeted;
        }
        CardType named = ctx.controllerOf(play.player()).chooseCardTypeToName(ctx.viewFor(play.player()), typesInGame(ctx.state()));
        if (named == null) {
            throw new InvalidMoveException("Name the card type you want");
        }
        return new Play(targeted.player(), targeted.cards(), targeted.target(), Optional.of(named));
    }

    /** Moves one card of the named type if the target holds one; otherwise nothing happens. */
    @Override
    public void apply(GameContext ctx, Play play) {
        Player target = play.target().orElseThrow();
        CardType named = play.namedType().orElseThrow();
        target.hand().cards().stream()
                .filter(card -> card.type().equals(named))
                .findFirst()
                .ifPresent(card -> ctx.transfer(card, target, play.player()));
    }

    /** Every type in the hands, deck and discard pile, from any expansion, sorted by name. */
    private static List<CardType> typesInGame(GameState state) {
        Stream<Card> inHands = state.players().stream().flatMap(p -> p.hand().cards().stream());
        Stream<Card> inDeck = state.deck().peek(state.deck().size()).stream();
        Stream<Card> inDiscard = state.discard().cards().stream();
        return Stream.of(inHands, inDeck, inDiscard).flatMap(cards -> cards)
                .map(Card::type)
                .distinct()
                .sorted(Comparator.comparing(CardType::name))
                .toList();
    }
}
