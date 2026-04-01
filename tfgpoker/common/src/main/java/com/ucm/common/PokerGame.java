package com.ucm.common;

import java.io.IOError;
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

	public static boolean checkIpValid(final String IP){
		return !IP.trim().isEmpty();
	}

    public static void sendName(final String name, Socket socket) throws IOException {

        SocketUtils.sendInteger(socket.getOutputStream(), GameType.PETITION_PLAYER_NAME);
        SocketUtils.sendString(socket.getOutputStream(), name);
    }

    public static String receiveName(InputStream input, OutputStream output) throws IOException {

        String name = SocketUtils.receiveString(input);
        if(name.length() < 3) {
            SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_SHORT);
            return null;
        }
        else if(10 < name.length()) {
            SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_LONG);
            return null;
        }
        else {
            SocketUtils.sendInteger(output, GameType.CONFIRMATION_NAME_VALID);
            return name;
        }
    }


}