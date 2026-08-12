package com.ucm.server.managers;

import java.util.HashMap;
import java.util.Map;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.players.AgentCFR;
import com.ucm.server.players.ClassifierFSM;
import com.ucm.server.players.GeminiLLM;
import com.ucm.server.players.LlamaPokerLLM;


/**
 * The BotManager class is responsible for managing the registration and creation of different types of bots in the poker game.
 * It maintains a mapping of bot IDs to their corresponding Bot instances, allowing for easy retrieval and creation of bots based on their unique identifiers.
 * The BotManager class is designed as a singleton, ensuring that only one instance of the manager exists throughout the application.
 * It provides methods to register new bots and create bot instances based on their IDs.
 */
public class BotManager {

    private static final Map<Integer, Bot> BOTS = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private BotManager() {}

    /**
     * Static block to register the available bot types with their corresponding IDs.
     * This block is executed when the class is loaded, ensuring that the bot types are registered before any bot creation requests are made.
     */
    static {
        register(GameType.BOT_GEMINI, new GeminiLLM(null));
        register(GameType.BOT_LLAMA, new LlamaPokerLLM(null));
        register(GameType.BOT_NN_MODEL_1, new AgentCFR());
        register(GameType.BOT_FSM_1, new ClassifierFSM());
    }

    /**
     * Registers a bot with the specified ID and Bot instance.
     * This method is private and is called internally to populate the BOTS map with the available
     * @param botId the unique identifier for the bot type
     * @param bot the Bot instance representing the bot type
     */
    private static void register(int botId, Bot bot) {
        BOTS.put(botId, bot);
    }

    /**
     * Creates a new Bot instance based on the specified bot ID.
     * If the bot ID is not registered, an IllegalArgumentException is thrown.
     * @param botId the unique identifier for the bot type
     * @return a new Bot instance corresponding to the specified bot ID
     * @throws IllegalArgumentException if the bot ID is not registered
     */
    public static Bot createBot(int botId) {
        Bot bot = BOTS.get(botId);
        if(bot == null){
            throw new IllegalArgumentException("Unknown bot id:" + botId);
        }
        return bot;
    }

}
