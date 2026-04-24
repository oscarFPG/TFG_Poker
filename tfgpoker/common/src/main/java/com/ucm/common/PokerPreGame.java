package com.ucm.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class PokerPreGame {

    public static final String LOCAL_HOST = "localhost"; 


    private PokerPreGame() {}


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
        int allowBotsCode = (config._allowBots) ? GameType.TRUE : GameType.FALSE;

        int dinamicBlinds = (config._dinamicBlinds) ? GameType.TRUE : GameType.FALSE;
        SocketUtils.sendString(out, config._roomName);
        SocketUtils.sendString(out, config._userName);
        SocketUtils.sendInteger(out, config._initialMoney);
        SocketUtils.sendInteger(out, allowBotsCode);
        SocketUtils.sendString(out, config._blindsValue);
        SocketUtils.sendInteger(out, dinamicBlinds);
        SocketUtils.sendString(out, config._levelDuration);
        SocketUtils.sendString(out, config._hikePercentage);
        SocketUtils.sendString(out, config._turnTimerPlayer);
        SocketUtils.sendInteger(out, config._numBots1);
        SocketUtils.sendInteger(out, config._numBots2);
        SocketUtils.sendInteger(out, config._numPlayers);
        SocketUtils.sendString(out, config._selectedTable);
        SocketUtils.sendString(out, config._selectedCard);
    }

    public static GameConfig receiveGameConfig(InputStream input, OutputStream output) throws IOException {

        String roomName = SocketUtils.receiveString(input);
        String userName = SocketUtils.receiveString(input);
        int initialMoney = SocketUtils.receiveInt(input);
        boolean allowBots = (SocketUtils.receiveInt(input) == GameType.TRUE) ? true : false;
        String blindsValue = SocketUtils.receiveString(input);
        boolean dinamicBlinds = (SocketUtils.receiveInt(input) == GameType.TRUE) ? true : false;
        String levelDuration = SocketUtils.receiveString(input);
        String hikePercentage = SocketUtils.receiveString(input);
        String turnTimer = SocketUtils.receiveString(input);
        int numBots1 = SocketUtils.receiveInt(input);
        int numBots2 = SocketUtils.receiveInt(input);
        int numPlayers = SocketUtils.receiveInt(input);
        String selectedTable = SocketUtils.receiveString(input);
        String selectedCard = SocketUtils.receiveString(input);

        GameConfig config = new GameConfig();

        config._roomName = roomName;
        config._userName = userName;
        config._initialMoney = initialMoney;
        config._allowBots = allowBots;
        config._blindsValue = blindsValue;
        config._dinamicBlinds = dinamicBlinds;
        config._levelDuration = levelDuration;
        config._hikePercentage = hikePercentage;
        config._turnTimerPlayer = turnTimer;
        config._numBots1 = numBots1;
        config._numBots2 = numBots2;
        config._numPlayers = numPlayers;
        config._selectedTable = selectedTable;
        config._selectedCard = selectedCard;

        return config;
    }

    public static void sendGameConfigToJoinedPlayer(GameConfig config, OutputStream out) throws IOException {

        SocketUtils.sendInteger(out, config._roomId);
        int allowBotsCode = (config._allowBots) ? GameType.TRUE : GameType.FALSE;
        int dinamicBlinds = (config._dinamicBlinds) ? GameType.TRUE : GameType.FALSE;
        SocketUtils.sendString(out, config._roomName);
        SocketUtils.sendString(out, config._userName);
        SocketUtils.sendInteger(out, config._initialMoney);
        SocketUtils.sendInteger(out, allowBotsCode);
        SocketUtils.sendString(out, config._blindsValue);
        SocketUtils.sendInteger(out, dinamicBlinds);
        SocketUtils.sendString(out, config._levelDuration);
        SocketUtils.sendString(out, config._hikePercentage);
        SocketUtils.sendInteger(out, config._numBots1);
        SocketUtils.sendInteger(out, config._numBots2);
        SocketUtils.sendInteger(out, config._numPlayers);
        SocketUtils.sendString(out, config._selectedTable);
        SocketUtils.sendString(out, config._selectedCard);
    }

    public static GameConfig receiveGameConfigAsJoinedPlayer(InputStream input, OutputStream output) throws IOException {

        int roomId = SocketUtils.receiveInt(input);
        String roomName = SocketUtils.receiveString(input);
        String userName = SocketUtils.receiveString(input);
        int initialMoney = SocketUtils.receiveInt(input);
        boolean allowBots = (SocketUtils.receiveInt(input) == GameType.TRUE) ? true : false;
        String blindsValue = SocketUtils.receiveString(input);
        boolean dinamicBlinds = (SocketUtils.receiveInt(input) == GameType.TRUE) ? true : false;
        String levelDuration = SocketUtils.receiveString(input);
        String hikePercentage = SocketUtils.receiveString(input);
        int numBots1 = SocketUtils.receiveInt(input);
        int numBots2 = SocketUtils.receiveInt(input);
        int numPlayers = SocketUtils.receiveInt(input);
        String selectedTable = SocketUtils.receiveString(input);
        String selectedCard = SocketUtils.receiveString(input);

        GameConfig config = new GameConfig();

        config._roomId = roomId;
        config._roomName = roomName;
        config._userName = userName;
        config._initialMoney = initialMoney;
        config._allowBots = allowBots;
        config._blindsValue = blindsValue;
        config._dinamicBlinds = dinamicBlinds;
        config._levelDuration = levelDuration;
        config._hikePercentage = hikePercentage;
        config._numBots1 = numBots1;
        config._numBots2 = numBots2;
        config._numPlayers = numPlayers;
        config._selectedTable = selectedTable;
        config._selectedCard = selectedCard;

        return config;
    }

    public static void sendWaitingRoomConfirmation(Socket socket) throws IOException {
        SocketUtils.sendInteger(socket.getOutputStream(), GameType.CONFIRMATION_WAITING_GAME);
    }

    public static int receiveWaitingRoomConfirmation(Socket socket) throws IOException {
       
        int response = SocketUtils.receiveInt(socket.getInputStream());
        return response;
    }

    public static void sendPlayerInRoomInfo(PlayerInfo p, Socket socket) throws IOException {

        SocketUtils.sendInteger(socket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
        SocketUtils.sendInteger(socket.getOutputStream(), p.id);
        SocketUtils.sendString(socket.getOutputStream(), p.name);
    }

    public static PlayerInfo receivePlayerInRoomInfo(InputStream input, OutputStream output) throws IOException {

        int response = SocketUtils.receiveInt(input);
        if(response != GameType.EVENT_PLAYER_JOINED) {
            return null;
        }
        
        
        int id = SocketUtils.receiveInt(input);
        String name = SocketUtils.receiveString(input);

        return new PlayerInfo(id, name);
    }

    public static List<PlayerInfo> receivePlayerListWaiting(InputStream input, OutputStream output) throws IOException {

        List<PlayerInfo> playerPositions = new ArrayList<>();
        int numPlayers = SocketUtils.receiveInt(input);

        for(int i = 0; i < numPlayers; i++) {
            PlayerInfo p = PokerPreGame.receivePlayerInRoomInfo(input, output);
            playerPositions.add( p );
        }

        return playerPositions;
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