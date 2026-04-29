package com.ucm.common;

import java.net.Socket;

public class Spectator {
 
    public Socket socket;
    public String name;

    public Spectator(Socket s, String n) {
        socket = s;
        name = n;
    }

}