package com.ucm.control;

import java.util.List;

import java.io.IOException;

import com.ucm.exceptions.OnlyOnePlayerLeftException;
import com.ucm.gameobjects.Player;
import com.ucm.logic.Game;
import com.ucm.middleclasses.ClientStructGame;


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

        while(true){}
    }

    public void run() {

        // Game loop
        _game.assignRolesToAllPlayers();
        while (!_game.isGameFinished()) {

            try {
                
                // Pre-flop
                _game.shareOutCardsToAllPlayers();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // Flop
                _game.addCardToTable();
                _game.addCardToTable();
                _game.addCardToTable();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // Turn
                _game.addCardToTable();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // River
                _game.addCardToTable();
                if (Game.DEBUG)
                    _game.showStateDEBUG();
                _game.playHand();

                // Showdown
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
