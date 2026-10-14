package main.core.players;

import java.time.Instant;

import main.core.model.Play;
import main.core.model.Player;

/** A chance to Nope a play before the deadline (rule 12, Req. 20). */
public record NopeOffer(Player actor, Play play, Instant deadline) {
}
