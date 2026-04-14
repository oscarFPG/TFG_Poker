package com.ucm.server.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.commands.Command;
import com.ucm.common.GameType;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.PotDistribution;
import com.ucm.server.managers.BotManager;
import com.ucm.server.managers.PotManager;


public class PlayerList {

    private static final Logger log = LogManager.getLogger(PlayerList.class);

    private class Node {

        public IPokerPlayer _player;
        public Node _prev;
        public Node _next;
        public boolean _isDisconnected;

        public Node(Node prev, IPokerPlayer p, Node next) {
            _player = p;
            _prev = prev;
            _next = next;
            _isDisconnected = false;
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

    public void assignRolesToAllPlayers() throws CancelGameException {

        int numPlayers = activePlayersCounter();
        Node _current = _first;

        if (numPlayers == 0 || numPlayers == 1)
            throw new CancelGameException();


        List<PlayerRole> roles = PlayerRole.getRolesDistribution(numPlayers);
        if (numPlayers == 2) {

            try {
                _current._player.receiveRole(PlayerRole.SMALL_BLIND);
                _current._player.notifyPlayerRole(PlayerRole.SMALL_BLIND);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
            
            _current = getNextPlayerActive(_current);
            
            try {
                _current._player.receiveRole(PlayerRole.BIG_BLIND);
                _current._player.notifyPlayerRole(PlayerRole.BIG_BLIND);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
            
        }
        else {

            PlayerRole currentRole = roles.removeFirst();
            try {
                _current._player.receiveRole(currentRole);
                _current._player.notifyPlayerRole(currentRole);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.DEALER.name());
            }
            catch (IOException e) {
                if(checkIfGameCancel())
                    throw new CancelGameException();
            }   

            _current = getNextPlayerActive(_current);
            currentRole = roles.removeFirst();
            try {
                _current._player.receiveRole(currentRole);
                _current._player.notifyPlayerRole(currentRole);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());
            }
            catch (IOException e) {
                // TODO : Handle exception
            }

            _current = getNextPlayerActive(_current);
            currentRole = roles.removeFirst();
            try {
                _current._player.receiveRole(currentRole);
                _current._player.notifyPlayerRole(currentRole);
                log.debug("Player {} receives role {}", _current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());
            }
            catch (IOException e) {
               // TODO : Handle exception
            }  

            _current = getNextPlayerActive(_current);
            while (_current != _first ) {

                currentRole = roles.removeFirst();
                try {
                    _current._player.receiveRole(currentRole);
                    _current._player.notifyPlayerRole(currentRole);
                    log.debug("Player {} receives role {}", _current._player.getPlayerName(), currentRole.name());
                }
                catch (IOException e) {
                    // TODO : Handle exception
                }
                
                _current = getNextPlayerActive(_current);
            }
        }
    }

    public void shareOutAllCardsFromPlayer(Card c1, Card c2) throws CancelGameException{

        if (isEmpty())
            return;

        if (!_first._player.isEliminated() && _first._player.getCardsCounter() == 0) {

            try {
                _first._player.receiveCard(c1);
                _first._player.receiveCard(c2);

                _first._player.notifyPlayerCard(c1);
                _first._player.notifyPlayerCard(c2);
            }
            catch (IOException e) {
                // TODO : Handle exception
            }
            

            log.debug("Player {} receives the cards: {} {}", _first._player.getPlayerName(), c1.toString(), c2.toString());
            return;
        }

        Node index = _first._next;
        while (index != _first) {

            if (!index._player.isEliminated() && index._player.getCardsCounter() == 0) {
                
                try {
                    index._player.receiveCard(c1);
                    index._player.receiveCard(c2);
                    
                    index._player.notifyPlayerCard(c1);
                    index._player.notifyPlayerCard(c2);
                }
                catch (IOException e) {
                    // TODO : Handle exception
                }

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
        try {

            pNode._player.actionSmallBlindBet(sb);
            pNode._player.notifySmallBlindBet(sb);
            notifyOtherPlayerActionToAllPlayers(pNode._player);
            log.debug(
                "Player {} puts {}$ as SMALL_BLIND. Now it has {}$", 
                pNode._player.getPlayerName(), pNode._player.getMoneyOnBet(), pNode._player.getMoneyOffBet()
            );
        }
        catch(IOException e) {
            // TODO : Handle exception
        }
        

        pNode = pNode._next;
        try {

            pNode._player.actionBigBlindBet(bb);
            pNode._player.notifyBigBlindBet(bb);
            notifyOtherPlayerActionToAllPlayers(pNode._player);
            log.debug(
                "Player {} puts {}$ as BIG_BLIND. Now it has {}$", 
                pNode._player.getPlayerName(), pNode._player.getMoneyOnBet(), pNode._player.getMoneyOffBet()
            );
        }
        catch(IOException e) {
            // TODO : Handle exception
        }
        

    }

    public void playHand(final int sb, final int bb, final boolean isPreflop) throws OnlyOnePlayerLeftException, CancelGameException {

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
            if(command == null) {

                log.debug("Something happened with player {} : Disconnecting player and making FOLD instead", playerOnTurn._player.getPlayerName());
                playerOnTurn._isDisconnected = true;
                if(checkIfGameCancel()) {
                    throw new CancelGameException();
                }
                else {
                    command = Command.parseCommand(new String[] {GameType.FOLD_ACTION_FULL}, playerOnTurn._player);
                }
            }

            CommandResult result = command.execute(sb, bb, maxBet);
            notifyOtherPlayerActionToAllPlayers(playerOnTurn._player);

            if(result.folds()) {
                --playersRemaining; 
                if (playersRemaining == 1) {
                    updateHandState();
                    notifyHandEndsByFold();
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
        try {

            while (command == null) {

                player.notifyTurnPlay();
                
                String commandString = player.actionMakePlay(sb, bb, maxBet);
                String[] commandFormatted = commandString.split(" ");

                log.debug("Player {} with command: {}", player.getPlayerName(), commandString);

                command = Command.parseCommand(commandFormatted, player);
                command = command.validate(maxBet) ? command : null;
            }
        }
        catch (IOException e) {
            log.error("Error happened waiting for player {} : {}", player.getPlayerName(), e.getMessage());
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

    public void passTurn() throws CancelGameException {

        resetPlayers();

        Node newLast = !_first._player.isEliminated() ? _first : getNextNotEliminatedPlayer(_first);
        Node newFirst = getNextNotEliminatedPlayer(newLast);

        _first = newFirst;
        _last = newLast;
        assignRolesToAllPlayers();

        _potManager.restartPots();
    }

    public void calculatePrizeDistribution(final List<PlayerEvaluation> players) throws CancelGameException {

        List<PotDistribution> distribution = _potManager.calculatePrizeDistribution(players);
        for(PotDistribution dist : distribution){
            givePriceToPlayerWithID(dist.playerID(), dist.potPrize());
        }

        notifyRankingsToAllPlayers();
    }

    public void calculatePrizeForPlayerLeft() throws CancelGameException {

        Node winner = ( _first._player.isFolded() && !_first._player.isEliminated() ) ? null : _first;
        Node iNode = _first._next;
        while (winner == null && iNode != _first) {
            winner = ( iNode._player.isFolded() && !iNode._player.isEliminated()) ? null : iNode;
            iNode = iNode._next;
        }

        PotDistribution distribution = _potManager.calculatePrizeForPlayer(winner._player.getPlayerId());
        givePriceToPlayerWithID(distribution.playerID(), distribution.potPrize());
        winner._player.setIsWinner(true);

        notifyRankingsToAllPlayers();
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

    public void sendTableCardToAllPlayers(final Card card) throws CancelGameException {

        if(!_first._player.isEliminated()) {

            try {
                _first._player.notifyTableCard(card);
            }
            catch (IOException e) {
                // TODO : Handle exception
            }
        }
            
        Node i = _first._next;
        while (i != _first) {

            if(!i._player.isEliminated()) {
                try {
                    i._player.notifyTableCard(card);
                }
                catch (IOException e) {
                    // TODO : Handle exception
                }
            }
                
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
            return !sb._player.isFolded() && !sb._isDisconnected ? sb : getNextPlayerActive(sb);
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

            try {
                iNode._player.notifyTurnWait();
            }
            catch(IOException e) {
                // TODO : Handle exception
            }
            
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
            || _first._isDisconnected
        ) ? 0 : 1;
        
        Node current = _first._next;
        while (current != _first) {
            cont += (
                current._player.isFolded() 
                || current._player.isEliminated() 
                || current._player.isAllIn()
                || current._isDisconnected
            ) ? 0 : 1;
            current = current._next;
        }

        return cont;
    } 

    private Node getNextPlayerActive(Node current) {

        Node iNode = current._next;
        if(!iNode._player.isEliminated() && !iNode._player.isFolded() && !iNode._isDisconnected)
            return iNode;


        boolean found = false;
        while (!found && iNode != current) {
            iNode = iNode._next;
            found = (!iNode._player.isEliminated() && !iNode._player.isFolded() && !iNode._isDisconnected);
        }

        return iNode;
    }

    private Node getNextPlayerToPlay(Node current) {

        Node iNode = current._next;
        if(!iNode._player.isEliminated() && !iNode._player.isFolded() && !iNode._player.isAllIn() && !iNode._isDisconnected)
            return iNode;


        boolean found = false;
        while (!found && iNode != current) {
            iNode = iNode._next;
            if(!iNode._player.isEliminated() && !iNode._player.isFolded() && !iNode._player.isAllIn() && !iNode._isDisconnected)
                found = true;
        
        }

        return iNode;
    }

    private Node getNextNotEliminatedPlayer(Node current) {

        Node iNode = current._next;
        if(!iNode._player.isEliminated() && !iNode._isDisconnected)
            return iNode;


        boolean found = false;
        while (!found && iNode != current) {
            iNode = iNode._next;
            if(!iNode._player.isEliminated() && !iNode._isDisconnected)
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

    private void notifyOtherPlayerActionToAllPlayers(IPokerPlayer p) {

        Node iNode = _first;
        if(!iNode._player.isEliminated() && iNode._player != p) {
            
            try {
                iNode._player.notifyOtherPlayerAction(p);
            }
            catch(IOException e) {
                // TODO : Handle exception
            }
        }

        iNode = iNode._next;
        while(iNode != _first) {

            if(!iNode._player.isEliminated() && iNode._player != p) {

                try {
                    iNode._player.notifyOtherPlayerAction(p);
                }
                catch(IOException e) {
                    // TODO : Handle exception
                }
            }

            iNode = iNode._next;
        }
    }

    private void notifyRankingsToAllPlayers() {

        Node i = _first;
        try {
            if(i._player.isWinner())
                i._player.notifyHandWinner();
            else
                i._player.notifyHandLoser();

            i._player.notifyMoneyAmount( i._player.getMoneyOffBet() );
        }
        catch(IOException e) {
            // TODO : Handle exception
        }        

        i = i._next;        
        while(i != _first) {

            try {
                if(i._player.isWinner())
                    i._player.notifyHandWinner();
                else
                    i._player.notifyHandLoser();

                i._player.notifyMoneyAmount( i._player.getMoneyOffBet() );
            }
            catch(IOException e) {
                // TODO : Handle exception
            }

            i = i._next;
        }

    }

    private void notifyHandEndsByFold() {
        
        Node iNode = _first;

        try {
            iNode._player.notifyHandEndsByFolds();
        }
        catch(IOException e) {
            // TODO : Handle exception
        }
        
        iNode = iNode._next;
        while(iNode != _first) {
            
            try {
                iNode._player.notifyHandEndsByFolds();
            }
            catch(IOException e) {
                // TODO : Handle exception
            }

            iNode = iNode._next;
        }
    }

    private void notifyRoundEnded() {

        Node iNode = _first;

        try {
            iNode._player.notifyRoundEnded();
        }
        catch(IOException e) {
            // TODO : Handle exception
        }

        iNode = iNode._next;
        while (iNode != _first) {
            
            try {
                iNode._player.notifyRoundEnded();
            }
            catch(IOException e) {
                // TODO : Handle exception
            }

            iNode = iNode._next;
        }
    }

    public void notifyGameEnds(final boolean gameEnds) throws CancelGameException {

        Node iNode = _first;
        try {
            if(gameEnds)
                iNode._player.notifyGameEnded();
            else
                iNode._player.notifyGameKeeps();
        }
        catch(IOException e) {
            // TODO : Handle exception
        }

        iNode = iNode._next;
        while(iNode != _first) {
            
            try {
                if(gameEnds)
                    iNode._player.notifyGameEnded();
                else
                    iNode._player.notifyGameKeeps();
            }
            catch(IOException e) {
                // TODO : Handle exception
            }

            iNode = iNode._next;
        }
    }

    private boolean checkIfGameCancel() {

        int connectedPlayers = 0;
        Node iNode = _first;
        if(!iNode._isDisconnected)
            ++connectedPlayers;

        iNode = iNode._next;
        while(iNode != _first) {
            if(!iNode._isDisconnected)
                ++connectedPlayers;

            iNode = iNode._next;
        }
        
        return (connectedPlayers <= 1);
    }


    public void notifyEquityToPlayers(Map<Integer, Double> equityMap) {

    if (isEmpty()) return;

    Node current = _first;

    do {
        IPokerPlayer player = current._player;

        if (!player.isEliminated()) {

            double equity;

            if (player.isFolded()) {
                equity = 0.0;
            } else {
                equity = equityMap.getOrDefault(player.getPlayerId(), 0.0);
            }

            player.notifyEquity(equity);
        }

        current = current._next;

    } while (current != _first);
}

    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

}