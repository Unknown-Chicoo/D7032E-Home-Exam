package main.core.events;

import main.core.model.Play;

/**
 * A player played one card or a combo, announced with its target and named type before the
 * Nope window (rules 11, 12).
 *
 * @param play the completed play
 */
public record CardPlayed(Play play) implements GameEvent {
    @Override
    public String describe() {
        return play.player().name() + " played " + CardNames.of(play.cards())
                + play.target().map(t -> " on " + t.name()).orElse("")
                + play.namedType().map(t -> ", asking for " + t.name()).orElse("");
    }
}
