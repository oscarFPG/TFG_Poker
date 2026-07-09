package com.ucm.server.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.players.AgentCFR;
import com.ucm.server.players.GeminiLLM;
import com.ucm.server.players.LlamaPokerLLM;


public class BotManager {

    private static final Map<Integer, Supplier<Bot>> BOTS = new HashMap<>();

    private BotManager(){}

    static {
        register(GameType.BOT_GEMINI, GeminiLLM::new);
        register(GameType.BOT_LLAMA, LlamaPokerLLM::new);
        register(GameType.BOT_NN_MODEL_1, AgentCFR::new);
    }

    private static void register(int botId, Supplier<Bot> factory){
        BOTS.put(botId, factory);
    }

    public static Bot createBot(int botId){
        Supplier<Bot> factory = BOTS.get(botId);
        if(factory == null){
            throw new IllegalArgumentException("Unknown bot id:" + botId);
        }
        return factory.get();
    }

}
