package com.ucm.logic;

import com.ucm.commands.Command;
import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Card;
import com.ucm.middleclasses.CommandResult;


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
    private Node _index;


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

    public int playHand(final int sb, final int bb) {

        boolean handCompleted = false;
        int totalPot = 0;
        int maxBet = 0;
        int currentBet = 0;
        int playsToMake = size();

        Node pNode = _first._next;  // Starting from the small-blind
        while (!handCompleted){
            
            if(pNode._player.hasFolded()){
                pNode = pNode._next;
            }
            else{
                
                // Player executes a command
                Command command = pNode._player.makePlay(sb, bb, maxBet);

                // Execute command
                CommandResult result = command.execute(sb, bb, maxBet);

                // Update remaining players loop
                if(result.raises())
                    playsToMake = size();
                else
                    --playsToMake;

                currentBet = result.bet();
                maxBet = Integer.max(maxBet, currentBet);
                pNode = pNode._next;

                // If all players remaining have checked -> Exit loop
                handCompleted = (playsToMake == 0);
            }
            
        }

        // Collect all players bets
        totalPot += _first._player.placeBet();
        pNode = _first._next;
        while(pNode != _first){
            totalPot += pNode._player.placeBet();
            pNode = pNode._next;
        }
        
        return totalPot;
    }

    public void shareOutAllCardsFromPlayer(Card c1, Card c2) {

        if (isEmpty())
            return;

            
        if (_first._player.getNumCards() == 0) {
            _first._player.receiveCard(c1);
            _first._player.receiveCard(c2);
            _index = _first._next;
            return;
        }
        if (_index == _first)
            return;

        _index._player.receiveCard(c1);
        _index._player.receiveCard(c2);
        _index = _index._next;
    }

    public void retrieveAllCardsFromPlayers() {

        Node i = _first._next;
        retrieveAllCardsFromPlayer(_first._player);

        while (i != _first) {
            retrieveAllCardsFromPlayer(i._player);
            i = i._next;
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

    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

}