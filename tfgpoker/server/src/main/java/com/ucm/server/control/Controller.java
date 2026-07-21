package com.ucm.server.control;


import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.logic.Game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Controller {

    private static final Logger log = LogManager.getLogger(Controller.class);

    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;


    public Controller(Game game) {
        _game = game;
    }

    /**
     * Controller constructor only for debugging purposes.
     * It will be used in the local mode of the server, where no clients are needed.
     * @param game The game instance to control.
     * @param numPlayers The number of local players to add to the game.
     */
    public Controller(Game game, int numPlayers) {
        _game = game;
    }


    public void run() throws CancelGameException {

        log.debug("Game starts!");
        runGame();
        log.debug("Game ends!");
    }

    private void runGame() throws CancelGameException {

        int handCounter = 0;
        boolean endOfGame = false;

        //ThreadContext.put("match", "0");
        //ThreadContext.put("hand", String.valueOf(handCounter));

        while (!endOfGame) {

            log.debug("---- HAND_{} ----", handCounter);
            try {

                // Player roles
                _game.assignRolesToAllPlayers();

                // Pre-flop (2)
                log.debug("---- PRE-FLOP ----");
                _game.shareOutCardsToAllPlayers();
                _game.updateEquity();
                _game.playHand();

                // Flop (3)
                log.debug("---- FLOP ----");
                _game.addCardToTable();
                _game.addCardToTable();
                _game.addCardToTable();
                _game.updateEquity();
                _game.playHand();

                // Turn (4)
                log.debug("---- TURN ----");
                _game.addCardToTable();
                _game.updateEquity();
                _game.playHand();

                // River (5)
                log.debug("---- RIVER ----");
                _game.addCardToTable();
                _game.updateEquity();
                _game.playHand();

                // Showdown (6)
                log.debug("---- SHOWDOWN ----");
                _game.giveRewardToWinner();
            }
            catch (OnlyOnePlayerLeftException e) {
                log.debug("Showdown with only one player left");
                _game.giveRewardToWinner();
            }

            log.debug("---- ~HAND_{} ----", handCounter);
            endOfGame = _game.passTurn();

            // Logger configuration for the next hand -> Write on file match{0}_hand{handCounter}.log
            //log.debug("Finishing hand {}", handCounter);
            ++handCounter;
            //ThreadContext.put("hand", String.valueOf(handCounter));
        }

    }

}
