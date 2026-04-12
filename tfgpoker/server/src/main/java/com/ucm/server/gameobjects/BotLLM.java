package com.ucm.server.gameobjects;

import com.ucm.common.gameobjects.PlayerRole;

/**
 * Abstract class that represents a poker bot powered by a Large Language Model (LLM).
 * 
 * <p>
 * This class extends {@link Bot} and introduces the concept of an external
 * LLM provider through an API key or a local model one.
 * </p>
 * 
 * <p>
 * It defines the required interface for bots that rely on language models
 * to make decisions, including:
 * </p>
 * <ul>
 * <li>Game action decision making</li>
 * <li>Game state notifications</li>
 * <li>Tracking opponent actions</li>
 * </ul>
 * 
 * <p>
 * Concrete implementations (such as online or local LLM bots) must define
 * how decisions are generated and how game events are processed.
 * </p>
 * 
 * @see BotLLMOnline
 */
public abstract class BotLLM extends Bot {

    /**
     * API key used to access the LLM provider.
     */
    protected String _apiKey;

    /**
     * Default constructor.
     */
    public BotLLM() {
        super();
    }

    /**
     * Constructs an LLM-based bot with basic configuration.
     * 
     * @param id     player identifier
     * @param name   player name
     * @param money  initial stack
     * @param apiKey API key for the LLM provider
     */
    public BotLLM(int id, String name, int money, String apiKey) {
        super(id, name, money);
        _apiKey = apiKey;
    }

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

    /** Notifies that the player must wait for their turn */
    public abstract void notifyTurnWait();

    /** Notifies that it is the player's turn to act */
    public abstract void notifyTurnPlay();

    /** Notifies that a betting round has ended */
    public abstract void notifyRoundEnded();

    /** Notifies that a hand has ended */
    public abstract void notifyHandEnded();

    /** Notifies that the game has ended */
    public abstract void notifyGameEnded();

    /** Notifies that the game continues */
    public abstract void notifyGameKeeps();

    /** Notifies that the player has won the hand */
    public abstract void notifyHandWinner();

    /** Notifies that the player has lost the hand */
    public abstract void notifyHandLoser();

    /** Notifies that the player has won the game */
    public abstract void notifyGameWinner();

    /** Notifies that the player has lost the game */
    public abstract void notifyGameLoser();

    /** Notifies that the hand ended due to folds */
    public abstract void notifyHandEndsByFolds();

    /**
     * Notifies the bot about an action performed by another player.
     * 
     * @param role   role of the player performing the action
     * @param action action performed (e.g., fold, call, raise)
     * @param amount amount associated with the action (if applicable)
     */
    public abstract void notifyPlayerAction(PlayerRole role, String action, double amount);
}