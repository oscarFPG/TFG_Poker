package com.ucm.client;

import java.net.Socket;
import java.util.List;

import com.ucm.common.GameConfig;
import com.ucm.common.PlayerInfo;



public class ClientInfo {
    
    public GameConfig gameConfig = new GameConfig();

    public int id = -1; // -1 used as 'not assigned'
    public String ip;
    public String name;
    public Socket socket;
    public boolean isHost;

    public List<PlayerInfo> playerPositions;

    private static ClientInfo instance;

    private ClientInfo() {}

    public static ClientInfo getInstance() {

        if(instance == null) {
            instance = new ClientInfo();
        }
        
        return instance;
    }
}