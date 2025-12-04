package com.ucm.gameobjects;

import java.io.IOException;
import java.net.Socket;
import com.ucm.SocketUtils;
import com.ucm.commands.AllInCommand;
import com.ucm.commands.CallCommand;
import com.ucm.commands.CheckCommand;
import com.ucm.commands.Command;
import com.ucm.commands.FoldCommand;
import com.ucm.commands.RaiseCommand;
import java.util.Scanner;


public class Player {

    /**
     * 
     */
    private int _id;

    /**
     * 
     */
    private String _name;

    /**
     * Player's total money that is not on bet
     */
    private int _money;

    /** 
     * Money the play has bet.
     * It is not lost unless the player folds or loses and it is a portion of all player money
     */
    private int _pocketMoney;

    private PlayerRole _role;
    private Card[] _cards;
    private int _numCards;
    private boolean _fold;
    private boolean _hasLost;

    private Socket _socket;


    public Player(int id, String name, int money, Socket socket) {
        _id = id;
        _name = name;
        _money = money;
        _pocketMoney = 0;

        _role = PlayerRole.NO_ROLE;
        _cards = new Card[2];
        _numCards = 0;
        _fold = false;
        _hasLost = false;

        _socket = socket;
    }

    public int makePlay() throws IOException{

        menuMakePlay();
        return SocketUtils.receiveInt( _socket.getInputStream() );
    }

    private void menuMakePlay() {
        System.out.printf("Haz una jugada, %s!\n", getName());
        System.out.printf("Opciones de jugada: \n");
        System.out.printf("0: fold \n");
        System.out.printf("1: check \n");
        System.out.printf("2: call \n");
        System.out.printf("3: raise \n");
        System.out.printf("4: allIn \n");
        System.out.printf("Introduce tu jugada: ");
    }

    public void makeForcedBet(int sb, int bb) {

        int bet = 0;

        // Forced small-blind and big-blind
        if (_role == PlayerRole.SMALL_BLIND)
            bet = sb;
        else if (_role == PlayerRole.BIG_BLIND)
            bet = bb;

        increasePocketMoney(bet);
        decreaseMoney(bet);
    }

    public boolean receiveCard(Card c) {

        if (_numCards == 2)
            return false;

        _cards[_numCards++] = c;
        return true;
    }

    public Card retrieveCard() {

        if (_numCards <= 0)
            return null;

        Card c = _cards[--_numCards];
        _cards[_numCards] = null;
        return c;
    }

    public int placeBet() {

        int money = _pocketMoney;
        _pocketMoney = 0;
        return money;
    }

    public void fold() {

        // Devuelvo las cartas
        Card c1 = retrieveCard();
        if (c1 != null)
            c1.setAvailable(true);

        Card c2 = retrieveCard();
        if (c2 != null)
            c2.setAvailable(true);

        // El jugador pierde su apuesta
        // Ya no tiene derecho a seguir jugando
        _fold = true;
    }

    public void call(int maxBet) {

        int resto = maxBet - this._pocketMoney; // dinero que necesita para igualar la apuesta en juego
        // Aumento la apuesta de mi ronda
        increasePocketMoney(resto);

        // Quito de mi cartera la diferencia
        decreaseMoney(resto);
    }

    public void allIn() {
        increasePocketMoney(this._money);
        this._money = 0;
    }

    public void raise(int maxBet) {

        // Si tengo menos dinero de lo que está apostado y quiero subir
        // entonces primero igualo y luego subo lo que sea(max All-in)
        if (this._pocketMoney < maxBet) {
            call(maxBet);
            // ejemplo: apuesto 10
            // this._pocketMoney += 10;
            return;
        }

        // En cualquier otro caso subo lo que eliga el player(max All-in)
        this._pocketMoney += 10;
    }

    public void receivePriceMoney(int money) {
        _money += money;
    }

    private void decreaseMoney(int bet) {
        this._money -= bet;
    }

    private void increasePocketMoney(int bet) {
        this._pocketMoney += bet;
    }

    private boolean isEnoughMoney(int bet) {

        if (bet > this._money)
            return false;

        return true;
    }

    public String toString() {

        String carta1 = (this._cards[0] != null) ? _cards[0].toString() : Card.MissingCardToString();
        String carta2 = (this._cards[1] != null) ? _cards[1].toString() : Card.MissingCardToString();

        return String.format("Player[%d]: %s - %s%s", _id, _name, carta1, carta2);
    }

    public void setRole(PlayerRole pr) {
        _role = pr;
    }

    public void resetCards() {

        if (_cards[0] != null)
            _cards[0] = null;

        if (_cards[1] != null)
            _cards[1] = null;

        _numCards = 0;
    }

    public void setFold(boolean fold) {
        _fold = fold;
    }

    public void setHasLost(boolean lost){
        _hasLost = lost;
    }

    public Card[] getCards(){ return _cards; }
    public int getID(){ return _id; }
    public String getName(){ return _name; }
    public int getMoney(){ return _money; }
    public int getPocketMoney(){ return _pocketMoney; }
    public PlayerRole getPlayerRole(){ return _role; }
    public int getNumCards(){ return _numCards; }
    public boolean hasFolded(){ return _fold; }
    public boolean hasLost(){ return _hasLost; }

}