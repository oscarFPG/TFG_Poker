package com.ucm.server.gameobjects;

import java.io.IOException;

import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPlayerActions;
import com.ucm.server.interfaces.IPlayerInfo;
import com.ucm.server.interfaces.IPlayerNotificator;


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
     * Interface to access player information.
     * This is used to send notifications to the player about the state of the game and other players.
     */
    protected IPlayerNotificator _playerInfo;
    

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


    /* Player methods */
    public String makePlay(int sb, int bb, int maxBet) {
        
        String action = null;
        try {
            action = _playerInfo.notifyMakePlay(sb, bb, maxBet, this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return action;
    }

    public int placeOnBetMoney() {
        
        int amount = _onBetMoney;
        _onBetMoney = 0;
        return amount;
    }

    public void wins() {
        _isWinner = true;
    }

    public void eliminate() {
        _isEliminated = true;
    }

    public void resetStates() {
        _isFold = false;
        _isWinner = false;
        _isAllIn = false;
        _lastCommand = "none";
    }


    public void receiveRole(PlayerRole r) {

        _role = r;
        try {
            _playerInfo.notifyPlayerRole(r);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveCard(Card c) {

        if(_numCards >= 2) {
            return;
        }

        _cards[_numCards++] = c;
        try {
            _playerInfo.notifyPlayerCard(c);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveTableCard(Card c) {

        try {
            _playerInfo.notifyTableCard(c);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receivePriceMoney(int amount) {
        _offBetMoney += amount;
    }

    public void retrieveCards() {
        
        _cards[0] = null;
        _cards[1] = null;
        _numCards = 0;
    }


    public void notifyTurnPlay() {

        try {
            _playerInfo.notifyTurnPlay();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyTurnWait() {

        try {
            _playerInfo.notifyTurnWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyOtherPlayerAction(IPlayerInfo other) {

        try {
            _playerInfo.notifyOtherPlayerAction(other);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyCurrentTurnPlayer(IPlayerInfo other) {
        
        try {
            _playerInfo.notifyCurrentTurnPlayer(other);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyOwnState() {
        try {
            _playerInfo.notifyOwnState(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyOtherPlayerState(IPlayerInfo other) {

        try {
            _playerInfo.notifyOtherPlayerState(other);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyEndPlayerState() {

        try {
            _playerInfo.notifyEndPlayerState();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyTotalPot(int total) {

        try {
            _playerInfo.notifyTotalPot(total);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyHandEndsByFolds() {

        try {
            _playerInfo.notifyHandEndsByFolds();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyRoundEnded() {

        try {
            _playerInfo.notifyRoundEnded();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyGameEnded() {

        try {
            _playerInfo.notifyGameEnded();
            if(isWinner())
                _playerInfo.notifyGameWinner();
            else
                _playerInfo.notifyGameLoser();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyGameKeeps() {

        try {
            _playerInfo.notifyGameKeeps();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyEquity(double equity) {

        _equity = equity;
        try {
            _playerInfo.notifyEquity(equity);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int putSmallBlindBet(int sb) {
        
        int bet = Math.min(sb, _offBetMoney);
        _onBetMoney += bet;
        _offBetMoney -= bet;

        try {
            _lastCommand = "small-blind";
            _playerInfo.notifySmallBlindBet(bet, this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return bet;
    }

    public int putBigBlindBet(int bb) {

        int bet = Math.min(bb, _offBetMoney);
        _onBetMoney += bet;
        _offBetMoney -= bet;

        try {
            _lastCommand = "big-blind";
            _playerInfo.notifyBigBlindBet(bet, this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return bet;
    }

    protected void decreaseOffBetMoney(int bet) {
        _offBetMoney = Math.clamp(_offBetMoney - bet, 0, _offBetMoney);
    }

    protected void increaseOnBetMoney(int bet) {
        _onBetMoney += bet;
    }

    
    /* Info methods */
    @Override
    public int getMoneyOnBet() {
        return _onBetMoney;
    }

    @Override
    public int getMoneyOffBet() {
        return _offBetMoney;
    }

    @Override
    public int getPlayerId() {
        return _id;
    }

    @Override
    public String getPlayerName() {
        return _name;
    }

    @Override
    public Card[] getPlayerCards() {
        return _cards.clone();
    }

    @Override
    public int getCardsCounter() {
        return _numCards;
    }

    @Override
    public boolean isFolded() {
        return _isFold;
    }

    @Override
    public boolean isWinner() {
        return _isWinner;
    }

    @Override
    public boolean isAllIn() {
        return _isAllIn;
    }

    @Override
    public boolean isEliminated() {
        return _isEliminated;
    }

    @Override
    public PlayerRole getRole() {
        return _role;
    }

    @Override
    public String getLastCommand() {
        return _lastCommand;
    }

}