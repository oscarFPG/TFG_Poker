package com.ucm.client;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;



public class ClientActions {

    private static Socket socket;
    private static SocketChannel channel;

    public static void init(Socket s, SocketChannel ch) {
        socket = s;
        channel = ch;
    }

    public static void sendCreateGame() throws IOException {
        ClientMainGUI.sendPetition(GameType.CREATE_PETITION, channel);
    }

    public static void sendJoinGame() throws IOException {
        ClientMainGUI.sendPetition(GameType.JOIN_PETITION, channel);
    }

    public static void sendStartGame() throws IOException {
        ClientMainGUI.sendPetition(GameType.HOST_START_GAME_PETITION, channel);
    }

    // ACCIONES
    public static void fold() throws IOException {
        SocketUtils.sendString(socket.getOutputStream(), "fold");
    }

    public static void call() throws IOException {
        SocketUtils.sendString(socket.getOutputStream(), "call");
    }

    public static void check() throws IOException {
        SocketUtils.sendString(socket.getOutputStream(), "check");
    }

    public static void allIn() throws IOException {
        SocketUtils.sendString(socket.getOutputStream(), "allin");
    }

    public static void raise(int amount) throws IOException {
        SocketUtils.sendString(socket.getOutputStream(), "raise " + amount);
    }
}