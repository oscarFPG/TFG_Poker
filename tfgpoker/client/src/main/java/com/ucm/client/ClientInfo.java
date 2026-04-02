package com.ucm.client;

import java.net.Socket;

import com.ucm.common.GameConfig;

public class ClientInfo{
    
    public GameConfig gameConfig = new GameConfig();

    public String ip;
    public String name;
    public Socket socket;

    public static ClientInfo instance;

    private ClientInfo() {}

    public static ClientInfo getInstance() {

        if(instance == null) {
            instance = new ClientInfo();
        }
        
        return instance;
    }
}