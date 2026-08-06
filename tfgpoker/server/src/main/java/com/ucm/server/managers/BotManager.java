package com.ucm.server.managers;

import java.util.HashMap;
import java.util.Map;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.players.AgentCFR;
import com.ucm.server.players.ClassifierFSM;
import com.ucm.server.players.GeminiLLM;
import com.ucm.server.players.LlamaPokerLLM;


public class BotManager {

    private static final Map<Integer, Bot> BOTS = new HashMap<>();

    private BotManager(){}

    static {
        register(GameType.BOT_GEMINI, new GeminiLLM(null));
        register(GameType.BOT_LLAMA, new LlamaPokerLLM(null));
        register(GameType.BOT_NN_MODEL_1, new AgentCFR());
        register(GameType.BOT_FSM_1, new ClassifierFSM());
    }

    private static void register(int botId, Bot bot){
        BOTS.put(botId, bot);
    }

    public static Bot createBot(int botId){
        Bot bot = BOTS.get(botId);
        if(bot == null){
            throw new IllegalArgumentException("Unknown bot id:" + botId);
        }
        return bot;
    }

}
