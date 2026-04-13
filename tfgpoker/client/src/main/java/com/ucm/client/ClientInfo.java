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

    
    public Image getAvatar(int size) {
        if (name == null) return null;

        return avatarCache.computeIfAbsent(
            size,
            s -> AvatarGenerator.generate(name, s)
        );
    }

    public void onNameChanged(String newName) {
        this.name = newName;
        avatarCache.clear();
    }

}