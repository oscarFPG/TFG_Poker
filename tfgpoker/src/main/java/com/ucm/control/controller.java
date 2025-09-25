package com.ucm.control;

import com.ucm.logic.Game;


public class Controller{
    
    /**
     * Atributo que referencia la clase Game
     */
    private Game _game;
    
    Controller(Game game){
        _game = game;
    }
    
    private void configureGame(){
        
        _game.addPlayerToTable( /* Un jugador */ );
        _game.addPlayerToTable( /* Un jugador */ );
        _game.addPlayerToTable( /* Un jugador */ );
        _game.addPlayerToTable( /* Un jugador */ );
        _game.addPlayerToTable( /* Un jugador */ );
        _game.addPlayerToTable( /* Un jugador */ );
    }
    
    public void run(){
        
    }
    
}