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
        1- CREO EL MAZO
        2- CREO LOS JUGADORES
        3- ASIGNO ROLES A LOS JUGADORES
        4- REPARTO CARTAS A CADA JUDADOR
        5- REPARTO CARTAS A LA MESA

        */

        _game.addPlayer( new Player("Valeria") );
        
    }
    
    public void run(){

        configureGame();
    }
    
}