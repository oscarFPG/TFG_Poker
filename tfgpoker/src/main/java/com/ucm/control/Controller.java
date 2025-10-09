package com.ucm.control;

import com.ucm.gameobjects.Player;
import com.ucm.logic.Game;


public class Controller{
    
    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;
    
    public Controller(Game game){
        _game = game;
    }
    
    
    private void configureGame(){
        
        /*

        // PASOS
        1- CREO LOS JUGADORES
        3- ASIGNO ROLES A LOS JUGADORES
        4- REPARTO CARTAS A CADA JUDADOR
        5- REPARTO CARTAS A LA MESA

        */

        _game.addPlayer( new Player(0, "Valeria", 1000) );
        _game.addPlayer( new Player(1, "Oscar", 1000) );
        _game.addPlayer( new Player(2, "Carla", 1000) );
    }
    
    public void run(){

        configureGame();
        
        _game.assignRolesToAllPlayers();
        while(!_game.isGameFinished()){
            
            // First hand
            _game.shareOutAllCards();
            _game.playHand();
            
            // Adding 3 cards to table and play
            _game.addCardToTable();
            _game.addCardToTable();
            _game.addCardToTable();
            _game.playHand();

            // Add 1 card to table and play
            _game.addCardToTable();
            _game.playHand();
            
            // Add 1 card to table
            _game.addCardToTable();
            _game.playHand();

            // Select winner
            // _game.selectWinner();
        }
    }
    
}
