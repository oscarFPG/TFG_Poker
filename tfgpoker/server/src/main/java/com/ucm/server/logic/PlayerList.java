package com.ucm.server.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.PotDistribution;
import com.ucm.server.managers.PotManager;


public class PlayerList implements Iterable<Node> {

    private static final Logger log = LogManager.getLogger(PlayerList.class);

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

    public void assignRolesToAllPlayers() throws CancelGameException {

        int numPlayers = activePlayersCounter();

        if (numPlayers == 0 || numPlayers == 1)
            throw new CancelGameException();


        List<PlayerRole> roles = PlayerRole.getRolesDistribution(numPlayers);
        if (numPlayers == 2) {

            Node current = _first;
            try {
                current._player.receiveRole(PlayerRole.SMALL_BLIND);
                current._player.notifyPlayerRole(PlayerRole.SMALL_BLIND);
                log.debug("Player {} receives role {}", current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
            
            current = getNextPlayerActive(current);
            
            try {
                current._player.receiveRole(PlayerRole.BIG_BLIND);
                current._player.notifyPlayerRole(PlayerRole.BIG_BLIND);
                log.debug("Player {} receives role {}", current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
            
        }
        else {

            PlayerRole currentRole = null;
            Iterator<Node> it = iterator();
            while( it.hasNext() ) {

                Node player = it.next();
                if(!player._player.isEliminated() && !player._player.isFolded() && !player._isDisconnected){

                    try {
                        currentRole = roles.removeFirst();
                        player._player.receiveRole(currentRole);
                        player._player.notifyPlayerRole(currentRole);
                        log.debug("Player {} receives role {}", player._player.getPlayerName(), currentRole.name());
                    }
                    catch (IOException e) {
                        if(checkIfGameCancel())
                            throw new CancelGameException();
                    }
                }
            }
        }

    }

    public void shareOutAllCardsFromPlayer(Card c1, Card c2) throws CancelGameException {

        if (isEmpty())
            return;


        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated() && player._player.getCardsCounter() == 0) {

                try {
                    player._player.receiveCard(c1);
                    player._player.receiveCard(c2);

                    player._player.notifyPlayerCard(c1);
                    player._player.notifyPlayerCard(c2);

                    log.debug("Player {} receives the cards: {} {}", player._player.getPlayerName(), c1.toString(), c2.toString());
                }
                catch (IOException e) {
                    
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

            pNode._player.actionSmallBlindBet(sb);
            pNode._player.notifySmallBlindBet(sb);
            notifyOtherPlayerActionToAllPlayers(pNode._player);
            log.debug(
                "Player {} puts {}$ as SMALL_BLIND. Now it has {}$", 
                pNode._player.getPlayerName(), pNode._player.getMoneyOnBet(), pNode._player.getMoneyOffBet()
            );
        }
        catch(IOException e) {
            
            pNode._isDisconnected = true;
            if( checkIfGameCancel() )
                throw new CancelGameException();
        }
        
        pNode = getNextPlayerActive(pNode);

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
            
            pNode._isDisconnected = true;
            if( checkIfGameCancel() )
                throw new CancelGameException();
        }
        
        int totalPot = calculateTotalPot();
        notifyTotalPotToAllPlayers(totalPot);
    }

    public void playHand(final int sb, final int bb, final boolean isPreflop) throws OnlyOnePlayerLeftException, CancelGameException {

        if( checkAllPlayersAllIn() ) { // Avoid asking if all active players have used all their money
            log.debug("All players are ALL_IN, skipping betting round");
            notifyGameStateToAllPlayers();
            notifyRoundEnded();
            return;
        }


        int playersRemaining = activePlayersCounter();        
        Node playerOnTurn = calculatePlayerOnTurn(playersRemaining, isPreflop);
        Node pivotPlayer = calculatePivotPlayer(playersRemaining, isPreflop);
        int maxBet = (isPreflop) ? bb : 0;
        int currentBet = maxBet;
        int totalPot = 0;


        if(playersRemaining == 1) { // In case only one player can play -> Cannot play alone
            notifyRoundEnded();
            return;
        }


        notifyGameStateToAllPlayers();
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


            Command command = askCommandToPlayer(playerOnTurn, sb, bb, maxBet);
            CommandResult result = command.execute(sb, bb, maxBet);
            notifyOtherPlayerActionToAllPlayers(playerOnTurn._player);

            totalPot = calculateTotalPot();
            notifyTotalPotToAllPlayers(totalPot);

            if( result.folds() ) {
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

            log.debug("Total pot is {}", totalPot);
            log.debug("Current bet is {}", currentBet);
            log.debug("Maximum bet is {}", maxBet);
        }
        while( pivotPlayer != playerOnTurn && playersRemaining != 0);

        updateHandState();
        notifyRoundEnded();
    }

    private Command askCommandToPlayer(Node node, final int sb, final int bb, final int maxBet) throws CancelGameException {

        IPokerPlayer player = node._player;
        Command command = null;
        try {

            log.debug("It's is {} turn to play", player.getPlayerName());
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
        List<IPokerPlayer> winners = new ArrayList<>();
        for(PotDistribution dist : distribution){
            givePriceToPlayerWithID(dist.playerID(), dist.potPrize());
        }

        notifyGameStateToAllPlayers();
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
        winner._player.setIsWinner(true);

        notifyGameStateToAllPlayers();
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
                    player._player.notifyTableCard(card);
                }
                catch (IOException e) {
                    
                    player._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

    }

    public void manageEliminatedPlayers() {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player._player.getMoneyOnBet() == 0 && player._player.getMoneyOffBet() == 0)
                player._player.setIsEliminated(true);

        }

    }

    public List<HandInfo> getPlayerHandsInfo() {

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

    private void resetPlayers() {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            player._player.retrieveCards();
            player._player.unfoldPlayer();
            player._player.setIsWinner(false);
            player._player.setAllIn(false);
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
        winner._player.setIsWinner(true);    
    }

    private void notifyWaitExceptTo(final Node playerOnTurn) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player == playerOnTurn)
                continue;

            try {
                player._player.notifyTurnWait();
            }
            catch(IOException e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }
        
    }

    private void notifyOtherPlayerActionToAllPlayers(IPokerPlayer p) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player._isDisconnected || player._player.isEliminated())
                continue;


            try {
                player._player.notifyOtherPlayerAction(p);
            }
            catch(IOException e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

    }

    private void notifyTotalPotToAllPlayers(final int totalPot) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            try {
                player._player.notifyTotalPot(totalPot);
            }
            catch(IOException e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

    }

    private void notifyHandEndsByFold() throws CancelGameException {
        
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            try {
                player._player.notifyHandEndsByFolds();
            }
            catch(IOException e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

    }

    private void notifyRoundEnded() throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            try {
                player._player.notifyRoundEnded();
            }
            catch(IOException e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

    }

    private void notifyGameStateToAllPlayers() throws CancelGameException {

        Iterator<Node> targetIt = iterator();
        while( targetIt.hasNext() ) {

            Node targetPlayer = targetIt.next();
            if(targetPlayer._isDisconnected || targetPlayer._player.isFolded() || targetPlayer._player.isEliminated())
                continue;

            Iterator<Node> infoIt = iterator();
            while( infoIt.hasNext() ) {

                Node infoPlayer = infoIt.next();
                boolean isLast = (infoPlayer._next == _first);
                try {
                    targetPlayer._player.notifyPlayerState(infoPlayer._player, isLast);
                }
                catch (IOException e) {
                    
                    targetPlayer._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

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
                catch(IOException e) {

                    player._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

    }

    public void notifyEquityToPlayers(Map<Integer, Double> equityMap) {

        if (isEmpty())
            return;


        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node node = it.next();
            IPokerPlayer player = node._player;
            if (!player.isEliminated()) {

                double equity;
                if (player.isFolded()) {
                    equity = 0.0;
                }
                else {
                    equity = equityMap.getOrDefault(player.getPlayerId(), 0.0);
                }

                player.notifyEquity(equity);
            }
        }

    }

    private boolean checkIfGameCancel() {

        int connectedPlayers = 0;
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            connectedPlayers += (player._isDisconnected) ? 1 : 0;
        }

        return (connectedPlayers <= 1);
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

    @Override
    public Iterator<Node> iterator() {
        return new PlayerIterator();
    }


}