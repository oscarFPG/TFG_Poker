package com.ucm.gameobjects;

public class Player {

    private Card[] _cards;
    private int _id;
    private String _name;
    private boolean _fold = false;
    private int _money;
    private String _role;

    public Player(String name) {
        this._name = name;
    }

    public Player(int id, Card[] cards, String name, int money) {
        this._id = id;
        this._cards = cards;
        this._name = name;
        this._money = money;
    }

    public Card[] getCards() {
        return _cards;
    }

    public int getID() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public boolean getFold() {
        return _fold;
    }

    public int getMoney() {
        return _money;
    }

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

    public void addCard(Card c) {
        _cards[_cards.length - 1] = c;
    }

}
