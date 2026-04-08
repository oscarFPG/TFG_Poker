package com.ucm.server;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameConfig;
import com.ucm.common.GameInfo;
import com.ucm.common.GameType;


public class ServerMain {
    
    private static final Logger log = LogManager.getLogger(ServerMain.class);

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     *      .\mvnw.cmd clean install
     * Run server:
     *      .\mvnw.cmd -pl server -Prun exec:java
     * Run server in local mode (no server, only for testing):
     *      .\mvnw.cmd -pl server -Prun-local exec:java -Dn=<int>
     * Debug:
     *      .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * Debug server in local mode (no server, only for testing):
     *      .\mvnwDebug.cmd -pl server -Pdebug-local exec:java -Dn=<int>
     * Run the tests
     *      .\mvnw.cmd test
     */
    public static void main(String[] args) {

        while(true) {

            try {
                ServerTCP server = new ServerTCP(GameType.PORT);
                
                List<ClientThread> clients = server.startPregame();
                GameConfig config = server.getGameConfigDeepCopy();
                log.debug("Pregame ended!");

                // Convert data -> This is for human players
                GameInfo gameInfo = new GameInfo( new GameConfig(config) );
                for(ClientThread ct : clients) {
                    gameInfo.addPlayer(ct._playerName, ct._socket);
                }

                // Add logic to add bots if needed
                // TODO

                log.debug("Poker game starting!");
                server.startGame(gameInfo);
                log.debug("Poker game finished!");
            }
            catch(IOException | InterruptedException e) {
                log.error("Error starting the server: {}", e.getMessage());
            }
        }
    }

}