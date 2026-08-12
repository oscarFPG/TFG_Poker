package com.ucm.server.gameobjects;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.exceptions.TurnTimeoutException;
import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.interfaces.IPlayerInfo;
import com.ucm.server.interfaces.IPlayerNotificator;
import com.ucm.server.statistics.PlayerExperimentData;


/**
 * Abstract class that represents a player in the poker game.
 * 
 * <p>
 * A player has an identifier, a name and a certain amount of money that is
 * divided into:
 * </p>
 * <ul>
 * <li>Money that has not been bet yet</li>
 * <li>Money currently in play (bet)</li>
 * </ul>
 * 
 * <p>
 * The player also has a role during each hand (see {@link PlayerRole}),
 * a set of cards (maximum of two) and several state flags such as fold,
 * all-in or elimination.
 * </p>
 * 
 * <p>
 * This class provides the base implementation of common poker actions
 * and game notifications, while delegating decision-making logic to
 * subclasses.
 * </p>
 */
public class Player implements IPlayerActions {

    public static int CURRENT_PLAYERS = 0;
    private static int INITIAL_MONEY = 0;

    /**
     * Player's unique identifier
     */
    protected int _id;

    /**
     * Player's name
     */
    protected String _name;

    /**
     * Player's total money that is safe.
     * This is money that the player has not bet yet.
     */
    protected int _onBetMoney;

    /**
     * Money the player has bet.
     * It is only truly lost when a round ends and the player has lost,
     * otherwise it is returned to the player.
     */
    protected int _offBetMoney;

    /**
     * Player's role during the current hand.
     * This role is assigned at the beginning of the game and reassigned
     * at the end of each hand.
     * 
     * Check {@link PlayerRole} for more information.
     */
    protected PlayerRole _role;

    /**
     * Represents the cards that the player has in their hand.
     * It can only contain zero or two cards.
     */
    protected Card[] _cards;

    /**
     * Number of cards the player currently has in hand.
     * It can only be 0, 1 or 2, although it should never be 1.
     */
    protected int _numCards;

    /**
     * Indicates whether the player has folded in the current hand.
     */
    protected boolean _isFold;

    /**
     * Indicates if the player is the winner of a hand or the game.
     * This flag should only be checked at the end of a hand or the game.
     */
    protected boolean _isWinner;

    /**
     * Indicates if the player has made an all-in in the current round.
     */
    protected boolean _isAllIn;

    /**
     * Indicates if the player is eliminated from the game.
     * Eliminated players cannot perform actions in the remaining game.
     */
    protected boolean _isEliminated;

    /**
     * Represents the player's equity in the current hand
     */
    protected double _equity;

    /**
     * Stores the last command executed by the player. Can be "call", "raise <amount>", "fold", "check", "small-blind", "big-blind", "all-in" or "none"
     */
    protected String _lastCommand;

    /**
     * Stores the rank of his last hand: Pair, Two-pair, etc...
     */
    protected String _lastHandRank;

    /**
     * Interface to access player information.
     * This is used to send notifications to the player about the state of the game and other players.
     * @see IPlayerNotificator
     */
    protected IPlayerNotificator _playerInfo;
    
    /**
     * Stores the player's experimental data for analysis and statistics.
     * This data includes information about the player's decisions, response times, and other relevant metrics.
     */
    private final PlayerExperimentData _experimentData = new PlayerExperimentData();


    /**
     * Default constructor.
     */
    public Player() {}

    /**
     * Constructs a player with an identifier, name and initial money.
     * 
     * @param id    unique identifier of the player
     * @param name  player's name
     * @param money initial amount of money
     */
    public Player(final int id, final String name, final int money, IPlayerNotificator playerInfo) {

        _id = id;
        _name = name;
        _offBetMoney = money;
        _onBetMoney = 0;
        _role = null;
        _cards = new Card[2];
        _numCards = 0;
        _playerInfo = playerInfo;

       
        _isFold = false;
        _isWinner = false;
        _isAllIn = false;
        _isEliminated = false;
        _equity = 0;
        _lastCommand = "none";
        _lastHandRank = "none";

        INITIAL_MONEY = money;
    }


    /* Actions */
    @Override
    public void call(int amount) {

        int resto = amount - _onBetMoney;
        increaseOnBetMoney(resto);
        decreaseOffBetMoney(resto);
        _lastCommand = GameType.CALL_ACTION_FULL;
    }

    @Override
    public void check() {
        _lastCommand = GameType.CHECK_ACTION_FULL;
    }

    @Override
    public void fold() {
        _isFold = true;
        _lastCommand = GameType.FOLD_ACTION_FULL;
    }

    @Override
    public void raise(int amount) {
        call(amount);
        _lastCommand = GameType.RAISE_ACTION_FULL;
    }

    @Override
    public void allIn() {
        increaseOnBetMoney(_offBetMoney);
        _offBetMoney = 0;
        _isAllIn = true;
        _lastCommand = GameType.ALL_IN_ACTION_FULL;
    }


    /* ----------------- Player methods ----------------- */
    /**
     * This method is called to notify the player about the current state of the game and to request an action.
     * It measures the time taken by the player to respond and stores it in the player's experimental data.
     * @param sb small blind amount
     * @param bb big blind amount
     * @param maxBet maximum bet amount in the current round
     * @param minRaise minimum raise amount in the current round
     * @return the action chosen by the player, which can be "call", "raise <amount>", "fold", "check", "small-blind", "big-blind", or "all-in"
     * @throws IOException if there is an error in the communication with the player
     * @throws TurnTimeoutException if the player takes too long to respond
     */
    public String makePlay(int sb, int bb, int maxBet, int minRaise) throws IOException, TurnTimeoutException {

        if(_playerInfo == null)
            return null;


        // Player makes an action
        // Check response time
        final long start = System.nanoTime();
        String action = _playerInfo.notifyMakePlay(sb, bb, maxBet, minRaise, this);
        final long end = System.nanoTime();

        // Save response time
        _experimentData.setDecisionTime((end - start) / 1_000_000);

        return action;
    }

    /**
     * Places the money that the player has bet on the table into the pot and resets the player's bet to zero.
     * @return the amount of money placed in the pot
     */
    public int placeOnBetMoney() {
        
        int amount = _onBetMoney;
        _onBetMoney = 0;
        return amount;
    }

    /**
     * Marks the player as the winner of the current hand or game.
     */
    public void wins() {
        _isWinner = true;
    }

    /**
     * Marks the player as eliminated from the game.
     */
    public void eliminate() {
        _isEliminated = true;
    }

    /**
     * Resets the player's state flags and experimental data for a new hand or game.
     */
    public void resetStates() {
        _isFold = false;
        _isWinner = false;
        _isAllIn = false;
        _lastCommand = "none";
        _experimentData.reset();
    }


    /* ----------------- Receive methods ----------------- */
    /**
     * Receives the role assigned to the player for the current hand.
     * Also notifies the player about their role through the {@link IPlayerNotificator} interface.
     * @param r the role assigned to the player
     * @throws IOException if there is an error in the communication with the player
     */
    public void receiveRole(PlayerRole r) throws IOException {

        _role = r;
        if(_playerInfo != null)
            _playerInfo.notifyPlayerRole(r);
    }

    /**
     * Receives a card to the player's hand.
     * The player can have a maximum of two cards in hand.
     * Also notifies the player about the received card through the {@link IPlayerNotificator} interface.
     * @param c the card to be received by the player
     * @throws IOException if there is an error in the communication with the player
     */
    public void receiveCard(Card c) throws IOException {

        if(_numCards >= 2) {
            return;
        }

        _cards[_numCards++] = c;
        if(_playerInfo != null)
            _playerInfo.notifyPlayerCard(c);
    }

    /**
     * Notifies the player about the received table card through the {@link IPlayerNotificator} interface.
     * @param c the card to be received by the player
     * @throws IOException if there is an error in the communication with the player
     */
    public void receiveTableCard(Card c) throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyTableCard(c);
    }

    /**
     * Receives the prize money won by the player and adds it to their off-bet money.
     * @param amount the amount of prize money won by the player
     */
    public void receivePriceMoney(int amount) {
        _offBetMoney += amount;
    }

    /**
     * Retrieves the cards from the player's hand, setting them to null and resetting the card count to zero.
     * This method is typically called at the end of a hand to clear the player's hand for the next round.
     */
    public void retrieveCards() {
        
        _cards[0] = null;
        _cards[1] = null;
        _numCards = 0;
    }

    /**
     * Receives the rank name of the player's last hand and stores it for reference.
     * If the received rank name is null, it defaults to "none".
     * @param rankName the name of the rank of the player's last hand (e.g., "Pair", "Two Pair", "Flush", etc.)
     */
    public void receiveHandRankName(String rankName) {
        _lastHandRank = (rankName == null) ? "none" : rankName;
    }


    /* ----------------- Notify methods ----------------- */
    /**
     * Notifies the player of their assigned player ID through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyPlayerID() throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyPlayerID(this);
    }

    /**
     * Notifies the player that it is their turn to play through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyTurnPlay() throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyTurnPlay();
    }

    /**
     * Notifies the player that they must wait for their turn to play through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyTurnWait() throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyTurnWait();
    }

    /**
     * Notifies the player about the action taken by another player through the {@link IPlayerNotificator} interface.
     * @param other the other player whose action is being notified
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyOtherPlayerAction(IPlayerInfo other) throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyOtherPlayerAction(other);
    }

    /**
     * Notifies the player that it is the current turn of another player through the {@link IPlayerNotificator} interface.
     * @param other the other player whose turn it is
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyCurrentTurnPlayer(IPlayerInfo other) throws IOException {
        
        if(_playerInfo != null)
            _playerInfo.notifyCurrentTurnPlayer(other);
    }

    /**
     * Notifies the player about their own state through the {@link IPlayerNotificator} interface.
     * @param receiveRank whether to receive the rank of the player's last hand
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyOwnState(final boolean receiveRank) throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyOwnState(this, receiveRank);
    }

    /**
     * Notifies the player about the state of another player through the {@link IPlayerNotificator} interface.
     * @param other the other player whose state is being notified
     * @param receiveRank whether to receive the rank of the other player's last hand
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyOtherPlayerState(IPlayerInfo other, final boolean receiveRank) throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyOtherPlayerState(other, receiveRank);
    }

    /**
     * Notifies the player that the current hand has ended and provides the final state of the player through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyEndPlayerState() throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyEndPlayerState();
    }

    /**
     * Notifies the player about the total pot amount in the current hand through the {@link IPlayerNotificator} interface.
     * @param total the total pot amount in the current hand
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyTotalPot(int total) throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyTotalPot(total);
    }

    /**
     * Notifies the player that the current hand has ended due to all other players folding through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyHandEndsByFolds() throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyHandEndsByFolds();
    }

    /**
     * Notifies the player that the current round has ended through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyRoundEnded() throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyRoundEnded();
    }

    /**
     * Notifies the player that the game has ended and whether they are the winner or loser through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyGameEnded() throws IOException {

        if(_playerInfo != null) {

            _playerInfo.notifyGameEnded();

            if(isWinner())
                _playerInfo.notifyGameWinner();
            else
                _playerInfo.notifyGameLoser();
        }
    }

    /**
     * Notifies the player that the game is ongoing and they are still in play through the {@link IPlayerNotificator} interface.
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyGameKeeps() throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyGameKeeps();
    }

    /**
     * Notifies the player about their current equity in the hand through the {@link IPlayerNotificator} interface.
     * @param equity the current equity of the player in the hand
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyEquity(double equity) throws IOException {

        _equity = equity;
        if(_playerInfo != null)
            _playerInfo.notifyEquity(equity);
    }

    /**
     * Notifies the player about the cards of another player through the {@link IPlayerNotificator} interface.
     * @param other the other player whose cards are being notified
     * @throws IOException if there is an error in the communication with the player
     */
    public void notifyOtherPlayerCards(IPlayerInfo other) throws IOException {

        if(_playerInfo != null)
            _playerInfo.notifyOtherPlayerCards(other);
    }

    /**
     * Places the small blind bet for the player, deducting it from their off-bet money and adding it to their on-bet money.
     * This method also updates the last command executed by the player to indicate that they have placed a small blind bet.
     * @param sb the small blind amount to be bet by the player
     * @return the actual amount of the small blind bet placed by the player, which may be less than the requested amount if the player does not have enough off-bet money 
     * @throws IOException if there is an error in the communication with the player
     */
    public int putSmallBlindBet(int sb) throws IOException {
        
        int bet = Math.min(sb, _offBetMoney);
        _onBetMoney += bet;
        _offBetMoney -= bet;
        _lastCommand = GameType.SMALL_BLIND_ACTION;

        if(_playerInfo != null)
            _playerInfo.notifySmallBlindBet(bet, this);

        return bet;
    }

    /**
     * Places the big blind bet for the player, deducting it from their off-bet money and adding it to their on-bet money.
     * This method also updates the last command executed by the player to indicate that they have placed a big blind bet.
     * @param bb the big blind amount to be bet by the player
     * @return the actual amount of the big blind bet placed by the player, which may be less than the requested amount if the player does not have enough off-bet money
     * @throws IOException if there is an error in the communication with the player
     */
    public int putBigBlindBet(int bb) throws IOException {

        int bet = Math.min(bb, _offBetMoney);
        _onBetMoney += bet;
        _offBetMoney -= bet;
        _lastCommand = GameType.BIG_BLIND_ACTION;

        if(_playerInfo != null)
            _playerInfo.notifyBigBlindBet(bet, this);

        return bet;
    }

    /**
     * Decreases the player's off-bet money by the specified bet amount, ensuring that it does not go below zero.
     * @param bet the amount to decrease from the player's off-bet money
     */
    protected void decreaseOffBetMoney(int bet) {
        _offBetMoney = Math.clamp(_offBetMoney - bet, 0, _offBetMoney);
    }

    /**
     * Increases the player's on-bet money by the specified bet amount.
     * @param bet the amount to increase the player's on-bet money
     */
    protected void increaseOnBetMoney(int bet) {
        _onBetMoney += bet;
    }

    
    /* ----------------- Info methods ----------------- */
    @Override public int getMoneyOnBet() { return _onBetMoney; }
    @Override public int getMoneyOffBet() { return _offBetMoney; }
    @Override public int getPlayerId() { return _id; }
    @Override public String getPlayerName() { return _name; }
    @Override public int getCardsCounter() { return _numCards; }
    @Override public boolean isFolded() { return _isFold; }
    @Override public boolean isWinner() { return _isWinner; }
    @Override public boolean isAllIn() { return _isAllIn; }
    @Override public boolean isEliminated() { return _isEliminated; }
    @Override public PlayerRole getRole() { return _role; }
    @Override public String getLastCommand() { return _lastCommand; }
    @Override public String getLastRankName() { return _lastHandRank; }
    @Override public Card[] getPlayerCards() { return _cards.clone(); }

    /**
     * Returns the player's experimental data, which includes information about the player's decisions, response times, and other relevant metrics for analysis and statistics.
     * @return the player's experimental data
     */
    public PlayerExperimentData getExperimentData() { return _experimentData; }

    /**
     * Returns the player's style, which is a representation of the player's behavior and decision-making tendencies in the game.
     * @return the player's style
     */
    public BotStyle getStyle() { return _playerInfo.getStyle(); }

    /**
     * Returns the player's type, which is a classification of the player based on their behavior and decision-making tendencies in the game.
     * @return the player's type
     */
    public String getPlayerType() { return _playerInfo.getPlayerType(); }

    /**
     * Returns the player's model, which is a representation of the player's decision-making process and strategy in the game.
     * @return the player's model
     */
    public String getPlayerModel() { return _playerInfo.getPlayerModel(); }
   
    /**
     * Returns the initial amount of money assigned to players at the start of the game.
     * @return the initial money assigned to players
     */
    public static int getInitialMoney() { return INITIAL_MONEY; }
}