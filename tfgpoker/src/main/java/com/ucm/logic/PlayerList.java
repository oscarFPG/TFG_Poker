package com.ucm.logic;

import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Card;
<<<<<<< Updated upstream
=======
import com.ucm.middleclasses.CommandResult;
>>>>>>> Stashed changes

public class PlayerList {

    private Player[] players;
    private int _contPlayers;

    public PlayerList() {
        _contPlayers = 0;
        players = new Player[Game.NUM_MAX_PLAYERS];
    }

<<<<<<< Updated upstream
=======
    private Node _first;
    private Node _last;
    private int _playerCounter;
    private int _maxNumberOfPlayers;
    private Node _index;

>>>>>>> Stashed changes
    public PlayerList(int n) {
        _contPlayers = 0;
        players = new Player[n];
    }

<<<<<<< Updated upstream
    public int getContPlayers() {
        return _contPlayers;
    }

    public void addPlayer(Player p) {
        players[_contPlayers] = p;
        _contPlayers++;
    }

    public void retrieveAllCardsFromPlayers() {
        for (Player p : players) {
            for (Card c : p.getCards()) {
                c.setAvailable(true);
            }
=======
    public void addPlayer(Player p) {

        Node newNode = new Node(_last, p, _first);
        if (isEmpty()) {
            _first = newNode;
            _last = newNode;
            _first._next = newNode;
            _first._prev = newNode;
            _last._next = newNode;
            _last._prev = newNode;
        } else {
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

        Node pNode = _first._next; // Starting from the small-blind
        while (!handCompleted) {

            // Player executes a command
            Command command = pNode._player.makePlay(sb, bb, maxBet);

            // Execute command
            CommandResult result = command.execute(sb, bb, maxBet);

            // Player wants to fold
            if (result.folds())
                pNode._player.fold();

            // Update remaining players loop
            if (result.raises())
                playsToMake = size();
            else
                --playsToMake;

            currentBet = result.bet();
            maxBet = Integer.max(maxBet, currentBet);
            pNode = pNode._next;

            // If all players remaining have checked -> Exit loop
            handCompleted = (playsToMake == 0);
        }

        // Collect all players bets
        pNode = _first;
        totalPot += pNode._player.placeBet();

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
>>>>>>> Stashed changes
        }
    }

    public void retrieveAllCardsFromPlayer(Player p) {
<<<<<<< Updated upstream
        for (Card c : p.getCards()) {
            c.setAvailable(true);
        }
    }
=======

        Card c1 = p.retrieveCard();
        if (c1 != null)
            c1.setAvailable(true);

        Card c2 = p.retrieveCard();
        if (c2 != null)
            c2.setAvailable(true);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return size() == max();
    }

    public int size() {
        return _playerCounter;
    }

    public int max() {
        return _maxNumberOfPlayers;
    }

>>>>>>> Stashed changes
}