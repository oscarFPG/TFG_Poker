package com.ucm.server.control;


import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.logic.Game;
import com.ucm.common.ClientStruct;
import com.ucm.server.players.HumanPlayer;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class Controller {

    private static final Logger log = LogManager.getLogger(Controller.class);

    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;

    
    public Controller(Game game, List<ClientStruct> players) {
        _game = game;
        addPlayersToGame(players);
    }

    /**
     * Controller constructor only for debugging purposes.
     * It will be used in the local mode of the server, where no clients are needed.
     * @param game The game instance to control.
     * @param numPlayers The number of local players to add to the game.
     */
    public Controller(Game game, int numPlayers) {
        _game = game;
        addPlayersToGameLocally(numPlayers);
    }


    private void addPlayersToGame(List<ClientStruct> players) {

        int id = 0;
        for(ClientStruct cs : players) {
            _game.addPlayer( new HumanPlayer(id, cs.name(), cs.socket(), 1000) );
            ++id;
        }
    }

    private void addPlayersToGameLocally(int numPlayers) {

        int id = 0;
        for(int i = 0; i < numPlayers; ++i){
            _game.addPlayer( new HumanPlayer(id, "Player" + i, null, 1000) );
            ++id;
        }

    }

    public void run() {

        int handCounter = 0;
        boolean endOfGame = false;

        ThreadContext.put("match", "0");
        ThreadContext.put("hand", String.valueOf(handCounter));


        log.debug("Assigning roles to all players");
        _game.assignRolesToAllPlayers();
        
        while (!endOfGame) {

            log.debug("Starting hand {}", handCounter);
            try {
                
                // Pre-flop (2)
                log.debug("Pre-flop round");
                _game.shareOutCardsToAllPlayers();
                _game.playHand();

                // Flop (3)
                log.debug("Flop round");
                _game.addCardToTable();
                _game.addCardToTable();
                _game.addCardToTable();
                _game.playHand();

                // Turn (4)
                log.debug("Turn round");
                _game.addCardToTable();
                _game.playHand();

                // River (5)
                log.debug("River round");
                _game.addCardToTable();
                _game.playHand();

                // Showdown (6)
                log.debug("Showdown round");
                _game.giveRewardToWinner();
            }
            catch (OnlyOnePlayerLeftException e) {
                log.debug("Showdown with only one player left");
                _game.giveRewardToWinner();
            }

            log.debug("Passing to the next round");
            endOfGame = _game.passTurn();

            // Logger configuration for the next hand -> Write on file match{0}_hand{handCounter}.log
            log.debug("Finishing hand {}", handCounter);
            ++handCounter;
            ThreadContext.put("hand", String.valueOf(handCounter));
        }

        log.debug("Game ends!");
    }

}
