package main.core.model;

/**
 * A single physical card. Two cards of the same type are equal, so removing "a Nope" from a hand
 * removes any one of them.
 *
 * @param type what kind of card this is
 */
public record Card(CardType type) {
}
