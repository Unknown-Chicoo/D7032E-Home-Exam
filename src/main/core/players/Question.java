package main.core.players;

import java.util.List;

/** A generic multiple-choice question, so expansions can ask for decisions the core does not know. */
public record Question(String prompt, List<String> options) {
}
