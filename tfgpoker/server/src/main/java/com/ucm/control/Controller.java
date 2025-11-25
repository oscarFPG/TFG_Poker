package com.ucm.control;

import java.io.IOException;

import com.ucm.exceptions.OnlyOnePlayerLeftException;
import com.ucm.gameobjects.Player;
import com.ucm.logic.Game;


public class Controller {

    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;

    public Controller(Game game) {
        _game = game;
    }

    private void configureGame() {
        
    }

    public void run() {

        configureGame();

        // Game loop
        _game.assignRolesToAllPlayers();
        int i = 0;
        while (i < 5 && !_game.isGameFinished()) {

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
            catch(IOException e){
                // TODO : Revisar bien esto, es un problema serio
            }
            catch (OnlyOnePlayerLeftException e) {
                _game.giveRewardToWinner();
            }

            // Devolver todas las cartas al mazo, restablecer jugadores que han 'foldeado',
            // etc...
            _game.restartRound();
            _game.passTurn();
            ++i;
        }
    }

}
