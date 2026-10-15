package main.core.players;

import java.util.List;

/**
 * A multiple-choice question an expansion asks through {@link PlayerController#choose}.
 *
 * @param prompt the text shown to the player
 * @param options the answers; the player's choice is returned as an index into this list
 */
public record Question(String prompt, List<String> options) {
}
