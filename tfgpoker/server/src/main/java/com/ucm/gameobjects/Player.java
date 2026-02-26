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

/**
 * This class represents any king of player in the game, whether its a human
 * player or and AI player.
 * It works as a representation of the player entity in the server side.
 */
public class Player implements IPlayer {

    /**
     * Player's unique identifier
     */
    private int _id;

    /**
     * Player's name
     */
    private String _name;

    /**
     * Player's total money.
     * This is money that the player has not bet yet.
     * The bet money is transfer to the {@link #_pocketMoney} when the player places
     * a bet.
     */
    private int _money;

    /**
     * Money the player has bet.
     * It is only truly lost only when a round ends and the player has lost,
     * otherwise it is returned to the player.
     */
    private int _pocketMoney;

    /**
     * Player's role during the current hand.
     * This role is assigned at the beginning of the game and it is reassigned at
     * the end of each hand.
     * Check {@link PlayerRole} for more information about the possible roles.
     */
    private PlayerRole _role;

    /**
     * Represents the cards that the player has in his hand.it can only hold zero or
     * two cards.
     */
    private Card[] _cards;

    /**
     * Number of cards the player currently has in hand.
     * It can only be 0, 1 or 2, but it should never be 1 since the player should
     * always have two cards or none.
     */
    private int _numCards;

    /**
     * Indicates whether the player has folded in the current hand.
     */
    private boolean _fold;

    /**
     * Indicates whether the player has lost the game.
     */
    private boolean _hasLost;

    /**
     * Socket used for communication with the player.
     * Check {@link Socket} and {@link SocketUtils}.
     */
    private Socket _socket;

    /**
     * Constructor for the Player class.
     * 
     * @param id     assignated to the player, it must be unique
     * @param name   of the player, it does not have to be unique
     * @param socket used for communicating with the real player
     * @param money  received at the beginning of the game
     */
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

    /**
     * Assigns a role to the player.
     * 
     * @param r
     */
    public void assignRole(PlayerRole r) {
        _role = r;
        onReceiveRole(r);
    }

    /**
     * Makes a play based on the player's input by console
     * 
     * @return {@link Command} representing the play the player has made
     */
    public Command makePlay() {

        Scanner sc = new Scanner(System.in);
        int jugada = -1;

        do {

            menuMakePlay();
            jugada = sc.nextInt(); // Suponemos que la entrada siempre es un numero
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
                    return new AllInCommand(this, _money, _pocketMoney);

                default:
                    return null;
            }

        } while (jugada == -1);

    }

    /**
     * Prints the menu for the player to make a play by console.
     */
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

    /**
     * Forces the player to make the small-blind or big-blind bet if its role is the
     * corresponding one.
     * Check {@link PlayerRole}
     * 
     * @param sb
     * @param bb
     */
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

    /**
     * Adds a card to the player's hand if he has less than two cards.
     * 
     * @param c card to be added
     * @return true if the card was added successfully, false in any other case
     */
    public boolean receiveCard(Card c) {

        if (_numCards == 2)
            return false;

        _cards[_numCards++] = c;
        return true;
    }

    /**
     * Retrieves a card from the player's hand.
     * If the player has two card it shoul be called twice to retrieve both cards.
     * 
     * @return the card retrieved or null if the player has no cards
     */
    public Card retrieveCard() {

        if (_numCards <= 0)
            return null;

        Card c = _cards[--_numCards];
        _cards[_numCards] = null;
        return c;
    }

    /**
     * Retrieve the money that it is contained in the {@link #_pocketMoney} and set
     * it to zero.
     * 
     * @return the money retrieved
     */
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

    public void setHasLost(boolean lost) {
        _hasLost = lost;
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

    public boolean hasLost() {
        return _hasLost;
    }

    @Override
    public void onReceiveRole(PlayerRole r) {

        try {

            int role = GameAdapter.playerRoleToCode(r);
            SocketUtils.sendInteger(_socket.getOutputStream(), role);
            _role = r;

            System.out.printf("Player %s receives rol %s\n", _name, _role.name());
        } catch (IOException e) {
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