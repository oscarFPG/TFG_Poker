package com.ucm.server.gameobjects;


import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerPlayer;


public abstract class Player implements IPokerPlayer {

    /**
     * Player's unique identifier
     */
    protected int _id;

    /**
     * Player's name
     */
    protected String _name;

    /**
     * Player's total money that is safe
     * This is money that the player has not bet yet.
     */
    protected int _onBetMoney;

    /**
     * Money the player has bet.
     * It is only truly lost only when a round ends and the player has lost,
     * otherwise it is returned to the player.
     */
    protected int _offBetMoney;

    /**
     * Player's role during the current hand.
     * This role is assigned at the beginning of the game and it is reassigned at
     * the end of each hand.
     * Check {@link PlayerRole} for more information about the possible roles.
     */
    protected PlayerRole _role;

    /**
     * Represents the cards that the player has in his hand.it can only hold zero or
     * two cards.
     */
    protected Card[] _cards;

    /**
     * Number of cards the player currently has in hand.
     * It can only be 0, 1 or 2, but it should never be 1 since the player should
     * always have two cards or none.
     */
    protected int _numCards;

    /**
     * Indicates whether the player has folded in the current hand.
     */
    protected boolean _isFold;

    /**
     * Indicates if the player is the winner of a hand or the game.
     * This flag should only be read at the end of a hand o the game.
     */
    protected boolean _isWinner;

    /**
     * Indicates if the player has made all-in in the current round
     */
    protected boolean _isAllIn;

    /**
     * Indicates if the player is eliminated from the game.
     * This means that cannot make any action during the reamining game
     */
    protected boolean _isEliminated;


    public Player() {}

    public Player(final int id, final String name, final int money) {

        _id = id;
        _name = name;
        _offBetMoney = money;
        _onBetMoney = 0;
        _role = null;
        _cards = new Card[2];
        _numCards = 0;

        _isFold = false;
        _isWinner = false;
        _isAllIn = false;
        _isEliminated = false;
    }


    public abstract String actionMakePlay(int sb, int bb, int maxBet);

    public abstract void notifySmallBlindBet(final int amount);
    public abstract void notifyBigBlindBet(final int amount);
    public abstract void notifyTurnWait();
    public abstract void notifyTurnPlay();
    public abstract void notifyRoundEnded();
    public abstract void notifyHandEnded();
    public abstract void notifyGameEnded();
    public abstract void notifyGameKeeps();
    public abstract void notifyHandWinner();
    public abstract void notifyHandLoser();
    public abstract void notifyGameWinner();
    public abstract void notifyGameLoser();
    public abstract void notifyHandEndsByFolds();


    /**
     * Decrements the {@link #_money} variable by a certain amount.
     * This avoids negative values
     * 
     * @param bet quantity to subtract
     */
    protected final void decreaseOffBetMoney(int bet) {
        _offBetMoney = Math.clamp(_offBetMoney - bet, 0, _offBetMoney);
    }

    /**
     * Adds to the {@link #_pocketMoney} variable by a certain amount.
     * 
     * @param bet quantity to add
     */
    protected final void increaseOnBetMoney(int bet) {
        _onBetMoney += bet;
    }

    @Override
    public final void actionSmallBlindBet(final int sb) {   
        decreaseOffBetMoney(sb);
        increaseOnBetMoney(sb);
    }

    @Override
    public final void actionBigBlindBet(final int bb) {
        decreaseOffBetMoney(bb);
        increaseOnBetMoney(bb);
    }
    
    @Override
    public final void receiveRole(PlayerRole r) {
        _role = r;
    }

    @Override
    public final void receiveCard(Card c) {

        if(_numCards == 2)
            return;

        _cards[_numCards++] = c;
    }
    
    @Override
    public final int placeOnBetMoney() {

        int money = _onBetMoney;
        _onBetMoney = 0;
        return money;
    }

    /**
     * Eliminates the hand cards of the player and set the {@link #_numCards} value
     * to zero.
     */
    @Override
    public final void retrieveCards() {
        
        _cards[0] = null;
        _cards[1] = null;
        _numCards = 0;
    }
    
    /**
     * The player receives money
     * 
     * @param money received by the player
     */
    @Override
    public final void receivePriceMoney(int money) {
        _offBetMoney += money;
    }


    @Override
    public final void call(int amount) {
        int resto = amount - _onBetMoney;
        increaseOnBetMoney(resto);
        decreaseOffBetMoney(resto);
    }

    @Override
    public final void check() {
        
    }
    
    @Override
    public final void fold() {
        _isFold = true;
    }
    
    @Override
    public final void raise(int amount) {
        call(amount);
    }
    
    @Override
    public final void allIn() {
        increaseOnBetMoney(_offBetMoney);
        _offBetMoney = 0;
        _isAllIn = true;
    }


    @Override
    public final void unfoldPlayer() {
        _isFold = false;
    }

    @Override
    public final void setAllIn(boolean state) {
        _isAllIn = state;
    }

    @Override
    public final void setIsWinner(boolean state) {
        _isWinner = state;
    }

    @Override
    public final void setIsEliminated(boolean state) {
        _isEliminated = state;
    }


    /**
     * String representation of the player. This contains the name and cards
     * 
     * @see {@link Card} to know more about the Card's toString() method
     *      implementation
     * @return {@link String} representation of the player
     */
    public String toString() {

        String carta1 = (_cards[0] != null) ? _cards[0].toString() : Card.MissingCardToString();
        String carta2 = (_cards[1] != null) ? _cards[1].toString() : Card.MissingCardToString();

        return String.format("Player: %s - %s%s", _name, carta1, carta2);
    }

    @Override
    public final int getPlayerId() { return _id; }

    @Override
    public final String getPlayerName() { return _name; }
    
    @Override
    public final Card[] getPlayerCards() { return _cards; }
    
    @Override
    public final int getCardsCounter() { return _numCards; }

    @Override
    public final int getMoneyOnBet() { return _onBetMoney; }
    
    @Override
    public final int getMoneyOffBet() { return _offBetMoney; }

    @Override
    public final PlayerRole getRole() { return _role; }

    @Override
    public final boolean isFolded() { return _isFold; }

    @Override
    public final boolean isWinner() { return _isWinner; }
    
    @Override
    public final boolean isAllIn() { return _isAllIn; }
    
    @Override
    public final boolean isEliminated() { return _isEliminated; }
    
}