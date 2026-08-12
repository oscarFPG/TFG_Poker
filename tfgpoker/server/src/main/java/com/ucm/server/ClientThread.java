package com.ucm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotRegistry;
import com.ucm.common.BotStruct;
import com.ucm.common.BotStyle;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;
import com.ucm.server.players.Spectator;


public class ClientThread implements Runnable {


    private static final Logger log = LogManager.getLogger(ClientThread.class);

    
    /**
     * Current player ID
     */
    private int _playerID;

    /**
     * The socket connected to the client
     */
    private Socket _socket;

    /**
     * Players name
     */
    private String _playerName;

    /**
     * Indicates if the player is the host of the game
     */
    private boolean _isHost;


    /**
     * The server socket for the game. This is closed by the host to stop allowing players to join and start the game
     */
    private ServerSocket _serverSocket;

    /**
     * The unique ID generator for all the players
     */
    private AtomicInteger _id;

    /**
     * List of players in the room
     */
    private List<ClientThread> _roomPlayerList;

    /**
     * List of bots in the room
     */
    private List<BotStruct> _roomBotsList;

    /**
     * Game spectator. This can be null
     */
    private Spectator _spectator;

    /**
     * Selected game configuration by the host
     */
    private GameConfig _gameConfig;


    /**
     * Constructor for the ClientThread class
     * @param socket
     * @param gameSocket
     * @param idGen
     * @param players
     * @param bots
     * @param spectator
     * @param config
     */
    public ClientThread(
        Socket socket, ServerSocket gameSocket, AtomicInteger idGen, 
        List<ClientThread> players, List<BotStruct> bots, Spectator spectator,
        GameConfig config) {
        _playerID = -1;
        _socket = socket;
        _playerName = null;
        _isHost = false;

        _serverSocket = gameSocket;
        _id = idGen;
        _roomPlayerList = players;
        _roomBotsList = bots;
        _spectator = spectator;
        _gameConfig = config;
    }

    
    /**
     * Run method for the client thread.
     * This method handles the communication with the client, processing requests and sending responses.
     */
    @Override
    public void run() {
        
        try {
            InputStream input = _socket.getInputStream();
            OutputStream output = _socket.getOutputStream();

            boolean clientWaitingGame = true;
            while (clientWaitingGame) {
                
                int request = SocketUtils.receiveInt(input);
                if(_playerName == null)
                    log.debug("Received request: {}", request);
                else
                    log.debug("Received request by {}: {}", _playerName, request);

                switch (request) {
                case GameType.PETITION_PLAYER_NAME:
                    
                    String name = PokerPreGame.receiveName(input, output);
                    if(PokerPreGame.checkNameIsTooShort(name)) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_SHORT);
                    }
                    else if(PokerPreGame.checkNameIsTooLong(name)) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_LONG);
                    }
                    else if (isNameAlreadyUsed(name)) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_ALREADY_USED);
                    }
                    else {
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_NAME_VALID);
                        _playerName = name;

                        log.debug("Client with name {} authenticated!", _playerName);
                    }
                     
                    break;
            
                case GameType.PETITION_CREATE_GAME:
                    
                    GameConfig config = PokerPreGame.receiveGameConfig(input, output);
                    if(config == null) {
                        SocketUtils.sendInteger(output, GameType.ERROR_GAME_NOT_CREATED);
                        log.error("Configuration was not valid");
                    }
                    else {

                        // DO NOT COPY THE CONFIG REFERENCE, IT MUST BE STAY SHARED BETWEEN ALL CLIENT THREADS !!
                        _gameConfig._roomId = (int)(Math.random() * 10000);
                        _gameConfig._roomName = config._roomName;
                        _gameConfig._userName = config._userName;
                        _gameConfig._initialMoney = config._initialMoney;
                        _gameConfig._allowBots = config._allowBots;
                        _gameConfig._blindsValue = config._blindsValue;
                        _gameConfig._dynamicBlinds = config._dynamicBlinds;
                        _gameConfig._levelDuration = config._levelDuration;
                        _gameConfig._hikePercentage = config._hikePercentage;
                        _gameConfig._turnTimerPlayer = config._turnTimerPlayer;

                        _gameConfig._botsByType.clear();
                        _gameConfig._botsByType.putAll(config._botsByType);
                        _gameConfig._botStylesByType.clear();
                        _gameConfig._botStylesByType.putAll(config._botStylesByType);
                        
                        _gameConfig._numPlayers = config._numPlayers + 1; // + 1 porque cuenta el host
                        _gameConfig._selectedTable = config._selectedTable;
                        _gameConfig._selectedCard = config._selectedCard;
                        _gameConfig._joinedAsSpectator = config._joinedAsSpectator;
                        _isHost = true;

                        // Si el host es espectador no lo incluimos en la lista de jugadores pero si en el socket de espectador
                        if(_gameConfig._joinedAsSpectator) {
                            _playerID = -1;
                            _spectator._name = String.copyValueOf( _playerName.toCharArray() );
                            _spectator._spectatorSocket = _socket;
                        }
                        else {
                            _playerID = _id.getAndIncrement();
                            _roomPlayerList.add(this);
                        }


                        // Instanciate every bot
                        for(var entry : _gameConfig._botsByType.entrySet()) {
                            Integer botID = entry.getKey();
                            Integer botCount = entry.getValue();

                            // Check if this bot allow styles
                            if( _gameConfig._botStylesByType.containsKey(botID) ) {
                                
                                Map<BotStyle, Integer> dist = _gameConfig._botStylesByType.get(botID);

                                for(var d : dist.entrySet()){

                                    BotStyle st = d.getKey();
                                    Integer amount = d.getValue();

                                    for(int i = 0; i < amount; i++){
                                        int matchId = _id.getAndIncrement();
                                        String botName = BotRegistry.getBotName(botID, matchId, st);
                                        _roomBotsList.add( BotStruct.createStyledBot(botID, matchId, botName, st) );
                                    }
                                }
                            }
                            else {

                                for(int i = 0; i < botCount; ++i) {

                                    int matchId = _id.getAndIncrement();
                                    String botName = BotRegistry.getBotName(botID, matchId, BotStyle.DEFAULT);

                                    _roomBotsList.add( BotStruct.createSimpleBot(botID, matchId, botName) );
                                }
                            }

                        }

                        // Avisar a todos los jugadores de la correcta creacion de la partida, ID de sala y su ID de jugador
                        // Tambien avisamos al jugador necesario de que es el host
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_WAITING_GAME);
                        SocketUtils.sendInteger(output, _gameConfig._roomId);
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_HOST_PLAYER);
                        SocketUtils.sendInteger(output, _playerID);

                        // DEBUG -> Mostrar jugadores
                        showPlayersInRoom();

                        // Avisar a todos los jugadores de la lista de jugadores unidos
                        broadcastPlayerJoined();

                        StringBuilder botsInfo = new StringBuilder();
                        _gameConfig._botsByType.forEach((id, count) ->
                            botsInfo.append("[botId=")
                                .append(id)
                                .append(", count=")
                                .append(count)
                                .append("] ")
                        );
                        log.debug("Configuration valid!");
                        log.debug("Room {}: Name=[{}], UserName=[{}], AllowBots=[{}], Bots configured: {}",
                            _gameConfig._roomId,
                            _gameConfig._roomName,
                            _gameConfig._userName,
                            _gameConfig._allowBots,
                            botsInfo.length() == 0 ? "none" : botsInfo.toString()
                        );
                    }

                    break;

                case GameType.PETITION_JOIN_GAME:
                    
                    int playersCounter = _roomPlayerList.size() + _roomBotsList.size();
                    if(_spectator._spectatorSocket != null || (0 < playersCounter && _roomPlayerList.size() < _gameConfig._numPlayers)) {

                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_WAITING_GAME);
                        PokerPreGame.sendGameConfigToJoinedPlayer(_gameConfig, output);

                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_NO_HOST_PLAYER);
                        _playerID = _id.getAndIncrement();
                        _roomPlayerList.add(this);
                        SocketUtils.sendInteger(output, _playerID);

                        broadcastPlayerJoined();
                        showPlayersInRoom();
                    }
                    else {
                        SocketUtils.sendInteger(output, GameType.ERROR_GAME_NOT_JOINED);
                    }

                    break;
                
                case GameType.EVENT_GAME_STARTS:
                    
                    if(!_isHost) {

                        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.ERROR_GAME_CANNOT_START);
                        log.debug("This client cannot request");

                        break;
                    }

                    synchronized(_roomPlayerList) {

                        if(2 <= _roomPlayerList.size() + _roomBotsList.size()) {

                            if(_spectator._spectatorSocket != null)
                                SocketUtils.sendInteger(output, GameType.CONFIRMATION_GAME_STARTS);

                            for(ClientThread ct : _roomPlayerList) {
                                SocketUtils.sendInteger(ct._socket.getOutputStream(), GameType.CONFIRMATION_GAME_STARTS);
                            }
                            log.debug("All players notified of game starts!");
                            
                            _serverSocket.close();
                            log.debug("ServerSocket closed!");
                        }
                        else {
                            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.ERROR_GAME_CANNOT_START);
                            log.debug("Game cannot start! Missing players");
                        }
                    }
                    break;

                case GameType.CONFIRMATION_PLAYER_STARTS:
                    clientWaitingGame = false;
                    break;

                default:
                    log.debug("Request {} unknown", request);
                    break;
                }

            }
        }
        catch(IOException e) {

            log.error("Handling client connection: {}", e.getMessage());
            if(_isHost) {

                log.warn("Host {} left the waiting room! Cancelling game!", _playerName);

                // Close connection with all players
                // Match is cancelled
                synchronized(_roomPlayerList) {
                    for(ClientThread ct : _roomPlayerList) {
                        closeConnection(ct._socket);
                    }

                    _id.set(0);
                    _roomPlayerList.clear();
                }
                
            }
            else {
                closeConnection(_socket);

                // Update global game state
                _id.getAndDecrement();          // IMPORTANT !!
                _roomPlayerList.remove(this);   // IMPORTANT !!

                // Notify all players about new game room
                broadcastPlayerJoined();
                
                log.warn("Client {} lef the waiting room", _playerName);
            }
        }
        
        log.debug("Client thread terminating...");
    }

    /**
     * This method broadcast the player list to all players.
     * It uses the {@link #broadcastPlayerInfoToSpectator} and {@link #broadcastPlayerInfoToRoomPlayers} methods to send the information to the spectator and the players in the room, respectively.
     */
    private void broadcastPlayerJoined() {

        Thread notify = new Thread(() -> {

            synchronized(_roomPlayerList) {

                try {

                    int roomSize = _roomPlayerList.size() + _roomBotsList.size();
                    if(roomSize == 0)
                        return;


                    broadcastPlayerInfoToSpectator();
                    broadcastPlayerInfoToRoomPlayers();
                }
                catch(IOException e) {
                    log.error("Error sending info to all players");
                }
            }

        });

        notify.start();
    }

    /**
     * Broadcasts the information of the players in the room to all connected players.
     * This method synchronizes access to the player list to prevent concurrent modifications while broadcasting.
     * @throws IOException
     */
    private void broadcastPlayerInfoToRoomPlayers() throws IOException {

        log.debug("----- Sending players info to in game players -----");

        int roomSize = _roomPlayerList.size() + _roomBotsList.size();
        for (ClientThread target : _roomPlayerList) {

            Socket targetSocket = target._socket;

            SocketUtils.sendInteger(targetSocket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
            SocketUtils.sendInteger(targetSocket.getOutputStream(), roomSize);
            for (ClientThread ct : _roomPlayerList) {
                PokerPreGame.sendPlayerInRoomInfo( new PlayerInfo(ct._playerID, ct._playerName), targetSocket);
                log.debug("Player {} on waiting room", ct._playerName);
            }

            for(BotStruct bs : _roomBotsList) {
                PokerPreGame.sendPlayerInRoomInfo( new PlayerInfo(bs.matchId(), bs.botName()), targetSocket);
                log.debug("Bot {} on waiting room", bs.botName());
            }
            
            log.debug("All player info sent to {}\n", target._playerName);
        }
    }

    /**
     * Broadcasts the information of the players in the room to the connected spectator.
     * This method checks if a spectator is connected and sends the player information to the spectator's socket.
     * @throws IOException
     */
    private void broadcastPlayerInfoToSpectator() throws IOException {

        if(_spectator._spectatorSocket != null) {

            log.debug("----- Sending players info to spectator -----");

            int roomSize = _roomPlayerList.size() + _roomBotsList.size();

            SocketUtils.sendInteger(_spectator._spectatorSocket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
            SocketUtils.sendInteger(_spectator._spectatorSocket.getOutputStream(), roomSize);
            for (ClientThread ct : _roomPlayerList) {
                PokerPreGame.sendPlayerInRoomInfo( new PlayerInfo(ct._playerID, ct._playerName), _spectator._spectatorSocket);
                log.debug("Player {} on waiting room", ct._playerName);
            }

            for(BotStruct bs : _roomBotsList) {
                PokerPreGame.sendPlayerInRoomInfo( new PlayerInfo(bs.matchId(), bs.botName()), _spectator._spectatorSocket);
                log.debug("Bot {} on waiting room", bs.botName());
            }
        }
    }

    /**
     * Closes the connection with the specified socket.
     * If the socket is null or already closed, the method returns without performing any action.
     * @param socket
     */
    private void closeConnection(Socket socket) {

        if(socket == null || socket.isClosed())
            return;


        try {
            socket.close();
            log.error("Closing connection with client");
        }
        catch(IOException e) {
            log.error("Closing client socket: {}", e.getMessage());
        }
    }

    /**
     * Displays the names of all players and bots currently in the room for debugging purposes.
     */
    private void showPlayersInRoom() {

        synchronized(_roomPlayerList) {

            for(ClientThread cl : _roomPlayerList) {
                log.debug("Player {} in room", cl._playerName);
            }

            for(BotStruct bs : _roomBotsList) {
                log.debug("Bot {} int the room", bs.botName());
            }
        }

    }

    /**
     * Checks if the specified player name is already used by any player in the room.
     * This method synchronizes access to the player list to prevent concurrent modifications while checking for name uniqueness.
     * @param name
     * @return
     */
    private boolean isNameAlreadyUsed(String name) {

        synchronized(_roomPlayerList) {
            
            for (ClientThread ct : _roomPlayerList) {
                if(ct._playerName != null && ct._playerName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Returns the player ID of this client thread.
     * @return the player ID
     */
    public int getPlayerID() { return _playerID; }

    /**
     * Returns the socket associated with this client thread.
     * @return the socket connected to the client
     */
    public Socket getPlayerSocket() { return _socket; }

    /**
     * Returns the name of the player associated with this client thread.
     * @return the player's name
     */
    public String getPlayerName() { return _playerName; }

    /**
     * Returns whether this client thread represents the host of the game.
     * @return true if this client thread is the host, false otherwise
     */
    public boolean getIsHost() { return _isHost; }

}