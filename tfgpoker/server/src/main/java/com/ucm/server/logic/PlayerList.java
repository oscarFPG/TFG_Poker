package com.ucm.server.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.commands.Command;
import com.ucm.server.exceptions.TurnTimeoutException;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.managers.PotManager;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.PotDistribution;
import com.ucm.server.middleclasses.Spectator;


public class PlayerList implements Iterable<Node> {

    private static final Logger log = LogManager.getLogger(PlayerList.class);

    private Node _first;
    private Node _last;
    private int _playerCounter;
    private int _maxNumberOfPlayers;

    private int _totalPot;
    private PotManager _potManager;
    private Node _host;
    private Spectator _spectator;


    public PlayerList(int n) {
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = n;

        _totalPot = 0;
        _potManager = new PotManager(n);
        _host = null;
        _spectator = null;
    }

    
    public void addPlayer(Player p) {

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

    public void addSpectator(Spectator p) {
        _spectator = p;
    }

    public void assignHost(Player p) {
        _host = new Node(null, p,null);
    }


    public void assignRolesToAllPlayers() throws CancelGameException {

        int numPlayers = activePlayersCounter();

        // Cannot be possible
        if (numPlayers == 0 || numPlayers == 1)
            throw new CancelGameException();

        // Only SB and BB
        if (numPlayers == 2) {

            Node current = _first;
            try {

                // Give SB role to first player
                current._player.receiveRole(PlayerRole.SMALL_BLIND);
                if(_spectator != null)
                    _spectator.notifyOtherPlayerState(current._player);
                log.debug("Player {} receives role {}", current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());
            
                current = getNextPlayerActive(current);

                // Give BB role to first player
                current._player.receiveRole(PlayerRole.BIG_BLIND);
                if(_spectator != null)
                    _spectator.notifyOtherPlayerState(current._player);
                log.debug("Player {} receives role {}", current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        
            notifyPlayerStateToAllPlayers();
            return;
        }

        
        List<PlayerRole> roles = PlayerRole.getRolesDistribution(numPlayers);
        PlayerRole currentRole = null;
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated() && !player._player.isFolded() && !player._isDisconnected){

                try {
                    currentRole = roles.removeFirst();
                    player._player.receiveRole(currentRole);
                    if(_spectator != null)
                        _spectator.notifyOtherPlayerState(player._player);

                    log.debug("Player {} receives role {}", player._player.getPlayerName(), currentRole.name());
                }
                catch (IOException e) {

                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

        notifyPlayerStateToAllPlayers();
    }

    public void shareOutCardsToSomePlayer(Card c1, Card c2) throws CancelGameException {

        if (isEmpty())
            return;


        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated() && player._player.getCardsCounter() == 0) {

                try {
                    player._player.receiveCard(c1);
                    player._player.receiveCard(c2);

                    log.debug("Player {} receives the cards: {} {}", player._player.getPlayerName(), c1.toString(), c2.toString());
                    return;
                }
                catch (Exception e) {
                    
                    player._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

    }

    private void smallBlindAndBigBlindPlays(final int sb, final int bb, final int playersRemaining) throws CancelGameException {

        // Select as small blind:
        // 1. First player if there is only two players -> playsToMake == 1
        // 2. Next player from first if there is more than two players -> playsToMake > 1
        Node pNode = (playersRemaining == 2) ? _first : _first._next;
        try {

            pNode._player.putSmallBlindBet(sb);
            notifyOtherPlayerActionToAllPlayers(pNode._player);
            if(_spectator != null)
                _spectator.notifyOtherPlayerState(pNode._player);

            log.debug(
                "Player {} puts {}$ as SMALL_BLIND. Now it has {}$", 
                pNode._player.getPlayerName(), pNode._player.getMoneyOnBet(), pNode._player.getMoneyOffBet()
            );
        }
        catch(Exception e) {
            
            pNode._isDisconnected = true;
            if( checkIfGameCancel() )
                throw new CancelGameException();
        }
        
        pNode = getNextPlayerActive(pNode);

        try {

            pNode._player.putBigBlindBet(bb);
            notifyOtherPlayerActionToAllPlayers(pNode._player);
            if(_spectator != null)
                _spectator.notifyOtherPlayerState(pNode._player);

            log.debug(
                "Player {} puts {}$ as BIG_BLIND. Now it has {}$", 
                pNode._player.getPlayerName(), pNode._player.getMoneyOnBet(), pNode._player.getMoneyOffBet()
            );
        }
        catch(Exception e) {
            
            pNode._isDisconnected = true;
            if( checkIfGameCancel() )
                throw new CancelGameException();
        }
        
        int totalPot = calculateTotalPot();
        notifyTotalPotToAllPlayers(totalPot);
        if(_spectator != null)
            _spectator.notifyTotalPot(totalPot);
    }

    public void playHand(final int sb, final int bb, final boolean isPreflop) throws OnlyOnePlayerLeftException, CancelGameException {

        // Avoid asking if all active players have used all their money
        if( checkAllPlayersAllIn() ) {
            log.debug("All players are ALL_IN, skipping betting round");
            notifyRoundEnded();
            return;
        }


        int playersRemaining = activePlayersCounter();        
        Node playerOnTurn = calculatePlayerOnTurn(playersRemaining, isPreflop);
        Node pivotPlayer = calculatePivotPlayer(playersRemaining, isPreflop, playerOnTurn);
        int maxBet = (isPreflop) ? bb : 0;
        int currentBet = maxBet;
        int minRaise = (isPreflop) ? bb - sb : 0;
        int totalPot = 0;

        // In case only one player can play -> Cannot play alone
        // E.g: All players all-in except one -> Skip rounds
        if(playersRemaining == 1) { 
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

            // Notify all about the player state
            notifyTurnPlayer(playerOnTurn._player);

            // Ask player to play and check response time
            long start = System.nanoTime();
            Command command = askCommandToPlayer(playerOnTurn, sb, bb, maxBet);
            long end = System.nanoTime();

            // Annotate response time
            playerOnTurn._player.getExperimentData().setDecisionTime((end - start) / 1_000_000);
            
            // Execute player action
            CommandResult result = command.execute(sb, bb, maxBet);
            
            // Notify new player state
            notifyPlayerOwnState(playerOnTurn);
            notifyOtherPlayerActionToAllPlayers(playerOnTurn._player);
            
            // Update and notify about the current total pot
            totalPot = calculateTotalPot() + _totalPot;
            notifyTotalPotToAllPlayers(totalPot);

            if( result.folds() ) {

                --playersRemaining;
                pivotPlayer = getNextPlayerActive(playerOnTurn);
                
                if (playersRemaining == 1) {
                    _totalPot = totalPot;
                    updateHandState();
                    notifyHandEndsByFold();
                    log.debug("Interrupting the round: only one player left!");
                    throw new OnlyOnePlayerLeftException();
                }
            }

            currentBet = result.folds() ? 0 : result.bet();
            minRaise = Math.max(minRaise, result.bet() - maxBet);
            maxBet = Math.max(maxBet, currentBet);

            pivotPlayer = result.raises() ? playerOnTurn : pivotPlayer;
            playerOnTurn = getNextPlayerActive(playerOnTurn);
        }
        while(pivotPlayer != playerOnTurn && playersRemaining != 0);

        _totalPot = totalPot;
        updateHandState();
        notifyRoundEnded();
    }

    private Command askCommandToPlayer(Node node, final int sb, final int bb, final int maxBet) throws CancelGameException {

        Player player = node._player;
        Command command = null;

        try {

            node._player.notifyTurnPlay();
            log.debug("It's is {} turn to play", player.getPlayerName());
            
            while (command == null) {
         
                String commandString = player.makePlay(sb, bb, maxBet);
                String[] commandFormatted = commandString.split(" ");

                command = Command.parseCommand(commandFormatted, player);
            }
        }
        catch (TurnTimeoutException e) {

            log.warn("Player {} TIMEOUT -> auto FOLD", player.getPlayerName());
            log.warn("Timer has ended, player {} make FOLD!", player.getPlayerName());
            command = Command.parseCommand(new String[] {GameType.FOLD_ACTION_FULL}, player);
        }
        catch (IOException e) {

            log.error("Error happened waiting for player {} : {}", player.getPlayerName(), e.getMessage());
            node._isDisconnected = true;

            if( checkIfGameCancel() )
                throw new CancelGameException();
            else
                command = Command.parseCommand(new String[] {GameType.FOLD_ACTION_FULL}, player);
        }

        return command;
    }

    private void updateHandState() {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated()) {
                _potManager.updatePlayerPot(player._player.getPlayerId(), player._player.placeOnBetMoney(), player._player.isFolded());
            }
        }

        log.debug("All players pots updated!");
    }

    public void passTurn() throws CancelGameException {

        // Restart previous bets from players
        resetPlayers();

        // Rotate first and last players -> Waiting to assing roles to them
        Node newLast = !_first._player.isEliminated() ? _first : getNextNotEliminatedPlayer(_first);
        Node newFirst = getNextNotEliminatedPlayer(newLast);
        _first = newFirst;
        _last = newLast;
        
        // Restart every pot made by players
        _potManager.restartPots();

        // Restart round status
        _totalPot = 0;
    }

    public void calculatePrizeDistribution(final List<PlayerEvaluation> players) throws CancelGameException {

        List<PotDistribution> distribution = _potManager.calculatePrizeDistribution(players);
        for(PotDistribution dist : distribution) {
            givePriceToPlayerWithID(dist.playerID(), dist.potPrize());
        }

        notifyPlayerStateToAllPlayers();
    }

    public void calculatePrizeForPlayerLeft() throws CancelGameException {

        Iterator<Node> it = iterator();
        Node winner = null;
        while( it.hasNext() && winner == null ) {

            Node player = it.next();
            if(!player._player.isFolded() && !player._player.isEliminated()) {
                winner = player;
            }
        }

        PotDistribution distribution = _potManager.calculatePrizeForPlayer(winner._player.getPlayerId());
        givePriceToPlayerWithID(distribution.playerID(), distribution.potPrize());

        notifyPlayerStateToAllPlayers();
    }

    public boolean checkEndOfGame() {

        int playersNotEliminated = _playerCounter;

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player._player.isEliminated()) {
                --playersNotEliminated;
            }
        }

        return (playersNotEliminated == 1);
    }

    public void sendTableCardToAllPlayers(final Card card) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated()) {

                try {
                    player._player.receiveTableCard(card);
                }
                catch (Exception e) {
                    
                    player._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

        if(_spectator != null)
            _spectator.notifyTableCard(card);

    }

    public void manageEliminatedPlayers() {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player._player.getMoneyOnBet() == 0 && player._player.getMoneyOffBet() == 0)
                player._player.eliminate();
        }
    }

    public void broadcastAllPlayerCards() throws CancelGameException {

        // Send the player cards to everyone just to display -> Only if the player has not folded or it is eliminated
        Iterator<Node> modelIT = iterator();
        while( modelIT.hasNext() ) {

            Node modelPlayer = modelIT.next();
            if(modelPlayer._player.isEliminated() || modelPlayer._player.isFolded())
                continue;


            // Send cards from modelPlayer to spectator
            if(_spectator != null)
                _spectator.notifyOtherPlayerCards(modelPlayer._player);

            // Send cards from modelPlayer to every player
            Iterator<Node> receiverIT = iterator();
            while( receiverIT.hasNext() ) {

                Node receiver = receiverIT.next();
                try {
                    receiver._player.notifyOtherPlayerCards(modelPlayer._player);
                }
                catch (IOException e) {
                    
                    receiver._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
                
            }

        }
        
    }

    public List<HandInfo> getPlayerHandsInfo() {

        // Generate struct <playerID, cards> for every player to select the winner(s)
        List<HandInfo> info = new ArrayList<>(_playerCounter);
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated() && !player._player.isFolded())
                info.add( new HandInfo(player._player.getPlayerId(), player._player.getPlayerCards()) );
        }

        return info;
    }

    private int calculateTotalPot() {

        int total = 0;
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated()) {
                total += player._player.getMoneyOnBet();
            }
        }

        return total;
    }

    private boolean checkAllPlayersAllIn() {

        boolean allPlayersAllIn = true;
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            allPlayersAllIn &= player._player.isAllIn();
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
                Node sb = getNextPlayerActive(dealer);
                Node bb = getNextPlayerActive(sb);
                return getNextPlayerActive(bb);
            }
        }
        else {

            Node sb = getPlayerByRole(PlayerRole.SMALL_BLIND);
            return !sb._player.isFolded() && !sb._isDisconnected ? sb : getNextPlayerActive(sb);
        }
    }

    private Node calculatePivotPlayer(final int numPlayers, final boolean isPreflop, Node current) {

        if(numPlayers == 2)
            return (isPreflop) ? getNextPlayerActive(current) : current;
        
        
        Node dealer = _first;
        if(isPreflop) {
            Node sb = getNextPlayerActive(dealer);
            return getNextPlayerActive(sb);
        }
        else {
            return getNextPlayerActive(dealer);
        }

    }

    private void givePriceToPlayerWithID(final int id, final int amount) {

        Iterator<Node> it = iterator();
        Node winner = null;
        boolean found = false;
        while( !found && it.hasNext() ) {

            winner = it.next();
            if(winner._player.getPlayerId() == id) {
                found = true;
            }
        }

        winner._player.receivePriceMoney(amount);
        winner._player.wins();
        PokerHistory history = PokerHistory.current();
        if (history != null) {
            history.winner(winner._player, amount);
        }
        log.debug("Player {} receives {}$ as prize! It has now {}$", winner._player.getPlayerName(), amount, winner._player.getMoneyOffBet());
    }

    private void resetPlayers() {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            player._player.retrieveCards();
            player._player.resetStates();
        }
    }

    private int activePlayersCounter() {

        if (isEmpty())
            return 0;


        Iterator<Node> it = iterator();
        int contador = 0;
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isFolded() && !player._player.isEliminated() && !player._player.isAllIn() && !player._isDisconnected) {
                ++contador;
            }
        }

        return contador;
    } 


    private void notifyWaitExceptTo(final Node playerOnTurn) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if( player.equals(playerOnTurn) )
                continue;

            try {
                player._player.notifyTurnWait();
            }
            catch(Exception e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }
        
    }

    private void notifyTurnPlayer(Player p) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player._isDisconnected || player._player == p)
                continue;


            try {
                player._player.notifyCurrentTurnPlayer(p);
            }
            catch(Exception e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

        if(_spectator != null)
            _spectator.notifyTurnPlayer(p);
    }

    private void notifyPlayerOwnState(Node player) throws CancelGameException {

        try {
            player._player.notifyOwnState();
        }
        catch(Exception e) {
            
            player._isDisconnected = true;
            if( checkIfGameCancel() )
                throw new CancelGameException();
        }
    }

    private void notifyOtherPlayerActionToAllPlayers(Player p) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player._isDisconnected || player._player == p)
                continue;


            try {
                player._player.notifyOtherPlayerAction(p);
            }
            catch(Exception e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

        if(_spectator != null)
            _spectator.notifyOtherPlayerAction(p);
    }

    private void notifyTotalPotToAllPlayers(final int totalPot) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            try {
                player._player.notifyTotalPot(totalPot);
            }
            catch(Exception e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

        if(_spectator != null)
            _spectator.notifyTotalPot(totalPot);
    }

    private void notifyHandEndsByFold() throws CancelGameException {
        
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            try {
                player._player.notifyHandEndsByFolds();
            }
            catch(Exception e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

        if(_spectator != null)
            _spectator.notifyHandEndsByFolds();
    }

    private void notifyRoundEnded() throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            try {
                player._player.notifyRoundEnded();
            }
            catch(Exception e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

        if(_spectator != null)
            _spectator.notifyRoundEnded();
    }

    public void notifyGameEnds(final boolean gameEnds) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._isDisconnected) {

                try {
                    if(gameEnds)
                        player._player.notifyGameEnded();
                    else
                        player._player.notifyGameKeeps();
                }
                catch(Exception e) {
                    player._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

        if(_spectator != null) {
            if(gameEnds)
                _spectator.notifyGameEnded();
            else
                _spectator.notifyGameKeeps();
        }
    }

    private void notifyPlayerStateToAllPlayers() throws CancelGameException {

        Iterator<Node> targetIt = iterator();
        while( targetIt.hasNext() ) {

            Node receiverPlayer = targetIt.next();
            if( !receiverPlayer._isDisconnected && !receiverPlayer._player.isEliminated() ) {

                try {
                    receiverPlayer._player.notifyOwnState();
                }
                catch(Exception e) {

                    receiverPlayer._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }

                Iterator<Node> it = iterator();
                while( it.hasNext() ) {

                    Node player = it.next();
                    if( player.equals(receiverPlayer) )
                        continue;
                
                    try {
                        receiverPlayer._player.notifyOtherPlayerState(player._player);
                    }
                    catch(Exception e) {
                        
                        receiverPlayer._isDisconnected = true;
                        if( checkIfGameCancel() )
                            throw new CancelGameException();
                    }
                }

                try {
                    receiverPlayer._player.notifyEndPlayerState();
                }
                catch(Exception e) {
                    receiverPlayer._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }
    }

    public void notifyEquityToPlayers(Map<Integer, Double> equityMap) throws CancelGameException {

        if (isEmpty())
            return;


        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node node = it.next();
            Player player = node._player;
            if (!player.isEliminated()) {

                double equity;
                if (player.isFolded()) {
                    equity = 0.0;
                }
                else {
                    equity = equityMap.getOrDefault(player.getPlayerId(), 0.0);
                }

                try {
                    player.notifyEquity(equity);
                }
                catch(Exception e) {
                    
                    node._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

    }


    private boolean checkIfGameCancel() {

        if(_host != null && _host._isDisconnected)
            return true; 


        int connectedPlayers = 0;
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            connectedPlayers += (player._isDisconnected) ? 1 : 0;
        }

        return (connectedPlayers <= 1);
    }

    private Node getNextPlayerActive(Node current) {

        Iterator<Node> it = iterator(current._next);
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated() && !player._player.isFolded() && !player._isDisconnected)
                return player;
        }

        return current;
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

        Iterator<Node> it = iterator();
        Node player = null;
        boolean found = false;
        while( !found && it.hasNext() ) {

            player = it.next();
            if(player._player.getRole() == role) {
                found = true;
            }
        }

        return player;
    }


    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }


    // Iterator
    private class PlayerIterator implements Iterator<Node> {

        private Node pivot = _first;
        private Node current = pivot;
        private boolean start = true;


        public PlayerIterator() {}
        public PlayerIterator(Node start) {
            pivot = start;
            current = pivot;
        }

        @Override
        public boolean hasNext() {
            return start ? true : (current != pivot);
        }

        @Override
        public Node next() {
            
            if(!hasNext())
                return null;


            Node iNode = current;
            current = current._next;
            start = false;

            return iNode;
        }

    }

    public Iterator<Node> iterator() {
        return new PlayerIterator();
    }

    public Iterator<Node> iterator(Node start) {
        return new PlayerIterator(start);
    }


    public List<Player> getPlayers() {

        List<Player> players = new ArrayList<>();

        Iterator<Node> it = iterator();
        while (it.hasNext()) {
            players.add(it.next()._player);
        }

        return players;
    }

    public int getTotalPot() {
        return _totalPot;
    }
}