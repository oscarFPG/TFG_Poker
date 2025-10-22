package com.ucm.gameobjects;

import com.ucm.commands.AllInCommand;
import com.ucm.commands.CallCommand;
import com.ucm.commands.CheckCommand;
import com.ucm.commands.Command;
import com.ucm.commands.FoldCommand;
import com.ucm.commands.RaiseCommand;
import java.util.Scanner;

public class Player {

    private int _id;
    private String _name;
    private int _money; // Player's total money
    private int _pocketMoney; // Money the play has bet. It is not lost unless the player folds or loses and
                              // it is a portion of the remaining of the total
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

    public Command makePlay(int maxBet) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Instrucciones jugada: ");
        System.out.println("0: fold ");
        System.out.println("1: check ");
        System.out.println("2: call ");
        System.out.println("3: raise ");
        System.out.println("4: allIn ");
        System.out.println("Introduce tu jugada: ");
        int jugada = sc.nextInt();
        sc.close();
        switch (jugada) {
            case 0:
                return new FoldCommand(this, _pocketMoney);
            case 1:
                return new CheckCommand(this, _pocketMoney);
            case 2:
                if (!isEnoughMoney(maxBet)) { // No puedo igualar porque no tengo suficiente dinero
                    System.out.println("Not enough money to make a call");
                    // Habrá que preguntarle al jugador si quiere hacer un allIn retirarse
                }
                return new CallCommand(this, _pocketMoney);
            case 3:
                if (!isEnoughMoney(maxBet)) { // No puedo igualar porque no tengo suficiente dinero
                    System.out.println("Not enough money to make a raise");
                }
                // Seguir haciendo...
                return new RaiseCommand(this, _pocketMoney);
            case 4:
                return new AllInCommand(this, _pocketMoney);
            default:
                System.out.println("Opción no válida.");
                return null;
        }

        // No comprobar AQUI si el valor para RaiseCommand, CallCommand o AllInCommand
        // es correcto
        // Comprobar en el comando concreto
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

        // Cogemos la apuesta que está en juego ahora y calculamos la diferencia entre
        // la apuesta en juego
        // y lo que tengo apostado de momento

        // Si quiero igualar pero no tengo dinero puedo hacer un allin
        if (!isEnoughMoney(maxBet)) {
            allIn();
            return;
        }

        // Aumento la apuesta de mi ronda
        increasePocketMoney(maxBet);

        // Quito de mi cartera la diferencia
        decreaseMoney(maxBet);
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

    public void setRole(PlayerRole pr) {
        _role = pr;
    }

    private void decreaseMoney(int maxBet) {
        int resto = maxBet - this._pocketMoney;
        this._money -= resto;
    }

    private void increasePocketMoney(int maxBet) {
        int resto = maxBet - this._pocketMoney;
        this._pocketMoney += resto;
    }

    private boolean isEnoughMoney(int maxBet) {
        if ((maxBet - _pocketMoney) > this._money)
            return false;

        return true;
    }

    public int getID() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getMoney() {
        return _money;
    }

    public int getPocketMoney() {
        return _pocketMoney;
    }

    public PlayerRole getPlayerRole() {
        return _role;
    }

    public int getNumCards() {
        return _numCards;
    }

    public boolean hasFolded() {
        return _fold;
    }

}
