package com.ucm.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        SocketUtils.sendString(out, config._roomName);
        SocketUtils.sendString(out, config._userName);
        SocketUtils.sendInteger(out, config._initialMoney);
        SocketUtils.sendInteger(out, config._allowBots ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendString(out, config._blindsValue);
        SocketUtils.sendInteger(out, config._dinamicBlinds ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendString(out, config._levelDuration);
        SocketUtils.sendString(out, config._hikePercentage);
        SocketUtils.sendString(out, config._turnTimerPlayer);

        // Number of bots instances by type
        SocketUtils.sendInteger(out, config._botsByType.size());
        for(var entry : config._botsByType.entrySet()) {
            SocketUtils.sendInteger(out, entry.getKey());
            SocketUtils.sendInteger(out, entry.getValue());
        }

        // Styled bots amount
        SocketUtils.sendInteger(out, config._botStylesByType.size());
        for(var entry : config._botStylesByType.entrySet()) {

            Integer botID = entry.getKey();
            Map<BotStyle, Integer> dist = entry.getValue();
            
            // Bot ID and style amount for the current bot
            SocketUtils.sendInteger(out, botID);
            SocketUtils.sendInteger(out, dist.size());

            for(var d : dist.entrySet()) {
                BotStyle st = d.getKey();
                Integer val = d.getValue();

                // Style identifier and number of instances
                SocketUtils.sendInteger(out, st.ordinal());
                SocketUtils.sendInteger(out, val);
            }
        }

        SocketUtils.sendInteger(out, config._numPlayers);
        SocketUtils.sendString(out, config._selectedTable);
        SocketUtils.sendString(out, config._selectedCard);
        SocketUtils.sendInteger(out, config._joinedAsSpectator ? GameType.TRUE : GameType.FALSE);
    }

    public static GameConfig receiveGameConfig(InputStream input, OutputStream output) throws IOException {

        String roomName = SocketUtils.receiveString(input);
        String userName = SocketUtils.receiveString(input);
        int initialMoney = SocketUtils.receiveInt(input);
        boolean allowBots = SocketUtils.receiveInt(input) == GameType.TRUE;
        String blindsValue = SocketUtils.receiveString(input);
        boolean dinamicBlinds = SocketUtils.receiveInt(input) == GameType.TRUE;
        String levelDuration = SocketUtils.receiveString(input);
        String hikePercentage = SocketUtils.receiveString(input);
        String turnTimer = SocketUtils.receiveString(input);

        // Receive number of bot types
        int botTypes = SocketUtils.receiveInt(input);
        Map<Integer, Integer> botsByType = new HashMap<>();
        for(int i = 0; i < botTypes; i++) {
            int botId = SocketUtils.receiveInt(input);
            int count = SocketUtils.receiveInt(input);
            botsByType.put(botId, count);
        }

        // Styled bots amount
        Map<Integer, Map<BotStyle, Integer> > botsByStyleMap = new HashMap<>();
        int numberOfStyledBots = SocketUtils.receiveInt(input);
        for(int i = 0; i < numberOfStyledBots; i++) {

            // Current bot ID and number of styles
            int botID = SocketUtils.receiveInt(input);
            int numberOfStyles = SocketUtils.receiveInt(input);

            // Save info
            botsByStyleMap.put(botID, new HashMap<BotStyle, Integer>());

            for(int j = 0; j < numberOfStyles; ++j) {

                // Style identifier and number of instances
                BotStyle style = BotStyle.createByOrdinal( SocketUtils.receiveInt(input) );
                int instances = SocketUtils.receiveInt(input);

                // Save info
                botsByStyleMap.get(botID).put(style, instances);
            }
        }
        
        int numPlayers = SocketUtils.receiveInt(input);
        String selectedTable = SocketUtils.receiveString(input);
        String selectedCard = SocketUtils.receiveString(input);
        boolean joinedAsSpectator = SocketUtils.receiveInt(input) == GameType.TRUE;

        // Save data received in the game configuration
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

        // Bot maps
        config._botsByType.putAll(botsByType);
        config._botStylesByType.putAll(botsByStyleMap);
        
        config._numPlayers = numPlayers;
        config._selectedTable = selectedTable;
        config._selectedCard = selectedCard;
        config._joinedAsSpectator = joinedAsSpectator;

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

        // Number of bots instances by type
        SocketUtils.sendInteger(out, config._botsByType.size());
        for(var entry : config._botsByType.entrySet()) {
            SocketUtils.sendInteger(out, entry.getKey());
            SocketUtils.sendInteger(out, entry.getValue());
        }

        // Styled bots amount
        SocketUtils.sendInteger(out, config._botStylesByType.size());
        for(var entry : config._botStylesByType.entrySet()) {

            Integer botID = entry.getKey();
            Map<BotStyle, Integer> dist = entry.getValue();
            
            // Bot ID and style amount for the current bot
            SocketUtils.sendInteger(out, botID);
            SocketUtils.sendInteger(out, dist.size());

            for(var d : dist.entrySet()) {
                BotStyle st = d.getKey();
                Integer val = d.getValue();

                // Style identifier and number of instances
                SocketUtils.sendInteger(out, st.ordinal());
                SocketUtils.sendInteger(out, val);
            }
        }

        SocketUtils.sendInteger(out, config._numPlayers);
        SocketUtils.sendString(out, config._selectedTable);
        SocketUtils.sendString(out, config._selectedCard);
        SocketUtils.sendInteger(out, config._joinedAsSpectator ? GameType.TRUE : GameType.FALSE);
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

        // Receive number of bot types
        int botTypes = SocketUtils.receiveInt(input);
        Map<Integer, Integer> botsByType = new HashMap<>();
        for(int i = 0; i < botTypes; i++) {
            int botId = SocketUtils.receiveInt(input);
            int count = SocketUtils.receiveInt(input);
            botsByType.put(botId, count);
        }

        // Styled bots amount
        Map<Integer, Map<BotStyle, Integer> > botsByStyleMap = new HashMap<>();
        int numberOfStyledBots = SocketUtils.receiveInt(input);
        for(int i = 0; i < numberOfStyledBots; i++) {

            // Current bot ID and number of styles
            int botID = SocketUtils.receiveInt(input);
            int numberOfStyles = SocketUtils.receiveInt(input);

            // Save info
            botsByStyleMap.put(botID, new HashMap<BotStyle, Integer>());

            for(int j = 0; j < numberOfStyles; ++j) {

                // Style identifier and number of instances
                BotStyle style = BotStyle.createByOrdinal( SocketUtils.receiveInt(input) );
                int instances = SocketUtils.receiveInt(input);

                // Save info
                botsByStyleMap.get(botID).put(style, instances);
            }
        }

        int numPlayers = SocketUtils.receiveInt(input);
        String selectedTable = SocketUtils.receiveString(input);
        String selectedCard = SocketUtils.receiveString(input);
        boolean joinedAsSpectator = SocketUtils.receiveInt(input) == GameType.TRUE;


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
        
        // Bot maps
        config._botsByType.putAll(botsByType);
        config._botStylesByType.putAll(botsByStyleMap);

        config._numPlayers = numPlayers;
        config._selectedTable = selectedTable;
        config._selectedCard = selectedCard;
        config._joinedAsSpectator = joinedAsSpectator;

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