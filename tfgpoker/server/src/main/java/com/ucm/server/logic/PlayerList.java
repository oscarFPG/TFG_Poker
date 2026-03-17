package com.ucm.server.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.commands.Command;
import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;

public class PlayerList {

    private static final Logger log = LogManager.getLogger(PlayerList.class);

    private class Node {

        public int _id;
        public IPokerPlayer _player;
        public int _playerPot;

        public Node _prev;
        public Node _next;

        public Node(int id, Node prev, Player p, Node next) {
            
            _id = id;
            _player = p;
            _playerPot = 0;

            _prev = prev;
            _next = next;
        }
    }

    private int _idCounter;
    private Node _first;
    private Node _last;
    private int _playerCounter;
    private int _maxNumberOfPlayers;


    public PlayerList(int n) {
        _idCounter = 0;
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = n;
    }

    
    public void addPlayer(Player p) {

        Node newNode = new Node(_idCounter++, null, p, null);
        if (isEmpty()) {
            _first = newNode;
            _last = newNode;
            newNode._next = newNode;
            newNode._prev = newNode;
        } 
        else {
            newNode._prev = _last;
            newNode._next = _first;

            _last._next = newNode;
            _first._prev = newNode;

            _last = newNode;
        }

        log.debug("Player {} added!", p.getPlayerName());
        ++_playerCounter;
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
        }
        else {
            previous._next = next;
            next._prev = previous;
        }

        --_playerCounter;
    }


    public void assignRolesToAllPlayers() {

        int n = activePlayersCounter();
        Node _current = _first;

        // NO HAY DEALER --> SOLO SB Y BB
        if (n == 0 || n == 1)
            return;


        if (n == 2) {
            _current._player.receiveRole(PlayerRole.SMALL_BLIND);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current);
            _current._player.receiveRole(PlayerRole.BIG_BLIND);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());
        }
        else {
            _current._player.receiveRole(PlayerRole.DEALER);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.DEALER.name());

            _current = getNextPlayerActive(_current);
            _current._player.receiveRole(PlayerRole.SMALL_BLIND);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current);
            _current._player.receiveRole(PlayerRole.BIG_BLIND);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());

            _current = getNextPlayerActive(_current);
            while (_current != _first) {
                _current._player.receiveRole(PlayerRole.NO_ROLE);
                _current = getNextPlayerActive(_current);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.NO_ROLE.name());
            }
        }
    }

    public void shareOutAllCardsFromPlayer(Card c1, Card c2) {

        if (isEmpty())
            return;

        if (_first._player.getCardsCounter() == 0) {
            _first._player.receiveCard(c1);
            _first._player.receiveCard(c2);
            return;
        }

        Node index = _first._next;
        while (index != _first) {

            if (index._player.getCardsCounter() == 0) {
                index._player.receiveCard(c1);
                index._player.receiveCard(c2);
                break;
            }

            index = index._next;
        }
    }

    private Node smallBlindAndBigBlindPlays(final int sb, final int bb, final int playsToMake) {

        // Select as small blind:
        // 1. First player if there is only two players -> playsToMake == 1
        // 2. Next player from first if there is more than two players -> playsToMake > 1
        Node pNode = (playsToMake == 1) ? _first : _first._next;
        pNode._player.actionSmallBlindBet(sb);

        pNode = pNode._next;
        pNode._player.actionBigBlindBet(bb);

        return pNode._next;
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


            // Broadcast to all players except to playerOnTurn
            IPokerPlayer playerOnTurn = pNode._player;
            Node iNode = (_first._player != playerOnTurn) ? _first : _first._next;
            while (iNode._player != playerOnTurn) {
                iNode._player.notifyTurnWait();
                iNode = iNode._next;
            }

            // Ask for an action by the player to execute
            Command command = null;
            while (command == null) {

                log.debug("It's is {} turn to play", pNode._player.getPlayerName());
                playerOnTurn.notifyTurnPlay();
                
                String commandString = playerOnTurn.actionMakePlay(sb, bb, maxBet);
                String[] commandFormatted = commandString.split(" ");

                command = Command.parseCommand(commandFormatted, playerOnTurn);
                command = command.validate(maxBet) ? command : null;
            }

            CommandResult result = command.execute(sb, bb, maxBet);
            if (result.folds()) {
                --playersRemaining;
                if (playersRemaining == 1)
                    throw new OnlyOnePlayerLeftException("Only one player left to play mid round");
            }

            playsToMake = result.raises() ? (activePlayersCounter() - 1) : (playsToMake - 1);
            currentBet = result.folds() ? 0 : result.bet();
            maxBet = Integer.max(maxBet, currentBet);

            log.debug("{} plays left to play", playsToMake);
            log.debug("Current bet is {} and maximum bet is {}", currentBet, maxBet);

            pNode = getNextPlayerActive(pNode);
        }

        // Notify all players that the betting round has ended
        _first._player.notifyRoundEnded();
        Node iNode = _first._next;
        while (iNode != _first) {
            iNode._player.notifyRoundEnded();
            iNode = iNode._next;
        }

    }

    public void managePlayerPots(){

    }

    public int collectAllBets() {

        int totalPot = 0;
        Node pNode = _first;

        totalPot += pNode._player.placeOnBetMoney();
        pNode = pNode._next;
        while (pNode != _first) {
            totalPot += pNode._player.placeOnBetMoney();
            pNode = pNode._next;
        }

        return totalPot;
    }

    public void passTurn() {
        _first = _first._next;
        _last = _last._next;
        assignRolesToAllPlayers();
    }

    public void resetPlayers() {

        _first._player.retrieveCards();
        _first._player.unfoldPlayer();

        Node current = _first._next;
        while (current != _first) {
            current._player.retrieveCards();
            current._player.unfoldPlayer();
            current = current._next;
        }
    }


    public void sendTableCardToAllPlayers(final Card card){

        _first._player.receiveTableCard(card);
        Node i = _first._next;
        while (i != _first){
            i._player.receiveTableCard(card);
            i = i._next;
        }
    }

    public void notifyRankingsToAllPlayers(){

        Node i = _first;
        if(i._player.isWinner())
            i._player.notifyHandWinner();
        else
            i._player.notifyHandLoser();

        i._player.receiveNewMoney( i._player.getMoneyOffBet() );
        i = i._next;

        while(i != _first) {

            if(i._player.isWinner())
                i._player.notifyHandWinner();
            else
                i._player.notifyHandLoser();

            i._player.receiveNewMoney( i._player.getMoneyOffBet() );
            i = i._next;
        }

    }


    public HandInfo[] getPlayerHandsInfo() {

        int size = activePlayersCounter();
        HandInfo[] info = new HandInfo[size];
        Node pNode = (!_first._player.isFolded()) ? _first : getNextPlayerActive(_first);
        int i = 0;

        while (i < size) {
            info[i++] = new HandInfo(pNode._player.getPlayerCards(), pNode._player);
            pNode = pNode._next;
        }

        return info;
    }

    private int activePlayersCounter() {

        if (isEmpty())
            return 0;

        int cont = _first._player.isFolded() ? 0 : 1;
        Node current = _first._next;
        while (current != _first) {
            cont += current._player.isFolded() ? 0 : 1;
            current = current._next;
        }

        return cont;
    } 

    private Node getNextPlayerActive(Node current) {

        Node iNode = current._next;
        if(!iNode._player.isFolded())
            return iNode;


        boolean found = false;
        while (!found && iNode._player != current._player) {
            iNode = iNode._next;
            if(!iNode._player.isFolded())
                found = true;
        }

        return iNode;
    }


    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

}