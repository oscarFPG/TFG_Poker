package com.ucm.server.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.commands.Command;
import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.PotDistribution;


public class PlayerList {

    private static final Logger log = LogManager.getLogger(PlayerList.class);

    private class Node {

        public int _id;
        public IPokerPlayer _player;
        public boolean _isEliminated;

        public Node _prev;
        public Node _next;

        public Node(int id, Node prev, IPokerPlayer p, Node next) {
            
            _id = id;
            _player = p;
            _isEliminated = false;

            _prev = prev;
            _next = next;
        }
    }

    private int _idCounter;
    private Node _first;
    private Node _last;
    private int _playerCounter;
    private int _maxNumberOfPlayers;

    private PotManager _potManager;

    public PlayerList(int n) {
        _idCounter = 0;
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = n;

        _potManager = new PotManager(n);
    }

    
    public void addPlayer(IPokerPlayer p) {

        if(isFull())
            return;


        Node newNode = new Node(_idCounter, null, p, null);
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

        _potManager.addPlayer(_idCounter);
        ++_idCounter;
        ++_playerCounter;

        log.debug("Player {} added!", p.getPlayerName());
    }

    public void removePlayer(IPokerPlayer p) {

        if (isEmpty())
            return;

        if (_first._player == p)
            delete(_first);

        Node iNode = _first._next;
        while (iNode._player != p && iNode != _first && iNode != null)
            iNode = iNode._next;

        delete(iNode);
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

            List<PlayerRole> roles = new ArrayList<>(List.of(
                PlayerRole.DEALER, 
                PlayerRole.SMALL_BLIND, 
                PlayerRole.BIG_BLIND,
                PlayerRole.UNDER_THE_GUN,
                PlayerRole.MIDDLE_POSITION,
                PlayerRole.CUT_OFF,
                PlayerRole.NO_ROLE,
                PlayerRole.NO_ROLE,
                PlayerRole.NO_ROLE
            ));

            PlayerRole currentRole = roles.removeFirst();
            _current._player.receiveRole(currentRole);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.DEALER.name());

            _current = getNextPlayerActive(_current);
            currentRole = roles.removeFirst();
            _current._player.receiveRole(currentRole);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current);
            currentRole = roles.removeFirst();
            _current._player.receiveRole(currentRole);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());

            _current = getNextPlayerActive(_current);
            while (_current != _first) {
                currentRole = roles.removeFirst();
                _current._player.receiveRole(currentRole);
                _current = getNextPlayerActive(_current);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), currentRole.name());
            }
        }
    }

    public void shareOutAllCardsFromPlayer(Card c1, Card c2) {

        if (isEmpty())
            return;

        if (!_first._isEliminated && _first._player.getCardsCounter() == 0) {
            _first._player.receiveCard(c1);
            _first._player.receiveCard(c2);
            return;
        }

        Node index = _first._next;
        while (index != _first) {

            if (!index._isEliminated && index._player.getCardsCounter() == 0) {
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

        int playsToMake = (isPreflop) ? activePlayersCounter() - 1 : activePlayersCounter();
        int playersRemaining = (isPreflop) ? playsToMake + 1 : playsToMake; // Number of players active

        // Forced plays by sb and bb if it is first round(Preflop)
        Node pNode = (isPreflop) ? smallBlindAndBigBlindPlays(sb, bb, playsToMake) : _first._next._next;

        // Broadcast to all players except to playerOnTurn
        Node iNode = (_first != pNode) ? _first : _first._next;
        while (iNode != pNode) {
            iNode._player.notifyTurnWait();
            iNode = iNode._next;
        }

        Node playerOnTurnNode = pNode;
        IPokerPlayer playerOnTurn = pNode._player;
        int maxBet = (isPreflop) ? bb : 0;
        while ( playsToMake > 0 ) {

            if(isPreflop)
                log.debug("Current small blind: {}, current big blind: {}, current max bet: {}", sb, bb, maxBet);
            else
                log.debug("Last maximum bet is {}", maxBet);


            // Ask for an action by the player to execute
            Command command = null;
            log.debug("It's is {} turn to play", playerOnTurn.getPlayerName());
            while (command == null) {

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
            int currentBet = result.folds() ? 0 : result.bet();
            maxBet = Integer.max(maxBet, currentBet);

            log.debug("{} plays left to play", playsToMake);
            log.debug("Current bet is {} and maximum bet is {}", currentBet, maxBet);

            playerOnTurnNode = getNextPlayerActive(playerOnTurnNode);
            playerOnTurn = playerOnTurnNode._player;
        }

        // Notify all players that the betting round has ended
        _first._player.notifyRoundEnded();
        iNode = _first._next;
        while (iNode != _first) {
            iNode._player.notifyRoundEnded();
            iNode = iNode._next;
        }

    }

    public void updatePlayerPots() {

        Node iNode = _first;
        if(!iNode._isEliminated)
            _potManager.updatePlayerPot(iNode._id, iNode._player.placeOnBetMoney(), iNode._player.isFolded());

        iNode = iNode._next;
        while(iNode != _first){
            if(!iNode._isEliminated)
                _potManager.updatePlayerPot(iNode._id, iNode._player.placeOnBetMoney(), iNode._player.isFolded());
            iNode = iNode._next;
        }

        log.debug("All players pots updated!");
    }

    public void passTurn() {
        _first = _first._next;
        _last = _last._next;
        assignRolesToAllPlayers();
    }

    public void resetPlayers() {

        _first._player.retrieveCards();
        _first._player.unfoldPlayer();
        _first._player.setIsWinner(false);
        if(_first._player.getMoneyOffBet() == 0){
            _first._isEliminated = true;
        }

        Node current = _first._next;
        while (current != _first) {
            current._player.retrieveCards();
            current._player.unfoldPlayer();
            current._player.setIsWinner(false);
            if(current._player.getMoneyOffBet() == 0){
                current._isEliminated = true;
            }

            current = current._next;
        }
    }

    public void calculatePrizeDistribution(final List<PlayerEvaluation> players) {

        List<PotDistribution> distribution = _potManager.calculatePrizeDistribution(players);
        for(PotDistribution dist : distribution){
            givePriceToPlayerWithID(dist.playerID(), dist.potPrize());
        }
    }

    public void calculatePrizeForPlayerLeft() {

        Node winner = ( _first._player.isFolded() && !_first._isEliminated ) ? null : _first;
        Node iNode = _first._next;
        while (winner == null && iNode != _first) {
            winner = ( iNode._player.isFolded()  && !iNode._isEliminated) ? null : iNode;
            iNode = iNode._next;
        }

        PotDistribution distribution = _potManager.calculatePrizeForPlayer(winner._id);
        givePriceToPlayerWithID(distribution.playerID(), distribution.potPrize());
        winner._player.setIsWinner(true);
    }

    public boolean checkEndOfGame() {

        int playersNotEliminated = _playerCounter;
        Node iNode = _first;
        playersNotEliminated = (iNode._isEliminated) ? playersNotEliminated - 1 : playersNotEliminated;

        iNode = iNode._next;
        while(iNode != _first){
            playersNotEliminated = (iNode._isEliminated) ? playersNotEliminated - 1 : playersNotEliminated;
            iNode = iNode._next;
        }

        return (playersNotEliminated == 1);
    }

    public void sendTableCardToAllPlayers(final Card card) {

        if(!_first._isEliminated)
            _first._player.receiveTableCard(card);

        Node i = _first._next;
        while (i != _first){
            if(!i._isEliminated)
                i._player.receiveTableCard(card);
            i = i._next;
        }
    }

    public void notifyRankingsToAllPlayers() {

        Node i = _first;
        if(!i._isEliminated) {
            if(i._player.isWinner())
                i._player.notifyHandWinner();
            else
                i._player.notifyHandLoser();

            i._player.receiveNewMoney( i._player.getMoneyOffBet() );
            i = i._next;
        }
        
        while(i != _first) {

            if(i._isEliminated){
                i = i._next;
                continue;
            }

            if(i._player.isWinner())
                i._player.notifyHandWinner();
            else
                i._player.notifyHandLoser();

            i._player.receiveNewMoney( i._player.getMoneyOffBet() );
            i = i._next;
        }

    }

    public void notifyHandEndsByFold() {
        
        Node iNode = _first;

        iNode._player.notifyHandEndsByFolds();
        iNode = iNode._next;
        while(iNode != _first) {
            iNode._player.notifyHandEndsByFolds();
            iNode = iNode._next;
        }
    }

    public void notifyGameEnds(final boolean gameEnds) {

        Node iNode = _first;
        if(gameEnds)
            iNode._player.notifyGameEnded();
        else
            iNode._player.notifyGameKeeps();

        iNode = iNode._next;
        while(iNode != _first) {
            if(gameEnds)
                iNode._player.notifyGameEnded();
            else
                iNode._player.notifyGameKeeps();
            iNode = iNode._next;
        }
    }

    public List<HandInfo> getPlayerHandsInfo() {

        List<HandInfo> info = new ArrayList<>(_playerCounter);
        
        Node pNode = _first._next;
        if(!pNode._isEliminated && !_first._player.isFolded())
            info.add( new HandInfo(_first._id, _first._player.getPlayerCards()) );
        
        while (pNode != _first) {
            if(!pNode._isEliminated && !pNode._player.isFolded())
                info.add( new HandInfo(pNode._id, pNode._player.getPlayerCards()) );
            
            pNode = pNode._next;
        }

        return info;
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

    private int activePlayersCounter() {

        if (isEmpty())
            return 0;

        int cont = (
            _first._player.isFolded() 
            || _first._isEliminated 
        ) ? 0 : 1;
        
        Node current = _first._next;
        while (current != _first) {
            cont += (
                current._player.isFolded() 
                || current._isEliminated 
            ) ? 0 : 1;
            current = current._next;
        }

        return cont;
    } 

    private Node getNextPlayerActive(Node current) {

        Node iNode = current._next;
        if(!iNode._isEliminated && !iNode._player.isFolded())
            return iNode;


        boolean found = false;
        while (!found && iNode != current) {
            iNode = iNode._next;
            if(!iNode._isEliminated && !iNode._player.isFolded())
                found = true;
        }

        return iNode;
    }

    private void givePriceToPlayerWithID(final int id, final int amount){

        Node winner = (_first._id == id && !_first._isEliminated) ? _first : null;
        if(winner == null){
            
            Node i = _first._next;
            while(winner == null && i != _first){
                winner = (i._id == id && !i._isEliminated) ? i : null;
                i = i._next;
            }
        }

        winner._player.receivePriceMoney(amount);
        winner._player.setIsWinner(true);
    }


    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

}