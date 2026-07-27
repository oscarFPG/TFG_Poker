package com.ucm.server.gameobjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPlayerInfo;
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
    protected int _botID;

    /**
     * Community cards on the table.
     */
    protected List<Card> table;

    /**
     * History of actions in the current hand.
     */
    protected List<String> actionHistory;

    /**
     * Small blind value.
     */
    protected int _smallBlind;

    /**
     * Big blind value.
     */
    protected int _bigBlind;

    /**
     * Max bet in the current hand
     */
    protected int _maxBet;

    /**
     * Total pot in the table
     */
    protected int _totalPot;

    /**
     * Estimated probability of winning the hand.
     */
    protected double _equity;

    /**
     *  Communication bridge between the poker player and the player controller
     */
    protected IPlayerInfo _player;

    /**
     * Playing style used by this bot.
     * By default, bots use a balanced playing style.
     */
    protected BotStyle _style = BotStyle.DEFAULT;


    public Bot(final int botID) {
        _botID = botID;
        table = new ArrayList<>();
        actionHistory = new ArrayList<>();
        _style = BotStyle.DEFAULT;
    }

    /**
     * Constructs a bot with a given identifier.
      *
     * @param botID unique identifier for the bot
     */
    public Bot(final int botID, BotStyle style) {
        _botID = botID;
        table = new ArrayList<>();
        actionHistory = new ArrayList<>();
        _style = (style == null) ? BotStyle.DEFAULT : style;
    }


    /**
     * Maps a {@link PlayerRole} to a standard poker position string.
     * 
     * @param role player role
     * @return position string (BTN, SB, BB, UTG, etc.)
     */
    protected String mapRole(PlayerRole role) {
        return switch (role) {
            case DEALER -> "BTN";
            case SMALL_BLIND -> "SB";
            case BIG_BLIND -> "BB";
            case UNDER_THE_GUN -> "UTG";
            case UNDER_THE_GUN_1 -> "UTG+1";
            case UNDER_THE_GUN_2 -> "UTG+2";
            case LOJACK -> "LJ";
            case HIJACK -> "HJ";
            case CUT_OFF -> "CO";
            default -> "UNKNOWN";
        };
    }

    /**
     * Returns the formatted action history.
     * 
     * @return {@link String} with all actions or "None" if empty
     */
    protected String getHistory() {
        return actionHistory.isEmpty() ? "None" : String.join(", ", actionHistory);
    }


    /**
     * Get the bot ID, which is used to identify it between other bots.
     * This cannot and must not be used to identify player during the game.
     * 
     * @return bot ID
     */
    public int getIdBot() {
        return _botID;
    }

    @Override
    public BotStyle getStyle() {
        return _style;
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
    public abstract Bot create(BotStyle style);
    

    /* ============== Communication methods ============== */
    @Override
    public void notifySmallBlindBet(int amount, IPlayerInfo player) throws IOException {
        _smallBlind = amount;
    }

    @Override
    public void notifyBigBlindBet(int amount, IPlayerInfo player) throws IOException {
        _bigBlind = amount;
    }

    @Override
    public void notifyTableCard(Card c) throws IOException {
        table.add(c);
    }

    @Override
    public void notifyTotalPot(int total) throws IOException {
        _totalPot = total;
    }

    @Override
    public void notifyOtherPlayerAction(IPlayerInfo other) throws IOException {
        
        String action = other.getLastCommand();
        if(action.equals(GameType.RAISE_ACTION_FULL) || action.equals(GameType.ALL_IN_ACTION_FULL)) {
            actionHistory.add( mapRole(other.getRole()) + " " + action + " " + other.getMoneyOnBet() );
        }
        else {
            actionHistory.add( mapRole(other.getRole()) + " " + action );
        }
    }

    @Override
    public void notifyEquity(double equity) throws IOException {
        _equity = equity; 
    }

    @Override public void notifyPlayerRole(PlayerRole role) throws IOException {}
    @Override public void notifyPlayerCard(Card c) throws IOException {}
    @Override public void notifyOwnState(IPlayerInfo player) throws IOException {}
    @Override public void notifyOtherPlayerState(IPlayerInfo other) throws IOException {}
    @Override public void notifyEndPlayerState() throws IOException {}
    @Override public void notifyCurrentTurnPlayer(IPlayerInfo player) throws IOException {}
    @Override public void notifyTurnWait() throws IOException {}
    @Override public void notifyTurnPlay() throws IOException {}
    @Override public void notifyRoundEnded() throws IOException {}
    @Override public void notifyHandEndsByFolds() throws IOException {}
    @Override public void notifyGameEnded() throws IOException {}
    @Override public void notifyGameKeeps() throws IOException {}
    @Override public void notifyGameWinner() throws IOException {}
    @Override public void notifyGameLoser() throws IOException {}

    @Override
    public String getPlayerType() {
        return "BOT";
    }

    @Override
    public String getPlayerModel() {
        return "-";
    }
}