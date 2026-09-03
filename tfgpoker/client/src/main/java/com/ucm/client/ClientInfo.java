package com.ucm.client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ucm.common.GameConfig;
import com.ucm.common.PlayerInfo;

import javafx.scene.image.Image;

/**
 * Singleton class that holds information about the client, including game configuration, player ID, IP address, name, socket connection, host status, and player positions. It also manages avatar generation and caching based on player names.
 */
public class ClientInfo {
    
    /**
     * The game configuration for the client, which includes settings and parameters for the poker game.
     */
    public GameConfig gameConfig = new GameConfig();
    /**
     * The unique identifier for the client. Initialized to -1 to indicate that it has not been assigned yet.
     */
    public int id = -1;
    /**
     * The IP address of the client, used for network communication with the server.
     */
    public String ip;
    /**
     * The name of the client, which can be used for display purposes and identification in the game.
     */
    public String name;
    /**
     * The socket connection used for communication between the client and the server. This is essential for sending and receiving game data.
     */
    public Socket socket;
    /**
     * A boolean flag indicating whether the client is the host of the game. If true, the client has additional responsibilities, such as managing game state and player interactions.
     */
    public boolean isHost;
    /**
     * A list of PlayerInfo objects representing the positions of players in the game. This list is used to manage and display player information during gameplay.
     */
    public List<PlayerInfo> playerPositions = new ArrayList<>();
    /**
     * The singleton instance of the ClientInfo class. This ensures that there is only one instance of ClientInfo throughout the application, providing a centralized point for accessing client-related information.
     */
    private static ClientInfo instance;
    /**
     * A cache for storing generated avatar images based on player names and sizes. This map uses the player's ID as the key and the corresponding avatar image as the value, allowing for efficient retrieval of avatars without regenerating them each time.
     */
    private Map<Integer, Image> avatarCache = new HashMap<>();

    /**
     * Private constructor to prevent instantiation of the ClientInfo class from outside. This enforces the singleton pattern, ensuring that only one instance of ClientInfo can exist at any given time.
     */
    private ClientInfo() {}
    /**
     * Returns the singleton instance of the ClientInfo class. If the instance does not already exist, it creates a new one. This method provides a global point of access to the ClientInfo instance, allowing other parts of the application to retrieve client-related information as needed.
     * @return The singleton instance of the ClientInfo class.
     */
    public static ClientInfo getInstance() {

        if(instance == null) {
            instance = new ClientInfo();
        }
        
        return instance;
    }
    /**
     * Generates and returns an avatar image based on the provided player name and size. The method checks the prefix of the player's name to determine the type of avatar to generate. If the name starts with specific prefixes (e.g., "GeminiLLM", "LlamaPoker", "DeepCFR", "FSM"), it generates a corresponding bot avatar. Otherwise, it generates a human avatar using the player's name. The generated avatar is returned as an Image object.
     * @param name
     * @param size
     * @return Image object representing the generated avatar based on the player's name and specified size.
     */
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
    /**
     * Updates the client's name and clears the avatar cache. This method is called when the client's name changes, ensuring that any previously generated avatars are invalidated and will be regenerated based on the new name. It helps maintain consistency between the client's name and their corresponding avatar.
     * @param newName
     */
    public void onNameChanged(String newName) {
        this.name = newName;
        avatarCache.clear();
    }
    /**
     * Clears the singleton instance of the ClientInfo class. This method is used to reset the client information, effectively removing the current instance and allowing for a new instance to be created if needed. It is useful in scenarios where the client needs to be reinitialized or when switching between different clients.
     */
    public void clearInfo() {
        instance = null;
    }

}