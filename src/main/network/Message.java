package main.network;

import java.io.Serializable;
import java.util.List;

/** A typed message between server and client (Req. 17). */
public record Message(MessageType type, String text, List<String> options, int answerIndex) implements Serializable {

    public static Message ask(MessageType type, String text, List<String> options) {
        return new Message(type, text, List.copyOf(options), -1);
    }

    public static Message answer(int index) {
        return new Message(MessageType.ANSWER, "", List.of(), index);
    }

    public static Message event(String text) {
        return new Message(MessageType.EVENT, text, List.of(), -1);
    }
}
