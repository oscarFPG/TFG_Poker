package com.ucm.logic;

import com.ucm.commands.Command;
import com.ucm.exceptions.OnlyOnePlayerLeftException;
import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Card;
import com.ucm.middleclasses.CommandResult;
import com.ucm.gameobjects.PlayerRole;


public class PlayerList {

    public class Node {
        Node _prev;
        Player _player;
        Node _next;

        public Node(Node prev, Player p, Node next) {
            _prev = prev;
            _player = p;
            _next = next;
        }
    }

    private Node _first;
    private Node _last;
    private int _playerCounter;
    private int _maxNumberOfPlayers;


    public PlayerList(int n) {
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = n;
    }


    public void addPlayer(Player p) {

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

    public void removePlayer(Player p) {

        if (isEmpty())
            return;

        if (_first._player == p)
            delete(_first);

        Node i = _first._next;
        while (i._player != p && i != _first && i != null)
            i = i._next;

        delete(i);
    }

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

    public void assignRolesToAllPlayers(){

        int n = activePlayersCounter();
        Node _current = _first;

        //NO HAY DEALER --> SOLO SB Y BB
        if ( n == 0 || n == 1 ) return;

        if (n == 2) {
            _current = getNextPlayerActive(_first);
            _current._player.setRole(PlayerRole.SMALL_BLIND);

            _current = getNextPlayerActive(_current._next);
            _current._player.setRole(PlayerRole.BIG_BLIND);
        }
        else {
            _current = getNextPlayerActive(_first);
            _current._player.setRole(PlayerRole.DEALER);

            _current = getNextPlayerActive(_current._next);
            _current._player.setRole(PlayerRole.SMALL_BLIND);

            _current = getNextPlayerActive(_current._next);
            _current._player.setRole(PlayerRole.BIG_BLIND);
        
            _current = getNextPlayerActive(_current._next);
            while (_current != _first){
                _current._player.setRole(PlayerRole.NO_ROLE);
                _current = getNextPlayerActive(_current._next);
            }
        }
    }

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

    public void playHand(final int sb, final int bb, final boolean isPreflop) throws OnlyOnePlayerLeftException {

        Node pNode = null;
        int currentBet = 0, maxBet = 0;
        int playsToMake = activePlayersCounter() - 1;   // Number of players that have to, at least, fold
        int playersRemaining = playsToMake + 1;         // Number of players active


        // Forced plays by sb and bb if it is first round(Preflop)
        pNode = (isPreflop) ? smallBlindAndBigBlindPlays(sb, bb, playsToMake) : _first._next._next;

        // Keep players betting until all have reach the same bet or only one player is left
        maxBet = bb;
        while ( !(playsToMake == 0) ){  // If all players remaining have checked -> Exit loop

            // Player executes a command
            Command command = pNode._player.makePlay(maxBet);

            // Execute command
            CommandResult result = command.execute(sb, bb, maxBet);
            
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

    public void retrieveAllCardsFromPlayer(Player p) {
        
        Card c1 = p.retrieveCard();
        if(c1 != null)
            c1.setAvailable(true);

        Card c2 = p.retrieveCard();
        if(c2 != null)
            c2.setAvailable(true);
    }

    public void retrieveAllCardsFromPlayers() {

        Node i = _first._next;
        retrieveAllCardsFromPlayer(_first._player);

        while (i != _first) {
            retrieveAllCardsFromPlayer(i._player);
            i = i._next;
        }
    }
    
    public void passTurn(){
        _first = _first._next;
        _last = _last._next;
        assignRolesToAllPlayers();
    }

    public Player selectWinner(){
        return _first._player;  // TODO : Search for winner player
    }

    private Node getNextPlayerActive(Node current){

        while ( current._player.hasFolded() ){
            current._player.setRole(PlayerRole.NO_ROLE);
            current = current._next;
        }

        return current;
    }

    public int activePlayersCounter(){
        
        if(isEmpty())
            return 0;

        int cont = _first._player.hasFolded() ? 0 : 1;
        Node _current = _first._next;
        while ( _current != _first){
           cont += _current._player.hasFolded() ? 0 : 1;
           _current = _current._next;
        }

        return cont;
    }

    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

    

}