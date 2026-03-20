package com.ucm.server.gameobjects;

import java.io.IOException;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.SocketUtils;
import com.ucm.server.control.GameAdapter;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.logic.Game;

/**
 * This class represents any king of player in the game, whether its a human
 * player or and AI player.
 * It works as a representation of the player entity in the server side.
 */
public class Player implements IPokerPlayer {

    private static final Logger log = LogManager.getLogger(Player.class);

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
     * Indicates if the player is the winner of a hand or the game.
     * This flag should only be read at the end of a hand o the game.
     */
    private boolean _isWinner;

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
    public Player(String name, Socket socket, int money) {
        _name = name;
        _socket = socket;

        _money = money;
        _pocketMoney = 0;
        _role = PlayerRole.NO_ROLE;
        _cards = new Card[2];
        _numCards = 0;
        _fold = false;
        _isWinner = false;
    }

    /**
     * Decrements the {@link #_money} variable by a certain amount.
     * This avoids negative values
     * 
     * @param bet quantity to subtract
     */
    private void decreaseOffBetMoney(int bet) {
        _money = Math.clamp(_money - bet, 0, _money);
    }

    /**
     * Adds to the {@link #_pocketMoney} variable by a certain amount.
     * 
     * @param bet quantity to add
     */
    private void increaseOnBetMoney(int bet) {
        _pocketMoney += bet;
    }

    /**
     * Eliminates the hand cards of the player and set the {@link #_numCards} value
     * to zero.
     */
    public void resetCards() {

        if (_cards[0] != null)
            _cards[0] = null;

        if (_cards[1] != null)
            _cards[1] = null;

        _numCards = 0;
    }

    /**
     * Gets the player cards.
     * Player could have zero to two cards
     * 
     * @return player cards
     */
    public Card[] getCards() {
        return _cards;
    }

    /**
     * Gets the player role in the current hand.
     * 
     * @return player role
     */
    public PlayerRole getPlayerRole() {
        return _role;
    }

    /**
     * Prints by console the player's status
     * This includes the player id, name and cards
     * 
     * @see {@link Card} to know more about the Card's toString() method
     *      implementation
     * @return {@link String} representation of the player
     */
    public String toString() {

        String carta1 = (_cards[0] != null) ? _cards[0].toString() : Card.MissingCardToString();
        String carta2 = (_cards[1] != null) ? _cards[1].toString() : Card.MissingCardToString();

        return String.format("Player: %s - %s%s", _name, carta1, carta2);
    }

    /// --------------------------------------- IPokerActions ---------------------------------------
    @Override
    public boolean call(final int amount) {

        int resto = amount - _pocketMoney; // dinero que necesita para igualar la apuesta en juego
        log.debug("Player {} wants to call to {}$", _name, amount);
        log.debug("Total money on bet: {}$", amount + _pocketMoney);
        
        // Aumento la apuesta de mi ronda
        increaseOnBetMoney(resto);

        // Quito de mi cartera la diferencia
        decreaseOffBetMoney(resto);

        return true;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public boolean fold() {

        // El jugador pierde su apuesta
        // Ya no tiene derecho a seguir jugando
        _fold = true;

        return true;
    }

    @Override
    public boolean raise(final int amount) {

        // Si tengo menos dinero de lo que está apostado y quiero subir
        // entonces primero igualo y luego subo lo que sea(max All-in)
        if (_pocketMoney <= amount) {
            call(amount);
            return false;
        }

        return true;
    }

    @Override
    public boolean allIn() {

        increaseOnBetMoney(_money);
        _money = 0;
        return true;
    }

    /* --------------------------------------- IPokerPlayer --------------------------------------- */
    @Override
    public String getPlayerName() {
        return _name;
    }

    @Override
    public int getMoneyOnBet() {
        return _pocketMoney;
    }

    @Override
    public int getMoneyOffBet() {
        return _money;
    }

    @Override
    public int getCardsCounter() {
        return _numCards;
    }

    @Override
    public boolean isFolded() {
        return _fold;
    }

    @Override
    public boolean isWinner(){
        return _isWinner;
    }

    @Override
    public Card[] getPlayerCards() {
        return _cards;
    }



    @Override
    public void receiveRole(PlayerRole r) {
        
        if (Game.DEBUG) {
            return;
        }

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerRoleToCode(r));
        }
        catch (IOException e) {
            log.error("Receiving the role for {} player: {}", _name, e.getMessage());
        }
    }

    @Override
    public void receiveCard(Card c) {

        if (_numCards == 2)
            return ;


        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardValueToCode(c));
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardSuitToCode(c));
            _cards[_numCards++] = c;
            log.debug("Player {} receives card {}", _name, c.toString());
        }
        catch (IOException e) {
            log.error("Giving the card {} to player {}: {}", c.toString(), _name, e.getMessage());
        }
    }

    @Override
    public void receiveTableCard(Card c) {
        
        if (Game.DEBUG) {
            return;
        }

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardValueToCode(c));
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardSuitToCode(c));
            log.debug("Table card {}", c.toString());
        }
        catch(IOException e){
            log.error("Trying to send a table card to {}: {}", _name,  e.getMessage());
        }
    }

    @Override
    public void receiveNewMoney(int money) {

       try{
            SocketUtils.sendInteger(_socket.getOutputStream(), money);
            log.debug("Players {} new money is {}", _name, money);
        }
        catch (IOException e) {
            log.error("Trying to force a move on the player {}: {}", _name, e.getMessage());
        }
    }

    /**
     * The player receives money
     * 
     * @param money received by the player
     */
    @Override
    public void receivePriceMoney(int money) {
        _money += money;
    }

    @Override
    public void retrieveCards() {
        
        _cards[0] = null;
        _cards[1] = null;
        _numCards = 0;
    }

    @Override
    public void setIsWinner(boolean state){
        _isWinner = state;
    }

    @Override
    public int placeOnBetMoney() {

        int money = _pocketMoney;
        _pocketMoney = 0;
        return money;
    }

    @Override
    public void foldPlayer(){
        _fold = true;
    }

    @Override
    public void unfoldPlayer() {
        _fold = false;
    }


    @Override
    public void notifyTurnWait() {

        if (Game.DEBUG) {
            return;
        }

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnWaitToCode());
            log.debug("Player {} has to wait his turn!", _name);
        }
        catch (IOException e) {
            log.error("Sending the WAIT order to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyTurnPlay() {

        if (Game.DEBUG) {
            return ;
        }

        try {

            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnPlayToCode());
            log.debug("Player {} has to play his turn!", _name);
        }
        catch (IOException e) {
            log.error("Receiving the command for {} player: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyRoundEnded() {
        
        if (Game.DEBUG) {
            return;
        }

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameRoundEnded());
            log.debug("Player {} notified about the end of the round", _name);
        }
        catch (IOException e) {
            log.error("Notifing ROUND_ENDS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyHandEnded() {
        
        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameHandEnded());
            log.debug("Player {} notified about the end of the hand", _name);
        }
        catch(IOException e){
            log.error("Notifying HAND_ENDS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyGameEnded() {

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameEnded());
            log.debug("Player {} notified about the end of the game", _name);
        }
        catch(IOException e){
            log.error("Notifying GAME_ENDS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyGameKeeps() {
        
        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameKeeps());
            log.debug("Player {} notified about game keeps on", _name);
        }
        catch(IOException e){
            log.error("Notifying GAME_KEEPS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyHandWinner() {

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerWinsHand());
            log.debug("Player {} is then winner of the hand", _name);
        }
        catch(IOException e){
            log.error("Notifying HAND_WINNER to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyHandLoser() {

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerLosesHand());
            log.debug("Player {} is the loser of the hand", _name);
        }
        catch(IOException e){
            log.error("Notifying HAND_LOSER to player {}: {}", _name, e.getMessage());
        }
    }
    
    @Override
    public void notifyGameWinner() {

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerWinsGame());
            log.debug("Player {} is then winner of the game", _name);
        }
        catch(IOException e){
            log.error("Notifying GAME_WINNER to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyGameLoser() {

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerLosesGame());
            log.debug("Player {} is the loser of the game", _name);
        }
        catch(IOException e){
            log.error("Notifying GAME_LOSER to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void actionSmallBlindBet(final int sb) {
        
        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnForcedSBToCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), sb);

            _money -= sb;
            _pocketMoney += sb;
        }
        catch(IOException e){
            log.error("Player {} making the small blind bet: {}", _name, e.getMessage());
        }
    }

    @Override
    public void actionBigBlindBet(final int bb) {
        
        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnForcedSBToCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), bb);

            _money -= bb;
            _pocketMoney += bb;
        }
        catch(IOException e){
            log.error("Player {} making the small blind bet: {}", _name, e.getMessage());
        }
    }

    @Override
    public String actionMakePlay(final int sb, final int bb, final int maxBet) {
        
        String commandInput = null;
        try{

            // Send round info
            SocketUtils.sendInteger(_socket.getOutputStream(), sb);
            SocketUtils.sendInteger(_socket.getOutputStream(), bb);
            SocketUtils.sendInteger(_socket.getOutputStream(), maxBet);
            SocketUtils.sendInteger(_socket.getOutputStream(), _money);
            log.debug("Round info sent to {}", _name);

            commandInput = SocketUtils.receiveString( _socket.getInputStream() );
            log.debug("Command code {} sent by the player {}", commandInput, _name);
        }
        catch (IOException e) {
            log.error("Receiving the command for {} player: {}", _name, e.getMessage());
        }

        return commandInput;
    }

    
}