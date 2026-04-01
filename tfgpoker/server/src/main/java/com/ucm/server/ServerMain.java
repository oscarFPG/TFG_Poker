package com.ucm.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.ServerTCP;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.server.control.Controller;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.logic.Game;
import com.ucm.server.middleclasses.ClientStruct;



public class ServerMain {
    

    private static final Logger log = LogManager.getLogger(ServerMain.class);
    
    public static int MAX_PLAYERS = 9;

    private static List< ClientStruct<Integer> > _clients = Collections.synchronizedList(new ArrayList<>());

    private static boolean _gameStarts = false;


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

        try {
            ServerTCP server = new ServerTCP(GameType.PORT);
            Evaluator evaluator = Evaluator.getInstance();

            server.start();
        }
        catch(IOException | InterruptedException e) {
            log.error("Error starting the server: {}", e.getMessage());
        }   
    }


}