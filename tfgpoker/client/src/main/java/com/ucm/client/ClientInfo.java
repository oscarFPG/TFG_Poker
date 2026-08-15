package com.ucm.client;

import java.net.Socket;
import java.util.ArrayList;
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

    public List<PlayerInfo> playerPositions = new ArrayList<>();

    private static ClientInfo instance;

    private Map<Integer, Image> avatarCache = new HashMap<>();


    private ClientInfo() {}

    public static ClientInfo getInstance() {

        if(instance == null) {
            instance = new ClientInfo();
        }
        
        return instance;
    }

    
    public Image getAvatar(String name, int size) {
        
        if(name.startsWith("GeminiLLM")) {
            return AvatarGenerator.generateBot(AvatarType.BOT_GEMINI, size);
        }
        else if(name.startsWith("LlamaPoker")) {
            return AvatarGenerator.generateBot(AvatarType.BOT_LLAMA, size);
        }
        else if(name.startsWith("DeepCFR")) {
            return AvatarGenerator.generateBot(AvatarType.BOT_FSM_1, size);
        }
        else if(name.startsWith("FSM")){
            return AvatarGenerator.generateBot(AvatarType.BOT_NN_MODEL_1, size);
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