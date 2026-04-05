package com.ucm.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class PokerGame {

    
    public static final String LOCAL_HOST = "localhost"; 

    
    private PokerGame() {}



    public static Socket connect(final String serverIP) throws IOException {

		Socket socket = new Socket(serverIP, GameType.PORT);
		return socket;
	}

    public static void sendName(final String name, Socket socket) throws IOException {

        SocketUtils.sendInteger(socket.getOutputStream(), GameType.PETITION_PLAYER_NAME);
        SocketUtils.sendString(socket.getOutputStream(), name);
    }

    public static String receiveName(InputStream input, OutputStream output) throws IOException {

        String name = SocketUtils.receiveString(input);
        return name;
    }

    public static void sendGameConfig(GameConfig config, OutputStream out) throws IOException {
    
        SocketUtils.sendInteger(out, GameType.PETITION_CREATE_GAME);
        SocketUtils.sendString(out, config._roomName);
        // TODO
        // Aqui habria que enviar los demas datos...
        // Id, numero de jugadores, etc...
    }

    public static GameConfig receiveGameConfig(InputStream input, OutputStream output) throws IOException {

        String name = SocketUtils.receiveString(input);
        //int roomNameCode = GameConfig.checkRoomName();

        GameConfig config = new GameConfig();
        config._roomName = name;

        return config;
    }


    /* Auxiliar methods */
    public static boolean checkIpValid(final String IP){
		return !IP.trim().isEmpty();
	}

    public static boolean checkNameIsTooShort(final String name) {
        return name.length() < 3;
    }

    public static boolean checkNameIsTooLong(final String name) {
        return 10 < name.length();
    }

}