package com.ucm.server.players;

import java.net.Socket;


public class Spectator extends HumanPlayer {

    private static final String DEFAULT_NAME = "Spectator";

    public Socket _spectatorSocket;
    public String _name;


    public Spectator(Socket socket) {
        super(socket);
        _name = DEFAULT_NAME;
        _spectatorSocket = socket;
    }

}