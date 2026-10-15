package main.core.players;

import java.time.Duration;

import main.core.model.Play;
import main.core.model.Player;

/**
 * A chance to Nope a play in one round of the Nope window (rule 12, Req. 20).
 *
 * @param actor who played the play or the last Nope; never asked in this round
 * @param play the completed play, with its target and named type
 * @param currentlyNoped true if the play is cancelled right now, so a Nope would bring it back (Yup)
 * @param timeLeft how long this round stays open; a later answer counts as "no"
 */
public record NopeOffer(Player actor, Play play, boolean currentlyNoped, Duration timeLeft) {
}
