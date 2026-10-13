package main.rules;

import main.GameContext;

/** Defines what a card or combination of cards does whn played */
@FunctionalInterface
public interface CardEffect {

    /** Run the effect */
    void apply(GameContext ctx); // Needs to decide if player should be passed into this
}
