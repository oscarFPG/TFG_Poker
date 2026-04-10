package com.ucm.server.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.commands.Command;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.PotDistribution;


public class PlayerList {

    private static final Logger log = LogManager.getLogger(PlayerList.class);

    private class Node {

        public IPokerPlayer _player;
        public Node _prev;
        public Node _next;

        public Node(Node prev, IPokerPlayer p, Node next) {
            
            _player = p;
            _prev = prev;
            _next = next;
        }
    }

    private Node _first;
    private Node _last;
    private int _playerCounter;
    private int _maxNumberOfPlayers;

    private PotManager _potManager;


    public PlayerList(int n) {
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = n;

        _potManager = new PotManager(n);
    }

    
    public void addPlayer(IPokerPlayer p) {

        if(isFull())
            return;


        Node newNode = new Node(null, p, null);
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

        _potManager.addPlayer( newNode._player.getPlayerId() );
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
            _current._player.notifyPlayerRole(PlayerRole.SMALL_BLIND);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current);
            _current._player.receiveRole(PlayerRole.BIG_BLIND);
            _current._player.notifyPlayerRole(PlayerRole.BIG_BLIND);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());
        }
        else {

            List<PlayerRole> roles = new ArrayList<>(
                List.of(
                    PlayerRole.DEALER, 
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND,
                    PlayerRole.UNDER_THE_GUN,
                    PlayerRole.MIDDLE_POSITION,
                    PlayerRole.CUT_OFF,
                    PlayerRole.NO_ROLE,
                    PlayerRole.NO_ROLE,
                    PlayerRole.NO_ROLE
                )
            );

            PlayerRole currentRole = roles.removeFirst();
            _current._player.receiveRole(currentRole);
            _current._player.notifyPlayerRole(currentRole);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.DEALER.name());

            _current = getNextPlayerActive(_current);
            currentRole = roles.removeFirst();
            _current._player.receiveRole(currentRole);
            _current._player.notifyPlayerRole(currentRole);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());

            _current = getNextPlayerActive(_current);
            currentRole = roles.removeFirst();
            _current._player.receiveRole(currentRole);
            _current._player.notifyPlayerRole(currentRole);
            log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());

            _current = getNextPlayerActive(_current);
            while (_current != _first ) {
                currentRole = roles.removeFirst();
                _current._player.receiveRole(currentRole);
                _current._player.notifyPlayerRole(currentRole);
                _current = getNextPlayerActive(_current);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), currentRole.name());
            }
        }
    }

    public void shareOutAllCardsFromPlayer(Card c1, Card c2) {

        if (isEmpty())
            return;

        if (!_first._player.isEliminated() && _first._player.getCardsCounter() == 0) {
            _first._player.receiveCard(c1);
            _first._player.notifyPlayerCard(c1);
            _first._player.receiveCard(c2);
            _first._player.notifyPlayerCard(c2);

            log.debug("Player {} receives the cards: {} {}", _first._player.getPlayerName(), c1.toString(), c2.toString());
            return;
        }

        Node index = _first._next;
        while (index != _first) {

            if (!index._player.isEliminated() && index._player.getCardsCounter() == 0) {
                index._player.receiveCard(c1);
                index._player.notifyPlayerCard(c1);
                index._player.receiveCard(c2);
                index._player.notifyPlayerCard(c2);

                log.debug("Player {} receives the cards: {} {}", index._player.getPlayerName(), c1.toString(), c2.toString());
                break;
            }

            index = index._next;
        }
    }

    private void smallBlindAndBigBlindPlays(final int sb, final int bb, final int playersRemaining) {

        // Select as small blind:
        // 1. First player if there is only two players -> playsToMake == 1
        // 2. Next player from first if there is more than two players -> playsToMake > 1
        Node pNode = (playersRemaining == 2) ? _first : _first._next;
        pNode._player.actionSmallBlindBet(sb);
        pNode._player.notifySmallBlindBet(sb);
        log.debug(
            "Player {} puts {}$ as SMALL_BLIND. Now it has {}$", 
            pNode._player.getPlayerName(), pNode._player.getMoneyOnBet(), pNode._player.getMoneyOffBet()
        );

        pNode = pNode._next;
        pNode._player.actionBigBlindBet(bb);
        pNode._player.notifyBigBlindBet(bb);
        log.debug(
            "Player {} puts {}$ as BIG_BLIND. Now it has {}$", 
            pNode._player.getPlayerName(), pNode._player.getMoneyOnBet(), pNode._player.getMoneyOffBet()
        );

    }

    public void playHand(final int sb, final int bb, final boolean isPreflop) throws OnlyOnePlayerLeftException {

        if(checkAllPlayersAllIn()) { // Avoid asking if all active players have used all their money
            notifyRoundEnded();
            return;
        }


        int playersRemaining = activePlayersCounter();        
        Node playerOnTurn = calculatePlayerOnTurn(playersRemaining, isPreflop);
        Node pivotPlayer = calculatePivotPlayer(playersRemaining, isPreflop);
        int maxBet = (isPreflop) ? bb : 0;
        int currentBet = maxBet;


        if(playersRemaining == 1) { // In case only one player can play -> Cannot play alone
            notifyRoundEnded();
            return;
        }

        if(isPreflop)
            smallBlindAndBigBlindPlays(sb, bb, playersRemaining);
        notifyWaitExceptTo(playerOnTurn);

        do {

            if(isPreflop)
                log.debug("Current small blind: {}, current big blind: {}, current max bet: {}", sb, bb, maxBet);
            else
                log.debug("Last maximum bet is {}", maxBet);


            if(playerOnTurn._player.isAllIn()) {
                playerOnTurn = getNextPlayerActive(playerOnTurn);
                continue;
            }


            Command command = askCommandToPlayer(playerOnTurn._player, sb, bb, maxBet);
            CommandResult result = command.execute(sb, bb, maxBet);
            if(result.folds()) {
                --playersRemaining; 
                if (playersRemaining == 1) {
                    updateHandState();
                    throw new OnlyOnePlayerLeftException();
                }
            }

            currentBet = result.folds() ? 0 : result.bet();
            maxBet = Integer.max(maxBet, currentBet);

            pivotPlayer = result.raises() ? playerOnTurn : pivotPlayer;
            playerOnTurn = getNextPlayerActive(playerOnTurn);

            log.debug("Current bet is {}", currentBet);
            log.debug("Maximum bet is {}", maxBet);
        }
        while( pivotPlayer != playerOnTurn && playersRemaining != 0);

        updateHandState();
        notifyRoundEnded();
    }

    private Command askCommandToPlayer(IPokerPlayer player, final int sb, final int bb, final int maxBet) {

        log.debug("It's is {} turn to play", player.getPlayerName());

        Command command = null;
        while (command == null) {

            player.notifyTurnPlay();
            
            String commandString = player.actionMakePlay(sb, bb, maxBet);
            String[] commandFormatted = commandString.split(" ");

            command = Command.parseCommand(commandFormatted, player);
            command = command.validate(maxBet) ? command : null;
        }

        return command;
    }

    private void updateHandState() {

        Node iNode = _first;
        if(!iNode._player.isEliminated()) {
            _potManager.updatePlayerPot(iNode._player.getPlayerId(), iNode._player.placeOnBetMoney(), iNode._player.isFolded());
        }

        iNode = iNode._next;
        while(iNode != _first) {
            if(!iNode._player.isEliminated()){
                _potManager.updatePlayerPot(iNode._player.getPlayerId(), iNode._player.placeOnBetMoney(), iNode._player.isFolded());
            }

            iNode = iNode._next;
        }

        log.debug("All players pots updated!");
    }

    public void passTurn() {

        resetPlayers();

        Node newLast = !_first._player.isEliminated() ? _first : getNextNotEliminatedPlayer(_first);
        Node newFirst = getNextNotEliminatedPlayer(newLast);

        _first = newFirst;
        _last = newLast;
        assignRolesToAllPlayers();

        _potManager.restartPots();
    }

    public void calculatePrizeDistribution(final List<PlayerEvaluation> players) {

        List<PotDistribution> distribution = _potManager.calculatePrizeDistribution(players);
        for(PotDistribution dist : distribution){
            givePriceToPlayerWithID(dist.playerID(), dist.potPrize());
        }
    }

    public void calculatePrizeForPlayerLeft() {

        Node winner = ( _first._player.isFolded() && !_first._player.isEliminated() ) ? null : _first;
        Node iNode = _first._next;
        while (winner == null && iNode != _first) {
            winner = ( iNode._player.isFolded() && !iNode._player.isEliminated()) ? null : iNode;
            iNode = iNode._next;
        }

        PotDistribution distribution = _potManager.calculatePrizeForPlayer(winner._player.getPlayerId());
        givePriceToPlayerWithID(distribution.playerID(), distribution.potPrize());
        winner._player.setIsWinner(true);
    }

    public boolean checkEndOfGame() {

        int playersNotEliminated = _playerCounter;
        Node iNode = _first;
        playersNotEliminated = (iNode._player.isEliminated()) ? playersNotEliminated - 1 : playersNotEliminated;

        iNode = iNode._next;
        while(iNode != _first){
            playersNotEliminated = (iNode._player.isEliminated()) ? playersNotEliminated - 1 : playersNotEliminated;
            iNode = iNode._next;
        }

        return (playersNotEliminated == 1);
    }

    public void sendTableCardToAllPlayers(final Card card) {

        if(!_first._player.isEliminated())
            _first._player.notifyTableCard(card);

        Node i = _first._next;
        while (i != _first){
            if(!i._player.isEliminated())
                i._player.notifyTableCard(card);
            i = i._next;
        }
    }

    public void manageEliminatedPlayers() {

        Node iNode = _first;
        if(iNode._player.getMoneyOnBet() == 0 && iNode._player.getMoneyOffBet() == 0)
            iNode._player.setIsEliminated(true);

        iNode = iNode._next;
        while(iNode != _first) {
            if(iNode._player.getMoneyOnBet() == 0 && iNode._player.getMoneyOffBet() == 0)
                iNode._player.setIsEliminated(true);
        
            iNode = iNode._next;
        }
    }

    public void notifyRankingsToAllPlayers() {

        Node i = _first;
        if(i._player.isWinner())
            i._player.notifyHandWinner();
        else
            i._player.notifyHandLoser();

        i._player.notifyMoneyAmount( i._player.getMoneyOffBet() );
        i = i._next;
        
        while(i != _first) {

            if(i._player.isWinner())
                i._player.notifyHandWinner();
            else
                i._player.notifyHandLoser();

            i._player.notifyMoneyAmount( i._player.getMoneyOffBet() );
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

    public void notifyRoundEnded() {

        _first._player.notifyRoundEnded();
        Node iNode = _first._next;
        while (iNode != _first) {
            iNode._player.notifyRoundEnded();
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
        if(!pNode._player.isEliminated() && !_first._player.isFolded())
            info.add( new HandInfo(_first._player.getPlayerId(), _first._player.getPlayerCards()) );
        
        while (pNode != _first) {
            if(!pNode._player.isEliminated() && !pNode._player.isFolded())
                info.add( new HandInfo(pNode._player.getPlayerId(), pNode._player.getPlayerCards()) );
            
            pNode = pNode._next;
        }

        return info;
    }

    private boolean checkAllPlayersAllIn() {

        boolean allPlayersAllIn = true;

        Node iNode = _first;
        allPlayersAllIn &= iNode._player.isAllIn();

        iNode = iNode._next;
        while(allPlayersAllIn && iNode != _first) {
            allPlayersAllIn &= iNode._player.isAllIn();
            iNode = iNode._next;
        }

        return allPlayersAllIn;
    }

    private Node calculatePlayerOnTurn(final int numPlayers, final boolean isPreflop) {

        if(isPreflop) {

            if(numPlayers == 2) {
                return getPlayerByRole(PlayerRole.SMALL_BLIND);
            }
            else {
                Node dealer = _first;
                Node sb = getNextPlayerToPlay(dealer);
                Node bb = getNextPlayerToPlay(sb);
                return getNextPlayerToPlay(bb);
            }
        }
        else {

            Node sb = getPlayerByRole(PlayerRole.SMALL_BLIND);
            return !sb._player.isFolded() ? sb : getNextPlayerActive(sb);
        }
    }

    private Node calculatePivotPlayer(final int numPlayers, final boolean isPreflop) {

        if(numPlayers == 2)
            return (isPreflop) ? getNextPlayerToPlay(_first) : _first;
        
        
        Node dealer = _first;
        if(isPreflop) {
            Node sb = getNextPlayerToPlay(dealer);
            return getNextPlayerToPlay(sb);
        }
        else {
            return getNextPlayerToPlay(dealer);
        }

    }

    private void notifyWaitExceptTo(final Node playerOnTurn) {

        Node iNode = (_first != playerOnTurn) ? _first : _first._next;
        while (iNode != playerOnTurn) {
            iNode._player.notifyTurnWait();
            iNode = iNode._next;
        }
    }

    private void resetPlayers() {

        _first._player.retrieveCards();
        _first._player.unfoldPlayer();
        _first._player.setIsWinner(false);
        _first._player.setAllIn(false);

        Node current = _first._next;
        while (current != _first) {
            current._player.retrieveCards();
            current._player.unfoldPlayer();
            current._player.setIsWinner(false);
            current._player.setAllIn(false);

            current = current._next;
        }
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
            || _first._player.isEliminated()
            || _first._player.isAllIn() 
        ) ? 0 : 1;
        
        Node current = _first._next;
        while (current != _first) {
            cont += (
                current._player.isFolded() 
                || current._player.isEliminated() 
                || current._player.isAllIn()
            ) ? 0 : 1;
            current = current._next;
        }

        return cont;
    } 

    private Node getNextPlayerActive(Node current) {

        Node iNode = current._next;
        if(!iNode._player.isEliminated() && !iNode._player.isFolded())
            return iNode;


        boolean found = false;
        while (!found && iNode != current) {
            iNode = iNode._next;
            if(!iNode._player.isEliminated() && !iNode._player.isFolded())
                found = true;

        }

        return iNode;
    }

    private Node getNextPlayerToPlay(Node current) {

        Node iNode = current._next;
        if(!iNode._player.isEliminated() && !iNode._player.isFolded() && !iNode._player.isAllIn())
            return iNode;


        boolean found = false;
        while (!found && iNode != current) {
            iNode = iNode._next;
            if(!iNode._player.isEliminated() && !iNode._player.isFolded() && !iNode._player.isAllIn())
                found = true;
        
        }

        return iNode;
    }

    private Node getNextNotEliminatedPlayer(Node current) {

        Node iNode = current._next;
        if(!iNode._player.isEliminated())
            return iNode;


        boolean found = false;
        while (!found && iNode != current) {
            iNode = iNode._next;
            if(!iNode._player.isEliminated())
                found = true;
        }

        return iNode;
    }

    private Node getPlayerByRole(final PlayerRole role) {

        Node iNode = _first;
        if(iNode._player.getRole() == role)
            return iNode;


        Node pivot = iNode;
        boolean found = false;

        iNode = iNode._next;
        while (!found && iNode != pivot) {

            if(iNode._player.getRole() == role) {
                found = true;
            }
            else {
                iNode = iNode._next;
            }
        }

        return iNode;
    }

    private void givePriceToPlayerWithID(final int id, final int amount){

        Node winner = (_first._player.getPlayerId() == id && !_first._player.isEliminated()) ? _first : null;
        if(winner == null){
            
            Node i = _first._next;
            while(winner == null && i != _first){
                winner = (i._player.getPlayerId() == id && !i._player.isEliminated()) ? i : null;
                i = i._next;
            }
        }

        winner._player.receivePriceMoney(amount);
        winner._player.setIsWinner(true);
    }


    public void notifyEquityToPlayers(Map<Integer, Double> equityMap) {

    if (isEmpty()) return;

    Node current = _first;

    do {
        IPokerPlayer player = current._player;

         double equity;

        if (player.isFolded()) {
            equity = 0.0;
        } else {
            equity = equityMap.getOrDefault(player.getPlayerId(), 0.0);
        }
       
        current = current._next;

    } while (current != _first);
}


    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

}