package main.network;

import static main.expansions.base.BaseCardType.ATTACK;
import static main.expansions.base.BaseCardType.TACOCAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import main.core.events.CardDrawn;
import main.core.events.PlayerDrew;
import main.core.model.Card;
import main.core.model.Pass;
import main.core.model.Play;
import main.core.model.Player;
import main.core.players.GameView;
import main.core.players.NopeOffer;
import main.io.TextRenderer;
import main.testing.InMemoryChannel;
import main.testing.RecordingListener;
import main.testing.ScriptedController;
import main.testing.Table;

/** The server side of a remote seat, driven through an in-memory channel (Req. 17, Req. 20). */
class RemoteControllerTest {
    private final Player me = new Player("P1");
    private final Player p2 = new Player("P2");
    private final Player p3 = new Player("P3");
    private final InMemoryChannel channel = new InMemoryChannel();
    private final ScriptedController fallback = new ScriptedController(me);
    private final RecordingListener announced = new RecordingListener();
    private final RemoteController remote = new RemoteController(channel, me, new TextRenderer(), fallback, announced);

    private GameView view(List<Card> hand) {
        return new GameView(me, hand, 10, Map.of(me, hand.size(), p2, 3, p3, 2), 1);
    }

    private NopeOffer offer() {
        return new NopeOffer(p2, new Play(p2, Table.cards(ATTACK), Optional.empty(), Optional.empty()), false,
                Duration.ofMillis(50));
    }

    @Test
    void req17_targetAnswerIsMappedToTheCandidateAtThatIndex() {
        channel.reply(Message.answer(1, 1));
        assertSame(p3, remote.chooseTarget(view(List.of()), List.of(p2, p3)));
        Message ask = channel.sent().getFirst();
        assertEquals(MessageType.ASK, ask.type());
        assertEquals(1, ask.id());
        assertEquals(List.of("P2", "P3"), ask.options());
    }

    @Test
    void req17_actionAnswerIsMappedThroughTheActionMenu() {
        List<Card> hand = Table.cards(ATTACK, TACOCAT, TACOCAT);
        channel.reply(Message.answer(1, 3)).reply(Message.answer(2, 0));
        // Menu: 0 draw, 1 ATTACK, 2 TACOCAT, 3 two TACOCAT.
        Play pair = assertInstanceOf(Play.class, remote.chooseAction(view(hand)));
        assertEquals(Table.cards(TACOCAT, TACOCAT), pair.cards());
        assertSame(me, pair.player());
        assertInstanceOf(Pass.class, remote.chooseAction(view(hand)));
    }

    @Test
    void req17_staleOrOutOfRangeAnswersAreIgnored() {
        channel.reply(Message.answer(7, 0))        // another question's id
                .reply(Message.answer(1, 5))       // out of range
                .reply(Message.event("noise"))     // not an answer
                .reply(Message.answer(1, 0));
        assertSame(p2, remote.chooseTarget(view(List.of()), List.of(p2, p3)));
    }

    @Test
    void rule12_nopeOfferIsATimedQuestionAndAnswerOneMeansNope() {
        channel.reply(Message.answer(1, 1));
        assertTrue(remote.wantsToNope(view(List.of()), offer()));
        assertEquals(MessageType.ASK_TIMED, channel.sent().getFirst().type());
        channel.reply(Message.answer(2, 0));
        assertFalse(remote.wantsToNope(view(List.of()), offer()));
    }

    @Test
    void rule12_noAnswerWithinTheWindowCountsAsNo() {
        assertFalse(remote.wantsToNope(view(List.of()), offer()));
    }

    @Test
    void rule12_lateAnswerToAnOldNopeOfferIsNotTakenAsTheNextAnswer() {
        assertFalse(remote.wantsToNope(view(List.of()), offer()));   // question 1 times out
        channel.reply(Message.answer(1, 1)).reply(Message.answer(2, 0));
        assertSame(p2, remote.chooseTarget(view(List.of()), List.of(p2, p3)));
    }

    @Test
    void req17_onlyEventsThisPlayerMaySeeAreSent() {
        remote.onEvent(new PlayerDrew(p2));
        remote.onEvent(new CardDrawn(p2, new Card(ATTACK)));
        remote.onEvent(new CardDrawn(me, new Card(TACOCAT)));
        List<Message> sent = channel.sent();
        assertEquals(2, sent.size());
        assertTrue(sent.stream().allMatch(m -> m.type() == MessageType.EVENT));
        assertTrue(sent.get(1).text().contains("TACOCAT"));
        assertTrue(sent.stream().noneMatch(m -> m.text().contains("ATTACK")));
    }

    @Test
    void req17_disconnectHandsTheSeatToTheFallbackAndIsAnnouncedOnce() {
        channel.breakConnection();
        fallback.targets(p3).targets(p2);
        assertSame(p3, remote.chooseTarget(view(List.of()), List.of(p2, p3)));
        assertSame(p2, remote.chooseTarget(view(List.of()), List.of(p2, p3)));
        remote.onEvent(new PlayerDrew(p2));
        List<PlayerDisconnected> disconnects = announced.of(PlayerDisconnected.class);
        assertEquals(1, disconnects.size());
        assertSame(me, disconnects.getFirst().player());
    }

    @Test
    void req17_failureOnANopeWorkerIsAnnouncedLaterOnTheGameThread() {
        channel.breakConnection();
        fallback.nopes(true);
        assertTrue(remote.wantsToNope(view(List.of()), offer()), "the fallback answered");
        assertTrue(announced.all().isEmpty(), "worker threads do not publish");
        remote.onEvent(new PlayerDrew(p2));
        assertEquals(1, announced.of(PlayerDisconnected.class).size());
    }

    @Test
    void rule13_gameOverIsSentAndTheChannelClosed() {
        remote.gameOver("P2 wins");
        Message last = channel.sent().getLast();
        assertEquals(MessageType.GAME_OVER, last.type());
        assertEquals("P2 wins", last.text());
        assertTrue(channel.isClosed());
    }
}
