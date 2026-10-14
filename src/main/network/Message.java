package main.network;

import java.io.Serializable;
import java.util.List;

/**
 * A typed message between server and client (Req. 17). {@code id} pairs an ANSWER with its ASK;
 * {@code seconds} is the time limit of an ASK_TIMED (Req. 20).
 */
public record Message(MessageType type, int id, String text, List<String> options, int answerIndex, int seconds)
        implements Serializable {

    public static Message ask(int id, String text, List<String> options) {
        return new Message(MessageType.ASK, id, text, List.copyOf(options), -1, 0);
    }

    public static Message askTimed(int id, String text, List<String> options, int seconds) {
        return new Message(MessageType.ASK_TIMED, id, text, List.copyOf(options), -1, seconds);
    }

    public static Message answer(int id, int index) {
        return new Message(MessageType.ANSWER, id, "", List.of(), index, 0);
    }

    public static Message event(String text) {
        return new Message(MessageType.EVENT, 0, text, List.of(), -1, 0);
    }

    public static Message gameOver(String text) {
        return new Message(MessageType.GAME_OVER, 0, text, List.of(), -1, 0);
    }
}
