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
    
    /**
     * Determines the action to take during the player's turn.
     * 
     * @param sb     small blind amount
     * @param bb     big blind amount
     * @param maxBet current maximum bet
     * @return {@link String} representing the chosen action
     */
    public abstract String actionMakePlay(int sb, int bb, int maxBet);

    /** @param amount small blind amount */
    public abstract void notifySmallBlindBet(final int amount);

    /** @param amount big blind amount */
    public abstract void notifyBigBlindBet(final int amount);

    /** Notifies that the bot must wait for its turn */
    public abstract void notifyTurnWait();

    /** Notifies that it is the bot's turn to act */
    public abstract void notifyTurnPlay();

    /** Notifies that a betting round has ended */
    public abstract void notifyRoundEnded();

    /** Notifies that a hand has ended */
    public abstract void notifyHandEnded();

    /** Notifies that the game has ended */
    public abstract void notifyGameEnded();

    /** Notifies that the game continues */
    public abstract void notifyGameKeeps();

    /** Notifies that the bot has won the hand */
    public abstract void notifyHandWinner();

    /** Notifies that the bot has lost the hand */
    public abstract void notifyHandLoser();

    /** Notifies that the bot has won the game */
    public abstract void notifyGameWinner();

    /** Notifies that the bot has lost the game */
    public abstract void notifyGameLoser();

    /** Notifies that the hand ended due to folds */
    public abstract void notifyHandEndsByFolds();
}