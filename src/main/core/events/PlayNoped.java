package main.core.events;

import main.core.model.Play;
import main.core.model.Player;

/**
 * A Nope was played against a play; a Nope on a Nope flips it back (rule 12).
 *
 * @param by who played the Nope
 * @param play the play being Noped
 * @param noped the result after this Nope: true = cancelled, false = back on (Yup)
 */
public record PlayNoped(Player by, Play play, boolean noped) implements GameEvent {
    @Override
    public String describe() {
        return by.name() + " played NOPE: " + play.player().name() + "'s " + CardNames.of(play.cards())
                + (noped ? " is cancelled" : " is back on (Yup)");
    }
}
