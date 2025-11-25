package com.ucm.logic;

import java.util.List;

import com.ucm.middleclasses.DTOClient;
import com.ucm.commands.Command;
import com.ucm.exceptions.OnlyOnePlayerLeftException;
import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.PlayerRole;
import com.ucm.middleclasses.CommandResult;
import com.ucm.middleclasses.HandInfo;



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


    public PlayerList(final int maxSize, final List<DTOClient> clients) {
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = maxSize;

        for(DTOClient client : clients){
            addPlayer( new Player(client.playerID(), client.playerName(), 1000, client.socket()) );
        }
    }


    private void addPlayer(Player p) {

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

    private void removePlayer(Player p) {

        if (isEmpty())
            return;

        if (_first._player == p)
            delete(_first);

        Node iNode = _first._next;
        while (iNode._player != p && iNode != _first && iNode != null)
            iNode = iNode._next;

        delete(iNode);
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
        Player current = _first._player;

        //NO HAY DEALER --> SOLO SB Y BB
        if ( n == 0 || n == 1 ) return;

        if (n == 2) {
            current = getNextPlayerActive(_first._player);
            current.setRole(PlayerRole.SMALL_BLIND);

            current = getNextPlayerActive(current);
            current.setRole(PlayerRole.BIG_BLIND);
        }
        else {
            current = getNextPlayerActive(_first._player);
            current.setRole(PlayerRole.DEALER);

            current = getNextPlayerActive(current);
            current.setRole(PlayerRole.SMALL_BLIND);

            current = getNextPlayerActive(current);
            current.setRole(PlayerRole.BIG_BLIND);
        
            current = getNextPlayerActive(current);
            while (current != _first._player){
                current.setRole(PlayerRole.NO_ROLE);
                current = getNextPlayerActive(current);
            }
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
    
    public void passTurn(){
        _first = _first._next;
        _last = _last._next;
        assignRolesToAllPlayers();
    }

    public HandInfo[] getPlayerHandsInfo(){

        int size = activePlayersCounter();
        HandInfo[] info = new HandInfo[ size ];
        Player p = (!_first._player.hasFolded()) ? _first._player : getNextPlayerActive(_first._player);
        int i = 0;

        while(i < size){
            info[i++] = new HandInfo(p.getCards(), p);
            p = getNextPlayerActive(p);
        }

        return info;
    }

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

    public void showPlayersStateDEBUG(){
        
        Node pNode = _first._next;
        System.out.println( _first._player.toString() );
        
        while(pNode != _first){
            System.out.println( pNode._player.toString() );
            pNode = pNode._next;
        }
    }

    public Player smallBlindAndBigBlindPlays(final int sb, final int bb, final int playsToMake){

        // Select first player to make a bet when :
        // 1. Only two players left
        // 2. More than one player left
        Node pNode = (playsToMake == 1) ? _first : _first._next;

        pNode._player.makeForcedBet(sb, bb);    // Small-blind
        pNode = pNode._next;
        pNode._player.makeForcedBet(sb, bb);    // Big-blind
        pNode = pNode._next;

        return pNode._player;
    }

    public Player getNextPlayerActive(Player p){
        
        if(_first._player == p)
            return _first._player;

        Node current = _first._next;
        while (current._player != _first._player){
            current = current._next;
        }

        return current._player;
    }
    
    public Player getFirst() { return _first._player; }
    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

}