package com.ucm.common;

import java.net.Socket;


public record ClientStruct(int playerID, String name, Socket socket, boolean isHost) {

    public static ClientStruct createGuestPlayer(int playerID, String name, Socket socket) {
        return new ClientStruct(playerID, name, socket, false);
    }

    public static ClientStruct createHostPlayer(int playerID, String name, Socket socket) {
        return new ClientStruct(playerID, name, socket, true);
    }

}