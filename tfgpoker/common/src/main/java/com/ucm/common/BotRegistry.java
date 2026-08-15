package com.ucm.common;

import java.util.List;

public final class BotRegistry {

    public static final int MAX_BOT_NAME_LENGTH = 10;
    private static final String BOT_IMAGE_PATH = "/images/bot.png";
    
    private BotRegistry() {}

    public static List<BotDescriptor> getAvailableBots() {
        return List.of(
            new BotDescriptor(
                GameType.BOT_GEMINI, 
                normalizeBotName("Gemini LLM"), 
                "Gemini Poker LLM", 
                "This bot is powered by Google's Gemini large language model (LLM). It reasons about the game state, evaluates possible actions, and selects moves dynamically instead of following fixed rules.", 
                BOT_IMAGE_PATH,
                true
            ),
            new BotDescriptor(
                GameType.BOT_LLAMA, 
                normalizeBotName("LlamaPokerBot"), 
                "Llama Poker LLM (Ollama)", 
                "This bot uses a locally executed AI language model (LLaMA) to analyze the current game state and make decisions. It runs entirely on the local machine, providing self-contained gameplay without requiring an internet connection.", 
                BOT_IMAGE_PATH, 
                true
            ),
            new BotDescriptor(
                GameType.BOT_NN_MODEL_1, 
                normalizeBotName("DeepCFR"), 
                "Red neuronal", 
                "Red neuronal CFR", 
                BOT_IMAGE_PATH, 
                false
            ),
            new BotDescriptor(
                GameType.BOT_FSM_1,
                normalizeBotName("BotFSM"),
                "Finit State Machine(SFM)",
                "Bot that uses a Finit State Machine(FSM)",
                BOT_IMAGE_PATH,
                false
            )
        );
    }

    public static String getBotName(int botId, int botIdentifier, BotStyle style) {
        return switch (botId) {
            case GameType.BOT_GEMINI -> "GeminiLLM#" + botIdentifier + "-" + style.getShorcut();
            case GameType.BOT_LLAMA -> "LlamaPokerLLM#" + botIdentifier + "-" + style.getShorcut();
            case GameType.BOT_NN_MODEL_1 -> "DeepCFR#" + botIdentifier + "-" + style.getShorcut();
            case GameType.BOT_FSM_1 -> "FSM#" + botIdentifier + "-" + style.getShorcut();
            default -> "Bot-" + botId + "-" + botIdentifier;
        };
    }

    private static String normalizeBotName(String botName) {

        if(botName == null)
            return "defaultBot";

        if(botName.length() <= MAX_BOT_NAME_LENGTH)
            return botName;
        
        return botName.substring(0, MAX_BOT_NAME_LENGTH);
    }

}
