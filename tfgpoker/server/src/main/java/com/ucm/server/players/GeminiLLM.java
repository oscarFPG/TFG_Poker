package com.ucm.server.players;

import java.io.IOException;

import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotLLMOnline;
import com.ucm.server.interfaces.IPokerPlayer;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;


/**
 * Concrete implementation of a poker bot powered by Google's Gemini LLM.
 * 
 * <p>
 * This class extends {@link BotLLMOnline} and connects the bot logic
 * to an external Gemini model using the LangChain4j library.
 * </p>
 * 
 * <p>
 * The bot generates decisions by:
 * </p>
 * <ul>
 * <li>Building a prompt with the current game state</li>
 * <li>Sending the prompt to the Gemini model</li>
 * <li>Receiving and returning the model's response</li>
 * </ul>
 * 
 * <p>
 * The API key can be provided directly or loaded automatically from a
 * {@code credentials.json} file located in the classpath.
 * </p>
 */
public class GeminiLLM extends BotLLMOnline {

    /**
     * Chat model instance used to interact with Gemini.
     */
    private ChatModel gemini;


    public GeminiLLM() {
        _idBot = GameType.BOT_GEMINI;
    }

    /**
     * Constructs a Gemini-based LLM bot.
     * 
     * <p>
     * If the API key is null, it will be loaded from a configuration file.
     * </p>
     * 
     * @param id     player identifier
     * @param money  initial stack
     * @param apiKey Gemini API key (optional)
     */
    public GeminiLLM(int id, int money) {
        super(id, "GeminiLLM", money);
        _idBot = GameType.BOT_GEMINI;

        gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(_apiKey)
                .modelName("gemini-2.5-flash")
                .build();
    }

    /**
     * Sends the prompt to the Gemini model and returns its response.
     * 
     * @param prompt input prompt describing the game state
     * @return model response as {@link String}
     */
    @Override
    protected String callModel(String prompt) {
        return gemini.chat(prompt);
    }

    /**
     * Returns a short description of the bot.
     * 
     * @return {@link String} describing the bot
     */
    @Override
    public String getDescription() {
        return "Gemini Poker LLM";
    }

    @Override
    protected String getCredentialKey() {
        return "GEMINI_API_KEY";
    }

	@Override
	public Bot create(int ID, int initialMoney) {
		return new GeminiLLM(ID, initialMoney);
	}
    
    public static String getGenericName() {
        return "Gemini LLM";
    }

    

}