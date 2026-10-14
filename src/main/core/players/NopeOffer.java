package main.core.players;

import java.time.Duration;

import main.core.model.Play;
import main.core.model.Player;

/**
 * A chance to Nope a play (rule 12, Req. 20): who acted last, whether the play is currently
 * Noped, and how long the window stays open.
 */
public record NopeOffer(Player actor, Play play, boolean currentlyNoped, Duration timeLeft) {
}
