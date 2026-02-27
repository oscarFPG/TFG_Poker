package com.ucm.server.logic;


import com.ucm.server.commands.Command;
import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;

/**
 * This class represents the list of players in the game in a Circular Double-linked list structure.
 * This implementation holds the circular form in any game state.
 * This means that in every moment, the first, last and middle nodes are connected to each other in a circular way.
 */
public class PlayerList {

    /**
     * Inner class to represent each node of the list
     */
    public class Node {

        /**
         * The previous node to the current one
         */
        Node _prev;

        /**
         * The player that the current node holds
         */
        Player _player;

        /**
         * The next node to the current one
         */
        Node _next;

        /**
         * Constructor of the Node class
         * @param prev The previous node to the current one
         * @param p The player that the current node holds
         * @param next The next node to the current one
         */
        public Node(Node prev, Player p, Node next) {
            _prev = prev;
            _player = p;
            _next = next;
        }
    }

    /**
     * The first node of the list
     */
    private Node _first;

    /**
     * The last node of the list
     */
    private Node _last;

    /**
     * The number of players currently in the list
     */
    private int _playerCounter;

    /**
     * The maximum number of players allowed in the list
     */
    private int _maxNumberOfPlayers;


    /**
     * Constructor of a empty circular double-linked list of players
     * @param n The maximum number of players allowed in the list
     */
    public PlayerList(int n) {
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = n;
    }


    /**
     * Adds a player to the list.
     * If the list is full, the player will not be added and this method will not have any effect.
     * @param p The player to be added
     */
    public void addPlayer(Player p) {

        if(isFull())
            return;

        Node newNode = new Node(_last, p, _first);
        if (isEmpty()) {
            _first = newNode;
            _last = newNode;
            _first._next = newNode;
            _first._prev = newNode;
            _last._next = newNode;
            _last._prev = newNode;
        } 
        else {
            _first._prev = newNode;
            _last._next = newNode;
            _last = newNode;
        }

        ++_playerCounter;
    }

    /**
     * Assigns the corresponding role to each player in the list according to the rules of the game.
     * The first player list will be assign the <i>DEALER</i> role.
     * The {@link #_first} member variable allways has to point to the dealer because it is the reference point to assign the rest of the roles. 
     */
    public void assignRolesToAllPlayers() {

        int n = activePlayersCounter();
        Node _current = _first;

        // NO HAY DEALER --> SOLO SB Y BB
        if ( n == 0 || n == 1 ) return;

        if (n == 2) {
            _current = getNextPlayerActive(_first);
            _current._player.assignRole(PlayerRole.SMALL_BLIND);

            _current = getNextPlayerActive(_current._next);
            _current._player.assignRole(PlayerRole.BIG_BLIND);
        }
        else {
            _current = getNextPlayerActive(_first);
            _current._player.assignRole(PlayerRole.DEALER);

            _current = getNextPlayerActive(_current._next);
            _current._player.assignRole(PlayerRole.SMALL_BLIND);

            _current = getNextPlayerActive(_current._next);
            _current._player.assignRole(PlayerRole.BIG_BLIND);
        
            _current = getNextPlayerActive(_current._next);
            while (_current != _first){
                _current._player.assignRole(PlayerRole.NO_ROLE);
                _current = getNextPlayerActive(_current._next);
            }
        }
    }

    /**
     * Removes a player from the list.
     * If the list if empty or the player is not in the list, this method will not have any effect.
     * @param p The player to be removed
     */
    public void removePlayer(Player p) {

        if (isEmpty())
            return;

        if (_first._player == p)
            delete(_first);

        Node iNode = _first._next;
        while (iNode._player != p && iNode != _first && iNode != null)
            iNode = iNode._next;

        delete(iNode);
    }

    /**
     * Removes a node from the list.
     * If the node is null because the player is not found, this method will not have any effect.
     * @param p The node to be removed
     */
    private void delete(Node p) {

        if (p == null)
            return;

        Node previous = p._prev;
        Node next = p._next;
        if (previous == next) { // Solo hay dos elementos
            _first = (previous == _first) ? next : previous;
            _first._prev = null;
            _first._next = null;
        } else {
            previous._next = next;
            next._prev = previous;
        }

        --_playerCounter;
    }

    /**
     * Executes a hand of poker with the current players in the list.
     * It asks every player to make a play until all players have 
     * @param sb The value of the small blind for this hand
     * @param bb The value of the big blind for this hand
     * @param isPreflop Flag that indicates if this hand to be played is the preflop or not
     * @throws OnlyOnePlayerLeftException Exception thrown when there is only one player left to play mid round
     */
    public void playHand(final int sb, final int bb, final boolean isPreflop) throws OnlyOnePlayerLeftException {

        Node pNode = null;
        int currentBet = 0, maxBet = 0;
        int playsToMake = (isPreflop) ? activePlayersCounter() - 1 : activePlayersCounter();   // Number of players that have to, at least, fold
        int playersRemaining = playsToMake + 1;         // Number of players active


        // Forced plays by sb and bb if it is first round(Preflop)
        pNode = (isPreflop) ? smallBlindAndBigBlindPlays(sb, bb, playsToMake) : _first._next._next;

        // Keep players betting until all have reach the same bet or only one player is left
        maxBet = bb;
        while ( !(playsToMake == 0) ){  // If all players remaining have checked -> Exit loop

            // Player executes a command
            Command command = pNode._player.makePlay();

            // Command receives all necessary info
            command.receiveCurrentBet(maxBet);

            // Execute command
            CommandResult result = command.execute(sb, bb, maxBet);
            
            if(Game.DEBUG)
                System.out.printf("Jugador %s hace %s!\n\n", pNode._player.getName(), command.getCommandName());

            // Check number of active players to break normal execution if there is only one left
            if(result.folds()){
                --playersRemaining;
                if(playersRemaining == 1)
                    throw new OnlyOnePlayerLeftException("Only one player left to play mid round");
            }

            // Update remaining players loop
            playsToMake = result.raises() ? (activePlayersCounter() - 1) : (playsToMake - 1);
            
            // Update maxBet and get next player
            currentBet = result.bet();
            maxBet = Integer.max(maxBet, currentBet);
            pNode = getNextPlayerActive(pNode);
        }
    }

    /**
     * Retrieves the sum of all the players' bets
     * This method also resets the players pot
     * @see {@link Player} class for more info about the player's pot
     * @return The total amount of money in the pot
     */
    public int collectAllBets(){

        int totalPot = 0;
        Node pNode = _first;

        totalPot += pNode._player.placeBet();
        pNode = pNode._next;
        while(pNode != _first){
            totalPot += pNode._player.placeBet();
            pNode = pNode._next;
        }

        return totalPot;
    }

    /**
     * It gives both cards to the first player wihtout cards in hand.
     * If the list is empty or all players have cards in hand, this method will not have any effect.
     * @param c1 The first card to be given
     * @param c2 The second card to be given
     */
    public void shareOutAllCardsFromPlayer(Card c1, Card c2) {

        if (isEmpty())
            return;

        if(_first._player.getNumCards() == 0){
            _first._player.receiveCard(c1);
            _first._player.receiveCard(c2);
            return;
        }

        Node index = _first._next;
        while(index != _first){

            if(index._player.getNumCards() == 0){
                index._player.receiveCard(c1);
                index._player.receiveCard(c2);
                break;
            }

            index = index._next;
        }
    }
    
    /**
     * Passes the turn to the next player in the list.
     * This is made by setting the role of the next player to the dealer as the new dealer and updating the rest of the roles accordingly.
     */
    public void passTurn() {
        _first = _first._next;
        _last = _last._next;
        assignRolesToAllPlayers();
    }

    /**
     * Retrieves the hand information of all active players in the list.
     * @see {@link HandInfo} class for more info about the hand information
     * @return a list of all players information
     */
    public HandInfo[] getPlayerHandsInfo() {

        int size = activePlayersCounter();
        HandInfo[] info = new HandInfo[ size ];
        Node pNode = (!_first._player.hasFolded()) ? _first : getNextPlayerActive(_first);
        int i = 0;

        while(i < size){
            info[i++] = new HandInfo(pNode._player.getCards(), pNode._player);
            pNode = pNode._next;
        }

        return info;
    }

    /**
     * Counts the number of active players in the list.
     * A player is active if it has not folded in the current hand.
     * @return the number of active players in the list
     */
    public int activePlayersCounter(){
        
        if(isEmpty())
            return 0;

        int cont = _first._player.hasFolded() ? 0 : 1;
        Node current = _first._next;
        while ( current != _first ){
           cont += current._player.hasFolded() ? 0 : 1;
           current = current._next;
        }

        return cont;
    }

    /**
     * Resets the players in the list for a new hand.
     * This method only resets the players card pointers and sets the fold status to false.
     * @see {@link Player} class for more info about the player's cards and fold status
     */
    public void resetPlayers(){
        
        _first._player.resetCards();
        _first._player.setFold(false);

        Node current = _first._next;
        while (current != _first){
            current._player.resetCards();
            current._player.setFold(false);
            current = current._next;
        }
    }

    /**
     * Prints the state of all players in the list for debugging purposes in the console.
     */
    public void showPlayersStateDEBUG(){
        
        Node pNode = _first._next;
        System.out.println( _first._player.toString() );
        
        while(pNode != _first){
            System.out.println( pNode._player.toString() );
            pNode = pNode._next;
        }
    }

    /**
     * This method forces the small blind and big blind players to make their corresponding bets.
     * @param sb The value of the small blind for this hand
     * @param bb The value of the big blind for this hand
     * @param playsToMake The number of plays that have to take place in this round to end it.
     * @return The node of the next player to play after the big blind
     */
    private Node smallBlindAndBigBlindPlays(final int sb, final int bb, final int playsToMake){

        // Select first player to make a bet when :
        // 1. Only two players left
        // 2. More than one player left
        Node pNode = (playsToMake == 1) ? _first : _first._next;

        pNode._player.makeForcedBet(sb, bb);    // Small-blind
        pNode = pNode._next;
        pNode._player.makeForcedBet(sb, bb);    // Big-blind
        pNode = pNode._next;

        return pNode;
    }

    /**
     * Returns the next active player in the list starting from the given node.
     * An active player is one that has not folded in the current hand.
     * @param current The node of the current player
     * @return The node of the next active player, or null if no active players are left
     */
    private Node getNextPlayerActive(Node current){

        while ( current._player.hasFolded() ){
            current._player.assignRole(PlayerRole.NO_ROLE);
            current = current._next;
        }

        return current;
    }
    
    /**
     * Checks if the list is empty
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() { return size() == 0; }

    /**
     * Checks if the list is full
     * @return true if the list is full, false otherwise
     */
    public boolean isFull() { return size() == max(); }

    /**
     * Returns the number of players introduced originally in the game
     * @return 
     */
    public int size() { return _playerCounter; }

    /**
     * Returns the maximum number of players allowed in the list
     * @return
     */
    public int max() { return _maxNumberOfPlayers; }
}