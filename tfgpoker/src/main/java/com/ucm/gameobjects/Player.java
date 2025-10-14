package com.ucm.gameobjects;


public class Player {

    private int _id;
    private String _name;
    private int _money;         // Player's total money
    private int _pocketMoney;   // Money the play has bet. It is not lost unless the player folds or loses and it is a portion of the remaining of the total    
    private PlayerRole _role;
    private Card[] _cards;
    private int _numCards;
    private boolean _fold;


    public Player(int id, String name, int money) {
        _id = id;
        _name = name;
        _money = money;
        _pocketMoney = 0;
        _role = PlayerRole.NO_ROLE;
        _cards = new Card[2];
        _numCards = 0;
        _fold = false;
    }


    public PlayerAction makePlay(final int sb, final int bb, final int maxBet) {

        PlayerAction action;
        if(_role == PlayerRole.SMALL_BLIND){
            action = PlayerAction.FORCED_PLAY;
            action.setPot( makeForcedPlay(sb) );
        }
        else if(_role == PlayerRole.BIG_BLIND){
            action = PlayerAction.FORCED_PLAY;
            action.setPot( makeForcedPlay(bb) );
        }
        else{
            action = PlayerAction.CHECK;
        }

        return action;
    }

    public boolean receiveCard(Card c) {

        if(_numCards == 2)
            return false;

        
        _cards[_numCards++] = c;
        return true;
    }

    public Card retrieveCard(){
        
        Card c = _cards[0];
        _cards[0] = null;
        --_numCards;


        return c;
    }

    public int makeBet(){
        _money -= 10;
        _pocketMoney += 10;
        return _pocketMoney;
    }

    private int makeForcedPlay(int q){
        _money -= q;
        _pocketMoney += q;
        return q;
    }

    public int fold() {
        return 0;   // returns the amount of money the player put on bet
    }



    public void setRole(PlayerRole pr) {
        _role = pr;
    }

    public int getID() { return _id; }
    public String getName() { return _name; }
    public int getMoney() { return _money; }
    public int getPocketMoney() { return _pocketMoney; }
    public PlayerRole getPlayerRole() { return _role; }
    public int getNumCards(){ return _numCards; }
    public boolean hasFolded() { return _fold; }

}
