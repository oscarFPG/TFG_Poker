package com.ucm.common;

import java.net.Socket;


public record ClientStruct(String name, Socket socket, boolean isHost) {

    public static ClientStruct createGuestPlayer(String name, Socket socket) {
        return new ClientStruct(name, socket, false);
    }

    public static ClientStruct createHostPlayer(String name, Socket socket) {
        return new ClientStruct(name, socket, true);
    }

}