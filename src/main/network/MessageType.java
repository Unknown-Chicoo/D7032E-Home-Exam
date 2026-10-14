package main.network;

/** Kind of a network message; replaces keyword parsing of strings (Req. 17). */
public enum MessageType {
    EVENT,
    ASK,
    ASK_TIMED,
    ANSWER,
    GAME_OVER
}
