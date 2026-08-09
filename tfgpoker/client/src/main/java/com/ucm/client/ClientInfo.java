package com.ucm.client;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ucm.common.GameConfig;
import com.ucm.common.PlayerInfo;

import javafx.scene.image.Image;



public class ClientInfo {
    
    public GameConfig gameConfig = new GameConfig();

    public int id = -1; // -1 used as 'not assigned'
    public String ip;
    public String name;
    public Socket socket;
    public boolean isHost;

    public List<PlayerInfo> playerPositions;

    private static ClientInfo instance;

    private Map<Integer, Image> avatarCache = new HashMap<>();


    private ClientInfo() {}

    public static ClientInfo getInstance() {

        if(instance == null) {
            instance = new ClientInfo();
        }
        
        return instance;
    }

    
    public Image getAvatar(String name, int size, boolean isBot) {
        // hacer busqueda para que si no encuentra uno Image con ese nombre, genere un avatar
        if(isBot) {
            return AvatarGenerator.generateBot(name, size);
        }
        else {
            return AvatarGenerator.generateHuman(name, size);
        }
    }

    public void onNameChanged(String newName) {
        this.name = newName;
        avatarCache.clear();
    }

    public void clearInfo() {
        instance = null;
    }

}