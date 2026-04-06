package com.ucm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerGame;
import com.ucm.common.SocketUtils;


public class ClientThread implements Runnable {


    private static final Logger log = LogManager.getLogger(ClientThread.class);

    public int _playerID;
    public Socket _socket;
    public String _playerName;
    public boolean _isHost;

    public List<ClientThread> _roomList;

    public ClientThread(Socket socket, List<ClientThread> players) {
        _socket = socket;
        _playerName = null;
        _isHost = false;

        _roomList = players;
    }


    /*
        Este metodo gestiona a cada cliente.
        Idealmente, todas las peticiones deben ser STATELESS,:
            Esto es: no debería almacenarse el estado de nada(o casi nada)
                        para que la respuesta a una petición concreta sea siempre a misma.

        Por ejemplo:
        - Si el cliente pulsa el boton 'Create Game' por primera vez, el servidor debe responder simplemente con OK o ERROR.
        - Suponiendo que haya respondido OK, el servidor pasaría a una ventana siguiente
        - Si ahora el cliente vuelve para atras en la interfaz y vuelve a hacer lo mismo(Asi infinitas veces) la respuesta será la misma.

        Esto no siempre va a ser posible, habrá momentos en los que haya almacenar cosas
        (Por ejemplo, cuando el cliente cree una partida habrá que alamcenar quien 
        es para que ese sea el único que pueda realizar las peticiones y recibir las respuestas)
    
        En estos casos, debemos tener siempre en cuenta de que estos datos tenemos que poder descartarlos segun el contexto
    
        ¡¡ IMPORTANTE !!
        Para probar el funcionamiento utilizar la interfaz, ya que esta se comporta muy distinto respecto a la consola.
        Para probar el funcionamiento de las peticiones, comprobaciones, etc... viene bien.
        Ir haciendo incrementalmente la lógica probada con la interfaz.

    */
    @Override
    public void run() {
        
        try {
            InputStream input = _socket.getInputStream();
            OutputStream output = _socket.getOutputStream();

            boolean clientConnected = true;
            while (clientConnected) {
                
                int request = SocketUtils.receiveInt(input);
                log.debug("Received request: {}", request);

                switch (request) {
                case GameType.PETITION_PLAYER_NAME:
                    
                    String name = PokerGame.receiveName(input, output);
                    if(PokerGame.checkNameIsTooShort(name)) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_SHORT);
                    }
                    else if(PokerGame.checkNameIsTooLong(name)) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_LONG);
                    }
                    else {
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_NAME_VALID);
                        _playerName = name;

                        log.debug("Client with name {} authenticated!", _playerName);
                    }
                     
                    break;
            
                case GameType.PETITION_CREATE_GAME:
                    
                    GameConfig config = PokerGame.receiveGameConfig(input, output);
                    if(config == null) {
                        SocketUtils.sendInteger(output, GameType.ERROR_GAME_NOT_CREATED);
                        log.error("Configuration was not valid");
                    }
                    else {
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_WAITING_GAME);
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_HOST_PLAYER);
                        _isHost = true;
                        _playerID = _roomList.size();
                        _roomList.add(this);
                        SocketUtils.sendInteger(output, _playerID);

                        showPlayersInRoom();
                        log.debug("Configuration valid!");
                        log.debug("Players on the room: {}", _roomList.size());
                        log.debug("Room configuration: Room name=\'{}\' | Allow bots={}",
                            config._roomName,
                            config._allowBots
                        );
                    }

                    break;

                case GameType.PETITION_JOIN_GAME:
                    
                    if(0 < _roomList.size()) {
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_WAITING_GAME);
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
                            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.CONFIRMATION_GAME_STARTS);
                            log.debug("Game starts!");
                        }
                        else {
                            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.ERROR_GAME_CANNOT_START);
                            log.debug("Game cannot start! Missing players");
                        }
                    }
                    break;

                default:
                    log.debug("Request {} unknown", request);
                    break;
                }

            }
        }
        catch(IOException e) {
            log.error("Handling client connection: {}", e.getMessage());  
        }
        finally {
            closeConnection();
        }

    }

    private void broadcastPlayerJoined() {

        Thread notify = new Thread(() -> {

            synchronized(_roomList) {

                try {
                    for(ClientThread target : _roomList) {
                        for(ClientThread ct : _roomList) {
                            SocketUtils.sendInteger(target._socket.getOutputStream(), GameType.EVENT_PLAYER_JOINED);
                            PokerGame.sendPlayerInRoomInfo( new PlayerInfo(ct._playerID, ct._playerName), target._socket);
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

    private void closeConnection() {

        if(_socket == null || _socket.isClosed())
            return;


        try {
            _socket.close();
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