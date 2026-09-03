package com.ucm.server;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotStruct;
import com.ucm.common.ClientStruct;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.players.Spectator;


/**
 * Main class of the server. 
 * It is responsible for starting the server, initializing the pregame, initializing the game and handling exceptions that may occur during the server's operation.
 */
public class ServerMain {
    
    private static final Logger log = LogManager.getLogger(ServerMain.class);
    

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     *      .\mvnw.cmd clean install
     * 
     * Run server:
     *      .\mvnw.cmd -pl server -Prun exec:java
     * 
     * Debug:
     *      .\mvnwDebug.cmd -pl server -Pdebug exec:java
     * 
     * Run the tests
     *      .\mvnw.cmd test
     * 
     * Generate the Javadoc:
     *     .\mvnw.cmd -pl server javadoc:javadoc
     */

    /**
     * Full server initialization point.
     * 
     * @hidden @param args
     * @throws EvaluatorException when the evaluator fails. It stops the server.
     * @throws CancelGameException when the game has been cancelled by multiple reasons. It stops the server.
     */
    public static void main(String[] args) throws EvaluatorException, CancelGameException {

        while(true) {

            try {

                ServerTCP server = new ServerTCP(GameType.PORT);
                server.startPregame();

                List<ClientStruct> players = server.getRoomPlayers();
                List<BotStruct> bots = server.getRoomBots();
                Spectator spectator = server.getSpectator();
                GameConfig config = server.getGameConfigDeepCopy();
                log.debug("Pregame ended!");

                log.debug("Poker game starting!");
                server.startGame(players, bots, spectator, config);
                log.debug("Poker game finished!");
            }
            catch(IOException | InterruptedException e) {
                log.error("Error starting the server: {}", e.getMessage());
            }
        }
    }

}