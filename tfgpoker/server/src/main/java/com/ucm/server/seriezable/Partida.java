package com.ucm.server.seriezable;

import java.io.Serializable;

import com.ucm.server.logic.Game;

public class Partida implements Serializable {


    // HABRA QUE SERIALIZAR EL RESTO DE COMPONENTES
    // PROBLEMA CON LOS SOCKETS, NO PUEDEN SER REIEZABLES TAMBIEN
    // idea --> separar game de datos seriezables --> tener un gamestate con datos seriezables y que game sea solo sockets+ logica
    // habria que separar players tambien 
    
    private static final long serialVersionUID = 1L;

    private Game game;

    public Partida(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}