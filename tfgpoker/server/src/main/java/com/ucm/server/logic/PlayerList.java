package com.ucm.server.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.commands.Command;
import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;

public class PlayerList {

    private static final Logger log = LogManager.getLogger(PlayerList.class);

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
        }
        else {
            _last._next = newNode;
        }

        log.debug("Player {} added!", p.getName());
        ++_playerCounter;
    }

    public void assignRolesToAllPlayers() {

        int n = activePlayersCounter();
        Node _current = _first;

        // NO HAY DEALER --> SOLO SB Y BB
        if (n == 0 || n == 1)
            return;


        if (n == 2) {
            _current = getNextPlayerActive(_first);
            _current._player.assignRole(PlayerRole.SMALL_BLIND);
            log.debug("Player {} receives role {}", _current._player.getName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current._next);
            _current._player.assignRole(PlayerRole.BIG_BLIND);
            log.debug("Player {} receives role {}", _current._player.getName(), PlayerRole.BIG_BLIND.name());
        }
        else {
            _current = getNextPlayerActive(_first);
            _current._player.assignRole(PlayerRole.DEALER);
            log.debug("Player {} receives role {}", _current._player.getName(), PlayerRole.DEALER.name());

            _current = getNextPlayerActive(_current._next);
            _current._player.assignRole(PlayerRole.SMALL_BLIND);
            log.debug("Player {} receives role {}", _current._player.getName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current._next);
            _current._player.assignRole(PlayerRole.BIG_BLIND);
            log.debug("Player {} receives role {}", _current._player.getName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current._next);
            while (_current != _first) {
                _current._player.assignRole(PlayerRole.NO_ROLE);
                _current = getNextPlayerActive(_current._next);
                log.debug("Player {} receives role {}", _current._player.getName(), PlayerRole.NO_ROLE.name());
            }
        }
    }

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

    public void playHand(final int sb, final int bb, final boolean isPreflop) throws OnlyOnePlayerLeftException {

        Node pNode = null;
        int currentBet = 0, maxBet = 0;
        int playsToMake = (isPreflop) ? activePlayersCounter() - 1 : activePlayersCounter();
        int playersRemaining = playsToMake + 1; // Number of players active

        // Forced plays by sb and bb if it is first round(Preflop)
        pNode = (isPreflop) ? smallBlindAndBigBlindPlays(sb, bb, playsToMake) : _first._next._next;
        maxBet = (isPreflop) ? bb : currentBet;
        while (!(playsToMake == 0)) {

            if(isPreflop)
                log.debug("Current small blind: {}, current big blind: {}, current max bet: {}", sb, bb, maxBet);
            else
                log.debug("Last maximum bet is {}", maxBet);


            Command command = pNode._player.makePlay(maxBet);
            command.receiveCurrentBet(maxBet);
            CommandResult result = command.execute(sb, bb, maxBet);

            if (result.folds()) {
                --playersRemaining;
                if (playersRemaining == 1)
                    throw new OnlyOnePlayerLeftException("Only one player left to play mid round");
            }

            playsToMake = result.raises() ? (activePlayersCounter() - 1) : (playsToMake - 1);
            log.debug("{} plays left to play", playsToMake);

            currentBet = result.bet();
            maxBet = Integer.max(maxBet, currentBet);
            log.debug("Current bet is {} and maximum bet is {}", currentBet, maxBet);

            pNode = getNextPlayerActive(pNode);
            log.debug("Next player to play is {}", pNode._player.getName());
        }
    }

    public int collectAllBets() {

        int totalPot = 0;
        Node pNode = _first;

        totalPot += pNode._player.placeBet();
        pNode = pNode._next;
        while (pNode != _first) {
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
            log.debug("Player {} received the cards {} and {}", _first._player.getName(), c1.toString(), c2.toString());
            return;
        }

        Node index = _first._next;
        while (index != _first) {

            if (index._player.getNumCards() == 0) {
                index._player.receiveCard(c1);
                index._player.receiveCard(c2);
                log.debug("Player {} received the cards {} and {}", index._player.getName(), c1.toString(),
                        c2.toString());
                break;
            }

            index = index._next;
        }
    }

    public void passTurn() {
        _first = _first._next;
        _last = _last._next;
        assignRolesToAllPlayers();
    }

    public HandInfo[] getPlayerHandsInfo() {

        int size = activePlayersCounter();
        HandInfo[] info = new HandInfo[size];
        Node pNode = (!_first._player.hasFolded()) ? _first : getNextPlayerActive(_first);
        int i = 0;

        while (i < size) {
            info[i++] = new HandInfo(pNode._player.getCards(), pNode._player);
            pNode = pNode._next;
        }

        return info;
    }

    public int activePlayersCounter() {

        if (isEmpty())
            return 0;

        int cont = _first._player.hasFolded() ? 0 : 1;
        Node current = _first._next;
        while (current != _first) {
            cont += current._player.hasFolded() ? 0 : 1;
            current = current._next;
        }

        return cont;
    }

    public void resetPlayers() {

        _first._player.resetCards();
        _first._player.setFold(false);

        Node current = _first._next;
        while (current != _first) {
            current._player.resetCards();
            current._player.setFold(false);
            current = current._next;
        }
    }

    public void showPlayersStateDEBUG() {

        Node pNode = _first._next;
        log.debug("{}", _first._player.toString());

        while (pNode != _first) {
            log.debug("{}", pNode._player.toString());
            pNode = pNode._next;
        }
    }

    private Node smallBlindAndBigBlindPlays(final int sb, final int bb, final int playsToMake) {

        // Select first player to make a bet when :
        // 1. Only two players left
        // 2. More than one player left
        Node pNode = (playsToMake == 1) ? _first : _first._next;

        pNode._player.makeForcedBet(sb, bb); // Small-blind
        pNode = pNode._next;
        pNode._player.makeForcedBet(sb, bb); // Big-blind
        pNode = pNode._next;

        return pNode;
    }

    private Node getNextPlayerActive(Node current) {

        Node iNode = current._next;
        if(!iNode._player.hasFolded())
            return iNode;


        boolean found = false;
        while (!found && iNode._player != current._player) {
            iNode = iNode._next;
            if(!iNode._player.hasFolded())
                found = true;
        }

        return iNode;
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
}