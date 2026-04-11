package com.ucm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;


public class ClientThread implements Runnable {


    private static final Logger log = LogManager.getLogger(ClientThread.class);

    public int _playerID;
    public Socket _socket;
    public String _playerName;
    public boolean _isHost;

    public List<ClientThread> _roomList;
    public ServerSocket _serverSocket;
    public GameConfig _gameConfig;


    public ClientThread(Socket socket, List<ClientThread> players, ServerSocket gameSocket, GameConfig config) {
        _playerID = -1;
        _socket = socket;
        _playerName = null;
        _isHost = false;

        _roomList = players;
        _serverSocket = gameSocket;
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
                log.debug("Received request: {}", request);

                switch (request) {
                case GameType.PETITION_PLAYER_NAME:
                    
                    String name = PokerPreGame.receiveName(input, output);
                    if(PokerPreGame.checkNameIsTooShort(name)) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_SHORT);
                    }
                    else if(PokerPreGame.checkNameIsTooLong(name)) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_LONG);
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
                        _gameConfig._numBots1 = config._numBots1;
                        _gameConfig._numBots2 = config._numBots2;
                        _gameConfig._numBots3 = config._numBots3;
                        _gameConfig._numPlayers = config._numPlayers;
                        _gameConfig._selectedTable = config._selectedTable;
                        _gameConfig._selectedCard = config._selectedCard;

                        _isHost = true;
                        _playerID = _roomList.size();
                        _roomList.add(this);

                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_WAITING_GAME);
                        SocketUtils.sendInteger(output, _gameConfig._roomId);
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_HOST_PLAYER);
                        SocketUtils.sendInteger(output, _playerID);

                        showPlayersInRoom();

                        log.debug("Configuration valid!");
                        log.debug("Room {}: Name=[{}], UserName=[{}], AllowBots=[{}]",
                            _gameConfig._roomId,
                            _gameConfig._roomName,
                            _gameConfig._userName,
                            _gameConfig._allowBots
                        );
                    }

                    break;

                case GameType.PETITION_JOIN_GAME:
                    
                    if(0 < _roomList.size()) {

                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_WAITING_GAME);
                        PokerPreGame.sendGameConfigToJoinedPlayer(_gameConfig, output);

                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_NO_HOST_PLAYER);
                        _playerID = _roomList.size();
                        _roomList.add(this);
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

                    synchronized(_roomList) {

                        if(2 <= _roomList.size()) {

                            for(ClientThread ct : _roomList) {
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

                synchronized(_roomList) {
                    for(ClientThread ct : _roomList) {
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

            synchronized(_roomList) {

                try {
                    for (ClientThread target : _roomList) {

                        SocketUtils.sendInteger(target._socket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
                        SocketUtils.sendInteger(target._socket.getOutputStream(), _roomList.size());
                        for (ClientThread ct : _roomList) {
                            PokerPreGame.sendPlayerInRoomInfo( new PlayerInfo(ct._playerID, ct._playerName), target._socket);
                        }
                        
                    }
                }
                catch(IOException e) {
                    log.error("Error sending info to all players");
                }
            }

        });

        notify.start();
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

        // Thread-safe print
        synchronized(_roomList) {
            for(ClientThread cl : _roomList){
                log.debug("Player {} in room", cl._playerName);
            }
        }

    }

}