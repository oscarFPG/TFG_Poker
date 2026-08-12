package com.ucm.server.control;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.logic.Game;

/**
 * Class that represents the Controller of the poker game.
 * It is responsible for managing the flow of the game, including player actions and game state transitions.
 */
public class Controller {

    private static final Logger log = LogManager.getLogger(Controller.class);

    /**
     * Reference to the game instance that this controller will manage.
     * @see Game
     */
    private Game _game;

    /**
     * Constructor for the Controller class.
     * @param game the game instance that this controller will manage
     */
    public Controller(Game game) {
        _game = game;
    }


    /**
     * Runs the poker game, managing the flow of hands and player actions until the game ends.
     * This method handles the different stages of a poker hand, including pre-flop, flop, turn, river, and showdown.
     * It also manages the transition between hands and checks for end-of-game conditions.
     * @throws CancelGameException if the game is cancelled by any reason or error
     */
    public void run() throws CancelGameException {

        int handCounter = 0;
        boolean endOfGame = false;

        _game.initialize();

        log.debug("Game starts!");
        while (!endOfGame) {

            log.debug("---- HAND_{} ----", handCounter);
            try {

                // Asign player roles
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
                _game.showdown();
            }
            catch (OnlyOnePlayerLeftException e) {
                
                log.debug("Showdown with only one player left");
                _game.showdown();
            }

            log.debug("---- ~HAND_{} ----", handCounter);
            endOfGame = _game.passTurn();

            ++handCounter;
        }

        log.debug("Game ends!");
    }

}