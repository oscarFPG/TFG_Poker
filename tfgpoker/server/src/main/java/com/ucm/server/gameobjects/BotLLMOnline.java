package com.ucm.server.gameobjects;

import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Abstract class that represents an online LLM-based poker bot.
 * 
 * <p>
 * This class extends {@link BotLLM} and integrates an external Large Language Model
 * (LLM) to make poker decisions based on the current game state.
 * </p>
 * 
 * <p>
 * The bot builds a structured prompt containing:
 * </p>
 * <ul>
 * <li>Player hand and board cards</li>
 * <li>Stack and pot size</li>
 * <li>Blinds</li>
 * <li>Action history</li>
 * <li>Estimated equity</li>
 * </ul>
 * 
 * <p>
 * The prompt is sent to the model, and the response is parsed to extract a valid
 * poker action. The class ensures robustness by sanitizing invalid or unexpected
 * outputs from the model.
 * </p>
 * 
 * <p>
 * Subclasses must implement {@link #callModel(String)} to connect to a specific
 * LLM provider (e.g., Gemini, OpenAI, etc.).
 * </p>
 */
public abstract class BotLLMOnline extends BotLLM {

    /**
     * 
     */
    protected String _credentialKey;

    /**
     * API key used to access the LLM provider
     */
    protected String _apiKey;


    /**
     * Default empty constructor
     */
    public BotLLMOnline(int botID) {
        super(botID);
        _credentialKey = getCredentialKey();
        _apiKey = loadApiKey();
    }

   
    /**
     * Loads the API key from a {@code credentials.json} file.
     * 
     * <p>
     * The file must be located in the classpath and contain a field
     * named as the .
     * </p>
     * 
     * @return API key as {@link String}
     * @throws RuntimeException if the file or key cannot be loaded
     */
    protected String loadApiKey() throws RuntimeException {

        String key = null;
        try {
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("credentials.json");

            if (input == null)
                throw new RuntimeException("credentials.json not found");


            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> json = mapper.readValue(
                input,
                new TypeReference<Map<String, String>>() {}
            );

            key = json.get(_credentialKey);
            if (key == null)
                throw new RuntimeException( String.format("Credential key %s not found", _credentialKey) );

        }
        catch (Exception e) {
            throw new RuntimeException("Error loading Gemini API key", e);
        }

        return key;
    }


    /**
     * Method used to retrieve the key name inside the credentials.json file
     * @return credential key
     */
    protected abstract String getCredentialKey();

}