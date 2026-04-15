package com.ucm.server.gameobjects;

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
public abstract class Bot extends Player {

    protected int _idBot;

    /**
     * Default constructor.
     */
    public Bot() {
        super();
    }
    
    /**
     * Constructs a bot with an identifier, name and initial money.
     * 
     * @param id    player identifier
     * @param name  bot name
     * @param money initial stack
     */
    public Bot(int id, String name, int money) {
        super(id, name, money);
    }


    /**
     * Returns a full description of the bot, including its name and behavior.
     * 
     * @return {@link String} in the format "name : description"
     */
    public String getFullDescription() {
        return this.getPlayerName() + " : " + this.getDescription();
    }

    /**
     * Returns a textual description of the bot's behavior or strategy.
     * 
     * @return {@link String} describing the bot
     */
    public abstract String getDescription();
    
    public int getIdBot() {
        return _idBot;
    }


    /** 
     * Creates a specific instance of any kind of bot implementation with and ID and initial money
     * @param ID player identifier
     * @param initialMoney initial stack
     * @return a new instance of a bot ready to play
    */
    public abstract Bot create(final int ID, final int initialMoney);
}