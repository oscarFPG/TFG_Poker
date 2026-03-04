package com.ucm.server.gameobjects;

import com.ucm.common.SocketUtils;
import com.ucm.server.commands.AllInCommand;
import com.ucm.server.commands.CallCommand;
import com.ucm.server.commands.CheckCommand;
import com.ucm.server.commands.Command;
import com.ucm.server.commands.FoldCommand;
import com.ucm.server.commands.RaiseCommand;
import com.ucm.server.control.GameAdapter;
import com.ucm.server.interfaces.IPlayer;
import com.ucm.server.logic.Game;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents any king of player in the game, whether its a human
 * player or and AI player.
 * It works as a representation of the player entity in the server side.
 */
public class Player implements IPlayer {

    private static final Logger log = LogManager.getLogger(Player.class);
    private static final Scanner sc = new Scanner(System.in);
    
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
        log.debug("Player {} received role {}", _name, r.toString());
    }

    /**
     * Makes a play based on the player's input by console
     * 
     * @return {@link Command} representing the play the player has made
     */
    public Command makePlay() {

        Command command = null;
        String[] userInput = null;

        do {

            menuMakePlay();
            userInput = sc.nextLine().trim().split(" ");
            command = Command.parseCommand(userInput, this);
        }
        while (command == null);

        return command;
    }

    /**
     * Prints the menu for the player to make a play by console.
     */
    private void menuMakePlay() {
        System.out.printf("\tHaz una jugada, %s!\n", getName());
        System.out.println("\tOpciones de jugada: ");
        System.out.println( Command.showAvailableCommands() );
        System.out.print("\tIntroduce tu jugada: ");
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
        if (_role == PlayerRole.SMALL_BLIND){
            bet = sb;
            log.debug("Player {} forced to bet {} as a small blind", _name, bet);
        }
        else if (_role == PlayerRole.BIG_BLIND){
            bet = bb;
            log.debug("Player {} forced to bet {} as a big blind", _name, bet);
        }

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

    /**
     * Makes the player fold by setting the {@link #_fold} value to true.
     * Retrieves the cards in his hands and marks him as 'folded'.
     * The {@link #_pocketMoney} variable keeps its value
     */
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

    /**
     * The player 'calls' in this round, which means that equals the maximum bet made by other players.
     * @param maxBet
     */
    public void call(int maxBet) {

        int resto = maxBet - _pocketMoney; // dinero que necesita para igualar la apuesta en juego
        // Aumento la apuesta de mi ronda
        increasePocketMoney(resto);

        // Quito de mi cartera la diferencia
        decreaseMoney(resto);
    }

    /**
     * The player bets all his money in the current hand
     */
    public void allIn() {
        increasePocketMoney(_money);
        _money = 0;
    }

    /**
     * TODO: HAY QUE HACERLO BIEN !!
     * @param maxBet
     */
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

    /**
     * The player receives money
     * @param money received by the player
     */
    public void receivePriceMoney(int money) {
        _money += money;
    }

    /**
     * Decrements the {@link #_money} variable by a certain amount.
     * This avoids negative values
     * 
     * @param bet quantity to subtract
     */
    private void decreaseMoney(int bet) {
        _money = Math.clamp(_money - bet, 0, _money);
    }

    /**
     * Adds to the {@link #_pocketMoney} variable by a certain amount.
     * @param bet quantity to add
     */
    private void increasePocketMoney(int bet) {
        _pocketMoney += bet;
    }

    /**
     * Prints by console the player's status
     * This includes the player id, name and cards
     * @see {@link Card} to know more about the Card's toString() method implementation
     * @return {@link String} representation of the player
     */
    public String toString() {

        String carta1 = (_cards[0] != null) ? _cards[0].toString() : Card.MissingCardToString();
        String carta2 = (_cards[1] != null) ? _cards[1].toString() : Card.MissingCardToString();

        return String.format("Player[%d]: %s - %s%s", _id, _name, carta1, carta2);
    }

    /**
     * Eliminates the hand cards of the player and set the {@link #_numCards} value to zero.
     */
    public void resetCards() {

        if (_cards[0] != null)
            _cards[0] = null;

        if (_cards[1] != null)
            _cards[1] = null;

        _numCards = 0;
    }

    /**
     * Sets a value to the {@link #_fold} member variable
     * @param fold new value
     */
    public void setFold(boolean fold) {
        _fold = fold;
    }

    /**
     * Sets a value to the {@link #_hasLost} member variable
     * @param lost new value
     */
    public void setHasLost(boolean lost) {
        _hasLost = lost;
    }

    /**
     * Gets the player cards.
     * Player could have zero to two cards
     * @return player cards
     */
    public Card[] getCards() {
        return _cards;
    }

    /**
     * Gets the player ID
     * @return player ID
     */
    public int getID() {
        return _id;
    }

    /**
     * Gets the player name
     * @return player name
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the player money.
     * This money could be the total money or a part of it, considering the {@link #_pocketMoney} variable.
     * @return player money
     */
    public int getMoney() {
        return _money;
    }

    /**
     * Gets the money the player has bet in the current hand.
     * @return money the player has bet in the current hand
     */
    public int getPocketMoney() {
        return _pocketMoney;
    }

    /**
     * Gets the player role in the current hand.
     * @return player role
     */
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

        if(Game.DEBUG){
            return;
        }

        try {

            int role = GameAdapter.playerRoleToCode(r);
            SocketUtils.sendInteger(_socket.getOutputStream(), role);
            _role = r;

            log.debug("Player {} receives role {}", _name, _role.name());
        } catch (IOException e) {
            log.error("Error receiving the role for {} player: {}", _name, e.getMessage());
        }
    }

    @Override
    public void onReceiveCard(Card c) {

        if(Game.DEBUG){
            return;
        }

        throw new UnsupportedOperationException("Unimplemented method 'onReceiveCard'");
    }

    @Override
    public void onReceiveTableCard(Card c) {
        
        if(Game.DEBUG){
            return;
        }

        throw new UnsupportedOperationException("Unimplemented method 'onReceiveTableCard'");
    }

    @Override
    public void onReceiveTurn() {
        
        if(Game.DEBUG){
            return;
        }

        throw new UnsupportedOperationException("Unimplemented method 'onReceiveTurn'");
    }

}