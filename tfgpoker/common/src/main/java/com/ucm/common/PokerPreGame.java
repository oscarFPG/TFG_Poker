package com.ucm.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides utility methods for handling the pre-game phase of a poker game, including connecting to the server, sending and receiving player names, game configurations, and player information.
 */
public class PokerPreGame {

    /**
     * The localhost IP address, used to connect to the server running on the same machine.
     */
    public static final String LOCAL_HOST = "localhost"; 

    /**
     * Private constructor to prevent instantiation of the PokerPreGame class.
     */
    private PokerPreGame() {}

    /**
     * Connects to the poker server using the specified server IP address.
     * @param serverIP The IP address of the poker server to connect to.
     * @return A Socket object representing the connection to the server.
     * @throws IOException If an I/O error occurs when creating the socket.
     */
    public static Socket connect(final String serverIP) throws IOException {

		Socket socket = new Socket(serverIP, GameType.PORT);
		return socket;
	}
    /**
     * Sends the player's name to the server through the specified socket.
     * @param name The name of the player to send to the server.
     * @param socket The socket through which to send the player's name.
     * @throws IOException If an I/O error occurs when sending the name to the server.
     */
    public static void sendName(final String name, Socket socket) throws IOException {

        SocketUtils.sendInteger(socket.getOutputStream(), GameType.PETITION_PLAYER_NAME);
        SocketUtils.sendString(socket.getOutputStream(), name);
    }
    /**
     * Receives the player's name from the server through the specified input stream.
     * @param input The input stream from which to receive the player's name.
     * @param output The output stream to which to send any necessary responses or acknowledgments.
     * @return The name of the player received from the server.
     * @throws IOException If an I/O error occurs when receiving the name from the server.
     */
    public static String receiveName(InputStream input, OutputStream output) throws IOException {

        String name = SocketUtils.receiveString(input);
        return name;
    }
    /**
     * Sends the game configuration to the server through the specified output stream.
     * @param config The GameConfig object containing the game configuration to send to the server.
     * @param out The output stream through which to send the game configuration to the server.
     * @throws IOException If an I/O error occurs when sending the game configuration to the server.
     */
    public static void sendGameConfig(GameConfig config, OutputStream out) throws IOException {
    
        SocketUtils.sendInteger(out, GameType.PETITION_CREATE_GAME);
        SocketUtils.sendString(out, config._roomName);
        SocketUtils.sendString(out, config._userName);
        SocketUtils.sendInteger(out, config._initialMoney);
        SocketUtils.sendInteger(out, config._allowBots ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendString(out, config._blindsValue);
        SocketUtils.sendInteger(out, config._dynamicBlinds ? GameType.TRUE : GameType.FALSE);
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
    /**
     * Receives the game configuration from the server through the specified input stream.
     * @param input The input stream from which to receive the game configuration.
     * @param output The output stream to which to send any necessary responses or acknowledgments.
     * @return A GameConfig object containing the game configuration received from the server.
     * @throws IOException If an I/O error occurs when receiving the game configuration from the server.
     */
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
        config._dynamicBlinds = dinamicBlinds;
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
    /**
     * Sends the game configuration to a player who has joined the game through the specified output stream.
     * @param config The GameConfig object containing the game configuration to send to the joined player.
     * @param out The output stream through which to send the game configuration to the joined player.
     * @throws IOException If an I/O error occurs when sending the game configuration to the joined player.
     */
    public static void sendGameConfigToJoinedPlayer(GameConfig config, OutputStream out) throws IOException {

        SocketUtils.sendInteger(out, config._roomId);
        int allowBotsCode = (config._allowBots) ? GameType.TRUE : GameType.FALSE;
        int dinamicBlinds = (config._dynamicBlinds) ? GameType.TRUE : GameType.FALSE;
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
    /**
     * Receives the game configuration from the server for a player who has joined the game through the specified input stream.
     * @param input The input stream from which to receive the game configuration for the joined player.
     * @param output The output stream to which to send any necessary responses or acknowledgments.
     * @return A GameConfig object containing the game configuration received from the server for the joined player.
     * @throws IOException If an I/O error occurs when receiving the game configuration from the server for the joined player.
     */
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
        config._dynamicBlinds = dinamicBlinds;
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
    /**
     * Sends a waiting room confirmation message to the server through the specified socket.
     * @param socket The socket through which to send the waiting room confirmation message.
     * @throws IOException If an I/O error occurs when sending the waiting room confirmation message to the server.
     */
    public static void sendWaitingRoomConfirmation(Socket socket) throws IOException {
        SocketUtils.sendInteger(socket.getOutputStream(), GameType.CONFIRMATION_WAITING_GAME);
    }
    /**
     * Receives a waiting room confirmation message from the server through the specified socket.
     * @param socket The socket from which to receive the waiting room confirmation message.
     * @return An integer representing the waiting room confirmation message received from the server.
     * @throws IOException If an I/O error occurs when receiving the waiting room confirmation message from the server.
     */
    public static int receiveWaitingRoomConfirmation(Socket socket) throws IOException {
       
        int response = SocketUtils.receiveInt(socket.getInputStream());
        return response;
    }
    /**
     * Sends the information of a player in the room to the server through the specified socket.
     * @param p The PlayerInfo object containing the information of the player in the room to send to the server.
     * @param socket The socket through which to send the player's information to the server.
     * @throws IOException If an I/O error occurs when sending the player's information to the server.
     */
    public static void sendPlayerInRoomInfo(PlayerInfo p, Socket socket) throws IOException {

        SocketUtils.sendInteger(socket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
        SocketUtils.sendInteger(socket.getOutputStream(), p.id);
        SocketUtils.sendString(socket.getOutputStream(), p.name);
    }
    /**
     * Receives the information of a player in the room from the server through the specified input stream.
     * @param input The input stream from which to receive the player's information.
     * @param output The output stream to which to send any necessary responses or acknowledgments.
     * @return A PlayerInfo object containing the information of the player in the room received from the server, or null if the response is not as expected.
     * @throws IOException If an I/O error occurs when receiving the player's information from the server.
     */
    public static PlayerInfo receivePlayerInRoomInfo(InputStream input, OutputStream output) throws IOException {

        int response = SocketUtils.receiveInt(input);
        if(response != GameType.EVENT_PLAYER_JOINED) {
            return null;
        }
        
        
        int id = SocketUtils.receiveInt(input);
        String name = SocketUtils.receiveString(input);

        return new PlayerInfo(id, name);
    }
    /**
     * Receives the list of players waiting in the room from the server through the specified input stream.
     * @param input The input stream from which to receive the list of players waiting in the room.
     * @param output The output stream to which to send any necessary responses or acknowledgments.
     * @return A list of PlayerInfo objects representing the players waiting in the room received from the server.
     * @throws IOException If an I/O error occurs when receiving the list of players waiting in the room from the server.
     */
    public static List<PlayerInfo> receivePlayerListWaiting(InputStream input, OutputStream output) throws IOException {

        List<PlayerInfo> playerPositions = new ArrayList<>();
        
        int numPlayers = SocketUtils.receiveInt(input);
        for(int i = 0; i < numPlayers; i++) {
            PlayerInfo p = PokerPreGame.receivePlayerInRoomInfo(input, output);
            playerPositions.add( p );
        }

        return playerPositions;
    }
    /**
     * Checks if the provided IP address is valid (not empty or whitespace).
     * @param IP The IP address to check for validity.
     * @return true if the IP address is valid (not empty or whitespace), false otherwise.
     */
    public static boolean checkIpValid(final String IP){
		return !IP.trim().isEmpty();
	}
    /**
     * Checks if the provided player name is too short (less than 3 characters).
     * @param name The player name to check for length.
     * @return true if the name is too short (less than 3 characters), false otherwise.
     */
    public static boolean checkNameIsTooShort(final String name) {
        return name.length() < 3;
    }
    /**
     * Checks if the provided player name is too long (more than 10 characters).
     * @param name The player name to check for length.
     * @return true if the name is too long (more than 10 characters), false otherwise.
     */
    public static boolean checkNameIsTooLong(final String name) {
        return 10 < name.length();
    }

}