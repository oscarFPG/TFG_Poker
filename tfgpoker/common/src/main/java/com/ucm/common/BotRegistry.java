package com.ucm.common;

import java.util.List;

/**
 * Utility class that provides access to the bots available in the application.
 * <p>
 * This registry contains the metadata of each supported bot, including its
 * identifier, display information, icon path, and style customization support.
 * It also provides helper methods for generating standardized bot names and
 * normalizing bot identifiers used in the user interface.
 * </p>
 */
public final class BotRegistry {

    /**
     * The maximum length allowed for a bot's name. If a bot name exceeds this length,
     */
    public static final int MAX_BOT_NAME_LENGTH = 10;
    /**
     * The path to the default bot image used for displaying bot icons in the user interface.
     */
    private static final String BOT_IMAGE_PATH = "/images/bot.png";
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private BotRegistry() {}

    /**
     * Returns the list of bots available in the application.
     * <p>
     * Each descriptor contains the bot metadata required for presentation
     * and configuration purposes.
     * </p>
     *
     * @return an immutable list containing the available bot descriptors
     */
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
    /**
     * Generates a formatted bot name that uniquely identifies a bot instance.
     * <p>
     * The generated name includes the bot type, a numerical identifier, and the associated style shortcut.
     * </p>
     * @param botId
     * @param botIdentifier
     * @param style
     * @return a formatted string representing the bot's name, e.g., "GeminiLLM#1-DF" for a Gemini bot with identifier 1 and default style.
     */
    public static String getBotName(int botId, int botIdentifier, BotStyle style) {
        return switch (botId) {
            case GameType.BOT_GEMINI -> "GeminiLLM#" + botIdentifier + "-" + style.getShorcut();
            case GameType.BOT_LLAMA -> "LlamaPokerLLM#" + botIdentifier + "-" + style.getShorcut();
            case GameType.BOT_NN_MODEL_1 -> "DeepCFR#" + botIdentifier + "-" + style.getShorcut();
            case GameType.BOT_FSM_1 -> "FSM#" + botIdentifier + "-" + style.getShorcut();
            default -> "Bot-" + botId + "-" + botIdentifier;
        };
    }
    /**
     * Normalizes a bot name so that it conforms to the maximum allowed length.
     * <p>
     * If the provided name is {@code null}, a default name is returned.
     * If the name exceeds {@link #MAX_BOT_NAME_LENGTH}, it is truncated.
     * </p>
     * 
     * @param botName
     * @return a normalized bot name that is guaranteed to be non-null and within the maximum length
     */
    private static String normalizeBotName(String botName) {

        if(botName == null)
            return "defaultBot";

        if(botName.length() <= MAX_BOT_NAME_LENGTH)
            return botName;
        
        return botName.substring(0, MAX_BOT_NAME_LENGTH);
    }

}