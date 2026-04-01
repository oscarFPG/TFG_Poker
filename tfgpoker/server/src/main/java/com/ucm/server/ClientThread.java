package com.ucm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;


public class ClientThread implements Runnable{


    private static final Logger log = LogManager.getLogger(ClientThread.class);


    public Socket _socket;
    public String _playerName;

    public ClientThread(Socket socket) {
        _socket = socket;
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
                    
                    String name = SocketUtils.receiveString(input);
                    log.debug("Received player name: {}", name);

                    for(int i = 6; 0 < i; i--) {
                        log.debug("Responding to client in {}", i);
                        Thread.sleep(1000);
                    }

                    if(name.length() < 3) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_SHORT);
                    }
                    else if(10 < name.length()) {
                        SocketUtils.sendInteger(output, GameType.ERROR_NAME_TOO_LONG);
                    }
                    else {
                        _playerName = name;
                        SocketUtils.sendInteger(output, GameType.CONFIRMATION_NAME_VALID);
                    }

                    break;
            
                case GameType.PETITION_CREATE_GAME:
                    // Misma logica...
                    break;

                case GameType.PETITION_JOIN_GAME:
                    // Misma logica...
                    break;

                default:
                    log.debug("Request {} unknown", request);
                    break;
                }

            }
        }
        catch(IOException | InterruptedException e) {
            log.error("Handling client connection: {}", e.getMessage());  
        }
        finally {

            try {
                _socket.close();
            }
            catch(IOException e) {
                log.error("Closing client socket: {}", e.getMessage());
            }
        }

    }

}