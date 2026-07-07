package com.ucm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotStruct;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;
import com.ucm.server.middleclasses.Spectator;
import com.ucm.server.players.AgentCFR;
import com.ucm.server.players.GeminiLLM;
import com.ucm.server.players.LlamaPokerLLM;


public class ClientThread implements Runnable {


    private static final Logger log = LogManager.getLogger(ClientThread.class);

    private int _playerID;
    private Socket _socket;
    private String _playerName;
    private boolean _isHost;

    private ServerSocket _serverSocket;
    private AtomicInteger _id;
    private List<ClientThread> _roomPlayerList;
    private List<BotStruct> _roomBotsList;
    private Spectator _spectator;
    private GameConfig _gameConfig;


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
                        _gameConfig._dinamicBlinds = config._dinamicBlinds;
                        _gameConfig._levelDuration = config._levelDuration;
                        _gameConfig._hikePercentage = config._hikePercentage;
                        _gameConfig._turnTimerPlayer = config._turnTimerPlayer;
                        _gameConfig._numBots1 = config._numBots1;   // Numero de instancias de bot gemini
                        _gameConfig._numBots2 = config._numBots2;   // Numero de instancias de bot llama
                        _gameConfig._numPlayers = config._numPlayers + 1; // +1 porque cuenta el host
                        _gameConfig._selectedTable = config._selectedTable;
                        _gameConfig._selectedCard = config._selectedCard;
                        _gameConfig._joinedAsSpectator = config._joinedAsSpectator;
                        _isHost = true;

                        // Si el host es espectador no lo incluimos en la lista de jugadores pero si en el socket de espectador
                        if(_gameConfig._joinedAsSpectator) {
                            _playerID = -1;
                            _spectator._name = _playerName;
                            _spectator._socket = _socket;
                        }
                        else {
                            _playerID = _id.getAndIncrement();
                            _roomPlayerList.add(this);
                        }

                        
                        boolean test_NN = false;
                        if(test_NN) {

                            // TEST -> usar bots Gemini como redes neuronales
                            for (int i = 0; i < _gameConfig._numBots1; i++) {
                                _roomBotsList.add( new BotStruct(_id.getAndIncrement(), GameType.BOT_NN_MODEL_1, AgentCFR.CFR_NAME) );
                            }
                        }
                        else {

                            // Incluir instancias de los bots seleccionados, si hay
                            for (int i = 0; i < _gameConfig._numBots1; i++) {
                                _roomBotsList.add( new BotStruct(_id.getAndIncrement(), GameType.BOT_GEMINI, GeminiLLM.NAME) );
                            }
                            for (int i = 0; i < _gameConfig._numBots2; i++) {
                                int instance = _id.getAndIncrement();
                                _roomBotsList.add( new BotStruct(instance, GameType.BOT_LLAMA, LlamaPokerLLM.MODEL_NAME + "#" + instance) );
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

                        log.debug("Configuration valid!");
                        log.debug("Room {}: Name=[{}], UserName=[{}], AllowBots=[{}], Bots Gemini=[{}], Bots Llama=[{}]",
                            _gameConfig._roomId,
                            _gameConfig._roomName,
                            _gameConfig._userName,
                            _gameConfig._allowBots,
                            _gameConfig._numBots1,
                            _gameConfig._numBots2
                        );
                    }

                    break;

                case GameType.PETITION_JOIN_GAME:
                    
                    int playersCounter = _roomPlayerList.size() + _roomBotsList.size();

                    if(_spectator._socket != null || 0 < playersCounter) {

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

                            if(_spectator._socket != null)
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

                synchronized(_roomPlayerList) {
                    for(ClientThread ct : _roomPlayerList) {
                        closeConnection(ct._socket);
                    }
                }
            }
            else {
                closeConnection(_socket);
            }
        }
        log.debug("Client thread terminating...");

    }

    private void broadcastPlayerJoined() {

        Thread notify = new Thread(() -> {

            synchronized(_roomPlayerList) {

                try {

                    int roomSize = _roomPlayerList.size() + _roomBotsList.size();
                    if(roomSize == 0)
                        return;


                    if(_spectator._socket != null) {

                        SocketUtils.sendInteger(_spectator._socket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
                        SocketUtils.sendInteger(_spectator._socket.getOutputStream(), roomSize);
                        for (ClientThread ct : _roomPlayerList) {
                            PokerPreGame.sendPlayerInRoomInfo( new PlayerInfo(ct._playerID, ct._playerName), _spectator._socket);
                            log.debug("Player {} on waiting room", ct._playerName);
                        }
                        for(BotStruct bs : _roomBotsList) {
                            PokerPreGame.sendPlayerInRoomInfo( new PlayerInfo(bs.matchId(), bs.botName()), _spectator._socket);
                            log.debug("Bot {} on waiting room", bs.botName());
                        }
                    }

                    broadcastPlayerInfoToRoomPlayers();
                }
                catch(IOException e) {
                    log.error("Error sending info to all players");
                }
            }

        });

        notify.start();
    }

    private void broadcastPlayerInfoToRoomPlayers() throws IOException {

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
            
        }
    }

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


    public Socket getPlayerSocket() { return _socket; }
    public String getPlayerName() { return _playerName; }
    public boolean getIsHost() { return _isHost; }

}