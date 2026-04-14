package com.ucm.server.players;

import java.io.IOException;

import com.ucm.common.GameType;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.interfaces.IPokerPlayer;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucm.server.gameobjects.BotLLMOnline;

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
        this._idBot = GameType.BOT_GEMINI;
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
    public GeminiLLM(int id, int money, String apiKey) {
        super(id, "GeminiLLM", money, apiKey);
        this._idBot = GameType.BOT_GEMINI;

        if (apiKey == null) {
            apiKey = loadApiKey();
        }

        gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .build();
    }

    /**
     * Loads the Gemini API key from a {@code credentials.json} file.
     * 
     * <p>
     * The file must be located in the classpath and contain a field
     * named {@code GEMINI_API_KEY}.
     * </p>
     * 
     * @return API key as {@link String}
     * @throws RuntimeException if the file or key cannot be loaded
     */
    private String loadApiKey() {
        try {
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("credentials.json");

            if (input == null) {
                throw new RuntimeException("credentials.json not found");
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> json = mapper.readValue(input, Map.class);

            String key = json.get("GEMINI_API_KEY");

            if (key == null) {
                throw new RuntimeException("GEMINI_API_KEY not found");
            }

            return key;

        } catch (Exception e) {
            throw new RuntimeException("Error loading Gemini API key", e);
        }
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
    public void notifyPlayerAction(PlayerRole role, String action, double amount) {
    }

    @Override
    public void notifyOtherPlayerAction(IPokerPlayer p) {}

    @Override
    public void notifyPlayerState(final IPokerPlayer player, boolean last) throws IOException {
    }

    @Override
    public void notifyTotalPot(int total) throws IOException {
    }
	@Override
	public Bot create(int ID, int initialMoney) {

        String key = loadApiKey();
		return new GeminiLLM(ID, initialMoney, key);
	}
    
}
