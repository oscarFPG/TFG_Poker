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
    private int _money;         // Player's total money
    private int _pocketMoney;   // Money the play has bet. It is not lost unless the player folds or loses and
                                // it is a portion of the remaining of the total
    private PlayerRole _role;
    private Card[] _cards;
    private int _numCards;
    private boolean _fold;
    private boolean _hasLost;


    public Player(int id, String name, int money) {
        _id = id;
        _name = name;
        _money = money;
        _pocketMoney = 0;

        _role = PlayerRole.NO_ROLE;
        _cards = new Card[2];
        _numCards = 0;
        _fold = false;
        _hasLost = false;
    }

    public Command makePlay() {

        Scanner sc = new Scanner(System.in);
        int jugada = -1;

        do {

            menuMakePlay();
            jugada = sc.nextInt();  // Suponemos que la entrada siempre es un numero
            sc.close();

            switch (jugada) {
            case 0:
                return new FoldCommand(this);
            
            case 1:
                return new CheckCommand(this);
            
            case 2:
                return new CallCommand(this, _money, _pocketMoney);

            case 3:
                Scanner scanner = new Scanner(System.in);
                int nuevaApuesta = 0;
                System.out.printf("Introduzca la cantidad a apostar: ");
                nuevaApuesta = scanner.nextInt();
                System.out.printf("\n");

                return new RaiseCommand(this, nuevaApuesta, _money, _pocketMoney);

            case 4:
                return new AllInCommand(this, _money + _pocketMoney);

            default:
                return null;
            }

            /*
            switch (jugada) {
                case 0:
                    return new FoldCommand(this, _pocketMoney);

                case 1:
                    if (maxBet != 0) { // No puedo hacer check si hay alguna apuesta en juego
                        System.out.println("You can't do check"); 
                    }
                    return new CheckCommand(this, _pocketMoney);

                case 2:
                    if (!isEnoughMoney(maxBet - _pocketMoney)) { // No puedo igualar porque no tengo suficiente dinero
                        System.out.println("Not enough money to make a call");
                        return new FoldCommand(this, _pocketMoney);
                        // TODO Habrá que preguntarle al jugador si quiere hacer un allIn retirarse.
                        // Se entiende que no quiere hacer fold
                    }
                    return new CallCommand(this, _pocketMoney);

                case 3:
                    //
                    // TODO Si no tengo suficiente dinero para subir, ver si tengo suficiente dinero
                    // para igualar, y si tengo, hago call
                    // (decidir si hacer call obligatoriamente o preguntar si el jugador quiere
                    // hacer call u allin), si no,
                    // tendría que hacer fold u allin
                    //
                    if (!isEnoughMoney(10)) {
                        System.out.println("Not enough money to make a raise");
                        return new CallCommand(this, _pocketMoney);
                    }
                    return new RaiseCommand(this, _pocketMoney);

                case 4:
                    return new AllInCommand(this, _pocketMoney);

                default:
                    System.out.println("Opción no válida. Vuelva a intentarlo...");
                    jugada = -1;
                    break;
            }
            */

        } while (jugada == -1);

    }

    private void menuMakePlay() {
        System.out.printf("Haz una jugada, %s!\n", getName());
        System.out.println("Opciones de jugada: ");
        System.out.println("0: fold ");
        System.out.println("1: check ");
        System.out.println("2: call ");
        System.out.println("3: raise ");
        System.out.println("4: allIn ");
        System.out.println("Introduce tu jugada: ");
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

        int resto = maxBet - _pocketMoney; // dinero que necesita para igualar la apuesta en juego
        // Aumento la apuesta de mi ronda
        increasePocketMoney(resto);

        // Quito de mi cartera la diferencia
        decreaseMoney(resto);
    }

    public void allIn() {
        increasePocketMoney(_money);
        _money = 0;
    }

    public void raise(int maxBet) {

        // Si tengo menos dinero de lo que está apostado y quiero subir
        // entonces primero igualo y luego subo lo que sea(max All-in)
        if (_pocketMoney <= maxBet) {
            call(maxBet);
            return;
        }

        // En cualquier otro caso subo lo que eliga el player(max All-in)
        _pocketMoney += 10;
    }

    public void receivePriceMoney(int money) {
        _money += money;
    }

    private void decreaseMoney(int bet) {
        _money -= bet;
    }

    private void increasePocketMoney(int bet) {
        _pocketMoney += bet;
    }

    private boolean isEnoughMoney(int bet) {

        if (bet > _money)
            return false;

        return true;
    }

    public String toString() {

        String carta1 = (_cards[0] != null) ? _cards[0].toString() : Card.MissingCardToString();
        String carta2 = (_cards[1] != null) ? _cards[1].toString() : Card.MissingCardToString();

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