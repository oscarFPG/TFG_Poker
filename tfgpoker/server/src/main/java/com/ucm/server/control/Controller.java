package com.ucm.server.control;


import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.logic.Game;
import com.ucm.server.logic.Timer;
import com.ucm.common.BotStruct;
import com.ucm.common.ClientStruct;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Controller {

    private static final Logger log = LogManager.getLogger(Controller.class);

    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;

    private Timer _timer;


    public Controller(Game game) {
        _game = game;
        _timer = game.getGameTimerConfiguration();
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
        boolean allowIncrease = false;
        boolean endOfGame = false;

        //ThreadContext.put("match", "0");
        //ThreadContext.put("hand", String.valueOf(handCounter));

        _game.assignRolesToAllPlayers();    
        while (!endOfGame) {

            log.debug("Starting hand {}", handCounter);
            try {
                
                if( _timer != null && !_timer.isRunning() ) {
                    
                    if(allowIncrease)
                        _game.increaseBlinds();

                    _timer.restart();
                    allowIncrease = true;
                }

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
            //ThreadContext.put("hand", String.valueOf(handCounter));
        }

    }

}