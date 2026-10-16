package main.expansions.base.events;

import main.core.events.GameEvent;

/** The deck was shuffled (rule 11c). */
public record DeckShuffled() implements GameEvent {
    @Override
    public String describe() {
        return "The deck was shuffled";
    }
}
