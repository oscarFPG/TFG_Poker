package com.ucm.common;

import java.util.List;

public final class BotRegistry {
    
    private BotRegistry() {}

    public static List<BotDescriptor> getAvailableBots() {
        return List.of(
            new BotDescriptor(GameType.BOT_GEMINI, "Gemini LLM", "Gemini Poker LLM", "This bot is powered by Google's Gemini large language model (LLM). It reasons about the game state, evaluates possible actions, and selects moves dynamically instead of following fixed rules.", "/images/bot.png"),
            new BotDescriptor(GameType.BOT_LLAMA, "llamaPokerBot", "Llama Poker LLM (Ollama)", "This bot uses a locally executed AI language model (LLaMA) to analyze the current game state and make decisions. It runs entirely on the local machine, providing self-contained gameplay without requiring an internet connection.", "/images/bot.png")
        );
    }

    public static String getBotName(int botId, int botIdentifier) {
        return switch (botId) {
            case GameType.BOT_GEMINI -> "Gemini LLM #" + botIdentifier;
            case GameType.BOT_LLAMA -> "Llama Poker LLM #" + botIdentifier;
            default -> "Bot-" + botId + "-" + botIdentifier;
        };
    }

}
