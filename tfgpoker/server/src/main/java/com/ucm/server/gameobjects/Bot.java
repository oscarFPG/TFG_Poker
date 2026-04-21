package com.ucm.server.gameobjects;

import com.ucm.server.interfaces.IPlayerNotificator;

/**
 * Abstract class that represents an automated poker player (bot).
 * 
 * <p>
 * This class extends {@link Player} and defines the base structure
 * for all non-human players in the game.
 * </p>
 * 
 * <p>
 * A bot is responsible for:
 * </p>
 * <ul>
 * <li>Making decisions during its turn</li>
 * <li>Reacting to game events through notifications</li>
 * </ul>
 * 
 * <p>
 * Different types of bots (e.g., rule-based or LLM-based) should extend
 * this class and implement their own decision-making logic.
 * </p>
 * 
 * @see Player
 * @see BotLLM
 */
public abstract class Bot implements IPlayerNotificator {

    /**
     * Unique identifier for the bot, used to distinguish it from other bots.
     */
    private int _botID;



    /**
     * Constructs a bot with a given identifier.
      *
     * @param botID unique identifier for the bot
     */
    public Bot(final int botID) {
        _botID = botID;
    }


    public int getIdBot() {
        return _botID;
    }


    /**
     * Returns a full description of the bot, including its name and behavior.
     * 
     * @return {@link String} in the format "name : description"
     */
    public abstract String getFullDescription();

    /**
     * Returns a textual description of the bot's behavior or strategy.
     * 
     * @return {@link String} describing the bot
     */
    public abstract String getDescription();
    
    /** 
     * Creates a specific instance of any kind of bot implementation with and ID and initial money
     * @param ID player identifier
     * @param initialMoney initial stack
     * @return a new instance of a bot ready to play
    */
    public abstract Bot create();
    
}