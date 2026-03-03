package com.ucm.server.control;


import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.logic.Game;
import com.ucm.server.middleclasses.ClientStructGame;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Controller {

    private static final Logger log = LogManager.getLogger(Controller.class);

    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;

    public Controller(Game game, List<ClientStructGame> players) {
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


    private void addPlayersToGame(List<ClientStructGame> players) {

        int id = 0;
        for(ClientStructGame cs : players){
            _game.addPlayer( new Player(id, cs.name(), cs.socket(), 1000) );
            ++id;
        }
    }

    private void addPlayersToGameLocally(int numPlayers) {

        for(int i = 0; i < numPlayers; ++i){
            _game.addPlayer( new Player(i, "Player" + i, null, 1000) );
        }
    }

    public void run() {

        // Start Game loop (1)
        _game.assignRolesToAllPlayers();
        while (!_game.isGameFinished()) {

            try {
                
                // Pre-flop (2)
                _game.shareOutCardsToAllPlayers();
                _game.playHand();

                // Flop (3)
                _game.addCardToTable();
                _game.addCardToTable();
                _game.addCardToTable();
                _game.playHand();

                // Turn (4)
                _game.addCardToTable();
                _game.playHand();

                // River (5)
                _game.addCardToTable();
                _game.playHand();

                // Showdown (6)
                _game.giveRewardToWinner();
            }
            catch (OnlyOnePlayerLeftException e) {
                _game.giveRewardToWinner();
            }

            // Devolver todas las cartas al mazo, restablecer jugadores que han 'foldeado',
            // etc...
            _game.restartRound();
            _game.passTurn();
        }
    }

}
