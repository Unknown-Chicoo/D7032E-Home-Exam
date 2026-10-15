package main.network;

import java.io.Serializable;
import java.util.List;

/**
 * A typed message between server and client (Req. 17); create it with the factory methods.
 *
 * @param type what kind of message this is
 * @param id pairs an ANSWER with its ASK or ASK_TIMED; 0 for EVENT and GAME_OVER
 * @param text the text to print; empty for ANSWER
 * @param options the numbered choices of an ASK or ASK_TIMED; empty otherwise
 * @param answerIndex the chosen index in an ANSWER; -1 otherwise
 * @param seconds the time limit of an ASK_TIMED in whole seconds (Req. 20); 0 otherwise
 */
public record Message(MessageType type, int id, String text, List<String> options, int answerIndex, int seconds)
        implements Serializable {

    /** A question the client must answer; {@code options} is copied. */
    public static Message ask(int id, String text, List<String> options) {
        return new Message(MessageType.ASK, id, text, List.copyOf(options), -1, 0);
    }

    /** A question with a time limit in whole seconds; no answer in time means "no" (rule 12). */
    public static Message askTimed(int id, String text, List<String> options, int seconds) {
        return new Message(MessageType.ASK_TIMED, id, text, List.copyOf(options), -1, seconds);
    }

    /** The client's answer to question {@code id}, as an index into its options. */
    public static Message answer(int id, int index) {
        return new Message(MessageType.ANSWER, id, "", List.of(), index, 0);
    }

    /** Text to print, e.g. a rendered game event. */
    public static Message event(String text) {
        return new Message(MessageType.EVENT, 0, text, List.of(), -1, 0);
    }

    /** The last message: the client prints it and quits. */
    public static Message gameOver(String text) {
        return new Message(MessageType.GAME_OVER, 0, text, List.of(), -1, 0);
    }
}
