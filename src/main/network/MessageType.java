package main.network;

/** Kind of a network message (Req. 17). */
public enum MessageType {
    /** Server to client: text to print. */
    EVENT,
    /** Server to client: a question that waits for an answer. */
    ASK,
    /** Server to client: a question with a time limit, e.g. a Nope offer. */
    ASK_TIMED,
    /** Client to server: the index chosen for a question. */
    ANSWER,
    /** Server to client: the game ended; print and quit. */
    GAME_OVER
}
