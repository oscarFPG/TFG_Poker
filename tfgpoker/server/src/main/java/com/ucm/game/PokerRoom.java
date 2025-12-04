package com.ucm.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import com.ucm.control.Controller;
import com.ucm.logic.Game;
import com.ucm.middleclasses.DTOClient;


public class PokerRoom{

    private ServerSocket _roomSocket;

    private List<DTOClient> _clients;

    private String _ID;


    public PokerRoom(final String ID, final ServerSocket socket, final List<DTOClient> clients) throws IOException{
        _ID = ID;
        _roomSocket = socket;
        _clients = clients;
    }

    
    public void start(){

        Game game = new Game(_clients);
        Controller controller = new Controller(game);
        controller.run();
    }

}