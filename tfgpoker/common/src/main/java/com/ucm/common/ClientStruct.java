package com.ucm.common;

import java.net.Socket;

/**
 * Immutable data structure that represents a connected player in the system.
 * <p>
 * A {@code ClientStruct} stores the player's unique identifier, display name,
 * communication socket, and host status within a match or game session.
 * </p>
 * <p>
 * This record also provides factory methods for creating guest and host
 * players with the appropriate host flag already configured.
 * </p>
 *
 * @param playerID unique identifier of the player
 * @param name display name of the player
 * @param socket network socket associated with the player's connection
 * @param isHost indicates whether the player is the host of the session
 */
public record ClientStruct(int playerID, String name, Socket socket, boolean isHost) {
    /**
     * Creates a client representing a guest player.
     * <p>
     * The created instance has its host flag set to {@code false}.
     * </p>
     * @param playerID
     * @param name
     * @param socket
     * @return a new {@code ClientStruct} representing a guest player
     */
    public static ClientStruct createGuestPlayer(int playerID, String name, Socket socket) {
        return new ClientStruct(playerID, name, socket, false);
    }
    /**
     * Creates a client representing the host player.
     * <p>
     * The created instance has its host flag set to {@code true}.
     * </p>
     *
     * @param playerID unique identifier of the player
     * @param name display name of the player
     * @param socket network socket associated with the player's connection
     * @return a new {@code ClientStruct} representing the host player
     */
    public static ClientStruct createHostPlayer(int playerID, String name, Socket socket) {
        return new ClientStruct(playerID, name, socket, true);
    }

}