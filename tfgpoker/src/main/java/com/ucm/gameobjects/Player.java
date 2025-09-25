package com.ucm.gameobjects;


public class Player {
 
    private Card[] _cards;
    private String _id;
    private String _name;
    private boolean _fold = false;
    private int _money;
    private String _role;
    

    public Player(Card[] cards){
        this._cards = cards;
    }
    
    public Player(String id, Card[] cards,  String name, int money){
        this._id = id;     
        this._cards = cards;
        this._name = name;
        this._money = money;
    }
        
    
    public Card[] getCards(){ return _cards;}
    
    public String getID(){  return _id;}
        
    public String getName(){return _name;}

    public boolean getFold() {return _fold; }
    
    public int getMoney(){ return _money;}


    public void setCards(Card[] c) {
         _cards = c;
    }

    public void setName(String n) {
        _name = n;
    }

    public void setMoney(int m) {
        _money = m;
    }
    public void setFold(boolean f) {
        _fold = f;
    }
    
    
}
