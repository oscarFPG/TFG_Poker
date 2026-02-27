package com.ucm.server.control;

import java.io.IOException;
import java.util.List;

import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.logic.Game;
import com.ucm.server.middleclasses.ClientStructGame;


public class Controller {

    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;

    public Controller(Game game, List<ClientStructGame> players) {
        _game = game;
        addPlayersToGame(players);
    }


    private void addPlayersToGame(List<ClientStructGame> players) {

        int id = 0;
        for(ClientStructGame cs : players){
            _game.addPlayer( new Player(id, cs.name(), cs.socket(), 1000) );
            ++id;
        }
    }

    public void run() {

        System.out.print("Controller.run() method");

        // Start Game loop (1)
        _game.assignRolesToAllPlayers();
        while (!_game.isGameFinished()) {

            try {
                
                // Pre-flop (2)
                _game.shareOutCardsToAllPlayers();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // Flop (3)
                _game.addCardToTable();
                _game.addCardToTable();
                _game.addCardToTable();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // Turn (4)
                _game.addCardToTable();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // River (5)
                _game.addCardToTable();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // Showdown (6)
                _game.giveRewardToWinner();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
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
