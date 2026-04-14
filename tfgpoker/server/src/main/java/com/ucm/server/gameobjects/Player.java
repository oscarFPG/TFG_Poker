package com.ucm.server.gameobjects;


import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerPlayer;

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

    /**
     * Notifies the player about their current equity.
     * 
     * @param equity probability of winning the hand
     */
    public abstract void notifyEquity(double equity);

    /**
     * Decrements the {@link #_offBetMoney} variable by a certain amount.
     * This method prevents negative values.
     * 
     * @param bet quantity to subtract
     */
    protected final void decreaseOffBetMoney(int bet) {
        _offBetMoney = Math.clamp(_offBetMoney - bet, 0, _offBetMoney);
    }

    /**
     * Adds to the {@link #_onBetMoney} variable by a certain amount.
     * 
     * @param bet quantity to add
     */
    protected final void increaseOnBetMoney(int bet) {
        _onBetMoney += bet;
    }

    /**
     * Places the small blind bet.
     * 
     * @param sb small blind amount
     */
    @Override
    public final void actionSmallBlindBet(final int sb) {   
        decreaseOffBetMoney(sb);
        increaseOnBetMoney(sb);
    }

    /**
     * Places the big blind bet.
     * 
     * @param bb big blind amount
     */
    @Override
    public final void actionBigBlindBet(final int bb) {
        decreaseOffBetMoney(bb);
        increaseOnBetMoney(bb);
    }
    
    /**
     * Assigns a role to the player.
     * 
     * @param r {@link PlayerRole} assigned to the player
     */
    @Override
    public final void receiveRole(PlayerRole r) {
        _role = r;
    }

    /**
     * Adds a card to the player's hand.
     * If the player already has two cards, the card is ignored.
     * 
     * @param c {@link Card} received by the player
     */
    @Override
    public final void receiveCard(Card c) {

        if(_numCards == 2)
            return;

        _cards[_numCards++] = c;
    }
    
    /**
     * Moves all current bet money to the pot and resets it.
     * 
     * @return amount of money placed in the pot
     */
    @Override
    public final int placeOnBetMoney() {

        int money = _onBetMoney;
        _onBetMoney = 0;
        return money;
    }

    /**
     * Eliminates the player's hand cards and resets the card counter.
     */
    @Override
    public final void retrieveCards() {
        
        _cards[0] = null;
        _cards[1] = null;
        _numCards = 0;
    }
    
    /**
     * The player receives money.
     * 
     * @param money received by the player
     */
    @Override
    public final void receivePriceMoney(int money) {
        _offBetMoney += money;
    }

    /**
     * Matches the current bet.
     * 
     * @param amount total amount to match
     */
    @Override
    public final void call(int amount) {
        int resto = amount - _onBetMoney;
        increaseOnBetMoney(resto);
        decreaseOffBetMoney(resto);
    }

    /**
     * Checks (does nothing if no bet is required).
     */
    @Override
    public final void check() {
        
    }
    
    /**
     * Folds the hand.
     */
    @Override
    public final void fold() {
        _isFold = true;
    }
    
    /**
     * Raises the bet.
     * Internally behaves like a call to the specified amount.
     * 
     * @param amount amount to raise to
     */
    @Override
    public final void raise(int amount) {
        call(amount);
    }
    
    /**
     * Performs an all-in action.
     * The player bets all remaining money.
     */
    @Override
    public final void allIn() {
        increaseOnBetMoney(_offBetMoney);
        _offBetMoney = 0;
        _isAllIn = true;
    }

    /**
     * Resets the fold state of the player.
     */
    @Override
    public final void unfoldPlayer() {
        _isFold = false;
    }

    /**
     * Sets the all-in state of the player.
     * 
     * @param state new all-in state
     */
    @Override
    public final void setAllIn(boolean state) {
        _isAllIn = state;
    }

    /**
     * Sets whether the player is a winner.
     * 
     * @param state winner state
     */
    @Override
    public final void setIsWinner(boolean state) {
        _isWinner = state;
    }

    /**
     * Sets whether the player is eliminated.
     * 
     * @param state elimination state
     */
    @Override
    public final void setIsEliminated(boolean state) {
        _isEliminated = state;
    }

    /**
     * String representation of the player.
     * Includes the name and current cards.
     * 
     * @see {@link Card}
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