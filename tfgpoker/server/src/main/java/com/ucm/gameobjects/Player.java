package com.ucm.gameobjects;

import com.ucm.SocketUtils;
import com.ucm.commands.AllInCommand;
import com.ucm.commands.CallCommand;
import com.ucm.commands.CheckCommand;
import com.ucm.commands.Command;
import com.ucm.commands.FoldCommand;
import com.ucm.commands.RaiseCommand;
import com.ucm.control.GameAdapter;
import com.ucm.interfaces.IPlayer;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Player implements IPlayer {

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
    private Socket _socket;


    public Player(int id, String name, Socket socket, int money) {
        _id = id;
        _name = name;
        _socket = socket;

        _money = money;
        _pocketMoney = 0;
        _role = PlayerRole.NO_ROLE;
        _cards = new Card[2];
        _numCards = 0;
        _fold = false;
        _hasLost = false;
    }


    public void assignRole(PlayerRole r) {
        _role = r;
        onReceiveRole(r);
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

    public String toString() {

        String carta1 = (_cards[0] != null) ? _cards[0].toString() : Card.MissingCardToString();
        String carta2 = (_cards[1] != null) ? _cards[1].toString() : Card.MissingCardToString();

        return String.format("Player[%d]: %s - %s%s", _id, _name, carta1, carta2);
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


    @Override
    public void onReceiveRole(PlayerRole r) {
        
        try{

            int role = GameAdapter.playerRoleToCode(r);
            SocketUtils.sendInteger(_socket.getOutputStream(), role);
            _role = r;

            System.out.printf("Player %s receives rol %s\n", _name, _role.name());
        }
        catch(IOException e){
            System.out.printf("Error receiving the role for %s player: %s\n", _name, e.getMessage());
        }
    }

    @Override
    public void onReceiveCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onReceiveCard'");
    }

    @Override
    public void onReceiveTableCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onReceiveTableCard'");
    }

    @Override
    public void onReceiveTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onReceiveTurn'");
    }

}