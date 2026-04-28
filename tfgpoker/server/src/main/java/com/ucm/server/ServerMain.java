package com.ucm.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.control.Controller;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.logic.Game;
import com.ucm.common.BotStruct;
import com.ucm.common.ClientStruct;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.exceptions.CancelGameException;


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
    public static void main(String[] args) throws EvaluatorException, CancelGameException {

        if(Game.DEBUG_PLAYERS) {

            List<ClientStruct> players = new ArrayList<>(
                List.of(
                    ClientStruct.createGuestPlayer("PL1", null),
                    ClientStruct.createGuestPlayer("PL2", null),
                    ClientStruct.createGuestPlayer("PL3", null)
                )
            );
            List<BotStruct> bots = new ArrayList<>();
            GameConfig config = new GameConfig();
            config.reset();

            Game game = new Game(players, bots, config);
            Controller controller = new Controller(game);
            controller.run();
            return; 
        }

        while(true) {

            try {

                ServerTCP server = new ServerTCP(GameType.PORT);
                server.startPregame();

                List<ClientStruct> players = server.getRoomPlayers();
                List<BotStruct> bots = server.getRoomBots();
                GameConfig config = server.getGameConfigDeepCopy();
                log.debug("Pregame ended!");

                log.debug("Poker game starting!");
                server.startGame(players, bots, config);
                log.debug("Poker game finished!");
            }
            catch(IOException | InterruptedException e) {
                log.error("Error starting the server: {}", e.getMessage());
            }
        }
    }

}