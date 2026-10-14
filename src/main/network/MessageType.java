package main.network;

/** Kind of a network message; replaces keyword parsing of strings (Req. 17). */
public enum MessageType {
    EVENT,
    ASK_ACTION,
    ASK_TARGET,
    ASK_CARD,
    ASK_CARD_TYPE,
    ASK_POSITION,
    ASK_NOPE,
    ANSWER,
    GAME_OVER
}
