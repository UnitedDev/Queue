package fr.kohei.queue.shared.jedis;

public enum JedisAction {

    ADD_SERVER,
    REMOVE_SERVER,
    LIST,
    UPDATE,
    TOGGLE,
    CLEAR_PLAYERS,
    ADD_PLAYER,
    ADDED_PLAYER,
    REMOVE_PLAYER,
    REMOVED_PLAYER,
    SEND_PLAYER_SERVER,
    SEND_PLAYER_HUB,
    MESSAGE_PLAYER

}
