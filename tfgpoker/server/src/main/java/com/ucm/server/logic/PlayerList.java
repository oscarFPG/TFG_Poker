package com.ucm.server.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.common.PokerStreet;
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
import com.ucm.server.players.Spectator;



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


    public void initialize() throws CancelGameException {

        // Send all player IDs to identify themselves
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            try {
                player._player.notifyPlayerID();
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        }

    }

    public void assignRolesToAllPlayers() throws CancelGameException {

        int numPlayers = activePlayersCounter();
        Player.CURRENT_PLAYERS = numPlayers;

        // Cannot be possible
        if (numPlayers == 0 || numPlayers == 1)
            throw new CancelGameException();

        // Only SB and BB
        if (numPlayers == 2) {

            Node current = _first;
            try {

                // Give SB role to first player
                current._player.receiveRole(PlayerRole.SMALL_BLIND);
                log.debug("Player {} receives role {}", current._player.getPlayerName(), PlayerRole.SMALL_BLIND.name());
            
                current = getNextPlayerActive(current);

                // Give BB role to first player
                current._player.receiveRole(PlayerRole.BIG_BLIND);
                log.debug("Player {} receives role {}", current._player.getPlayerName(), PlayerRole.BIG_BLIND.name());
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        
            notifyPlayerStateToAllPlayers(false);
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

                    log.debug("Player {} receives role {}", player._player.getPlayerName(), currentRole.name());
                }
                catch (IOException e) {

                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

        notifyPlayerStateToAllPlayers(false);
    }

    public void shareOutCardsToSomePlayer(Card c1, Card c2) throws CancelGameException {

        if (isEmpty())
            return;


        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._isDisconnected && !player._player.isEliminated() && player._player.getCardsCounter() == 0) {

                try {
                    player._player.receiveCard(c1);
                    player._player.receiveCard(c2);

                    if(_spectator != null)
                        _spectator.notifyOtherPlayerCards(player._player);

                    log.debug("Player {} receives the cards: {} {}", player._player.getPlayerName(), c1.toString(), c2.toString());
                    return;
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
        Node current = (playersRemaining == 2) ? _first : _first._next;
        try {

            current._hasActed = false;
            current._player.putSmallBlindBet(sb);
            notifyOtherPlayerActionToAllPlayers(current._player);

            log.debug(
                "Player {} puts {}$ as SMALL_BLIND. Now it has {}$", 
                current._player.getPlayerName(), current._player.getMoneyOnBet(), current._player.getMoneyOffBet()
            );
        }
        catch(IOException e) {
            
            current._isDisconnected = true;
            if( checkIfGameCancel() )
                throw new CancelGameException();
        }
        
        // Pass to big-blind
        current = getNextPlayerActive(current);

        try {

            current._hasActed = false;
            current._player.putBigBlindBet(bb);
            notifyOtherPlayerActionToAllPlayers(current._player);

            log.debug(
                "Player {} puts {}$ as BIG_BLIND. Now it has {}$", 
                current._player.getPlayerName(), current._player.getMoneyOnBet(), current._player.getMoneyOffBet()
            );
        }
        catch(IOException e) {
            
            current._isDisconnected = true;

            if( checkIfGameCancel() )
                throw new CancelGameException();
        }
        
        // Calculate and notify total pot
        int totalPot = calculateTotalPot();
        notifyTotalPotToAllPlayers(totalPot);
    }

    public void playHand(final int sb, final int bb, final PokerStreet street) throws OnlyOnePlayerLeftException, CancelGameException {

        // Only the spectator needs to receive this info
        // IN-GAME players infer this information, so there is no need so send it to them
        if(_spectator != null) {
            try {
                _spectator.notifyGameRound(street);
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        }

        // Avoid asking if all active players have used all their money
        if( checkAllPlayersAllIn() ) {
            log.debug("All players are ALL_IN, skipping betting round");
            notifyRoundEnded();
            return;
        }


        // In case only one player can play -> Cannot play alone
        // E.g: All players all-in except one -> Skip rounds
        int playersRemaining = activePlayersCounter();
        if(playersRemaining == 1) {
            notifyRoundEnded();
            return;
        }

        // Small-blind and big-blind mandatory play !! Does not count as "play", it is done automatically
        if(street == PokerStreet.PREFLOP)
            smallBlindAndBigBlindPlays(sb, bb, playersRemaining);


        Node playerOnTurn = calculatePlayerOnTurn(playersRemaining, street);
        int maxBet = (street == PokerStreet.PREFLOP) ? bb : 0;
        int minRaise = (street == PokerStreet.PREFLOP) ? bb + (bb - 0) : 0;
        int totalPot = 0;
        notifyWaitExceptTo(playerOnTurn);   // Keep all players, except the first one to play, waiting
        do {

            if(street == PokerStreet.PREFLOP)
                log.debug("Current small blind: {}, current big blind: {}, current max bet: {}", sb, bb, maxBet);
            else
                log.debug("Last maximum bet is {}", maxBet);


            // Skip to the next active player in case the current player went all-in and cannot play
            if(playerOnTurn._player.isAllIn()) {
                playerOnTurn._hasActed = true;
                playerOnTurn = getNextPlayerActive(playerOnTurn);
                continue;
            }

            // Notify player to play
            notifyTurnPlayer(playerOnTurn._player);

            // Ask player to play
            Command command = askCommandToPlayer(playerOnTurn, sb, bb, maxBet, minRaise);
            
            // Execute player action
            CommandResult result = command.execute(sb, bb, maxBet);
            playerOnTurn._hasActed = true;
            
            // Notify new player state to every other player, even himself
            notifyPlayerOwnState(playerOnTurn, false);  // false 
            notifyOtherPlayerActionToAllPlayers(playerOnTurn._player);
            
            // Calculate total pot and notify to all players
            totalPot = calculateTotalPot() + _totalPot;
            notifyTotalPotToAllPlayers(totalPot);

            // Check if game has to be cancelled : Only one player connected -> Cannot play alone
            if( result.folds() ) {

                --playersRemaining;
                if (playersRemaining == 1) {

                    _totalPot = totalPot;
                    updateHandState();
                    notifyHandEndsByFold();
                    log.debug("Interrupting the round: only one player left!");
                    throw new OnlyOnePlayerLeftException();
                }
            }

            // Reset hand cycle if necessary
            if( result.raises() ) {
                resetHandCycle();
                playerOnTurn._hasActed = true;
            }

            // Update table state
            minRaise = Math.max(maxBet, result.bet()) + (Math.max(maxBet, result.bet()) - maxBet);
            maxBet = Math.max(maxBet, result.bet());

            // Pass turn to the next active player
            playerOnTurn = getNextPlayerActive(playerOnTurn);
        }
        while( !checkAllPlayersActed() );

        _totalPot = totalPot;
        updateHandState();
        notifyRoundEnded();
    }

    private Command askCommandToPlayer(Node node, final int sb, final int bb, final int maxBet, final int minRaise) throws CancelGameException {

        Player player = node._player;
        Command command = null;

        try {

            node._player.notifyTurnPlay();
            while (command == null) {
         
                String commandString = player.makePlay(sb, bb, maxBet, minRaise);
                String[] commandFormatted = commandString.split(" ");

                command = Command.parseCommand(commandFormatted, player);
            }
        }
        catch (TurnTimeoutException e) {

            log.warn("Player {} TIMEOUT -> auto FOLD", player.getPlayerName());
            command = Command.parseCommand(new String[] {GameType.FOLD_ACTION_FULL}, player);
        }
        catch (IOException e) {

            log.error("Error happened waiting for player {}: {}", player.getPlayerName(), e.getMessage());

            node._isDisconnected = true;
            if( checkIfGameCancel() )
                throw new CancelGameException();
            else
                command = Command.parseCommand(new String[] {GameType.FOLD_ACTION_FULL}, player);
        }

        return command;
    }

    private void resetHandCycle() {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();

            // If the player has folded, is eliminated or made all-in -> Already played, cannot play in next round
            if(!player._isDisconnected)
                player._hasActed = (player._player.isFolded() || player._player.isEliminated() || player._player.isAllIn());
        }
    }

    private void updateHandState() {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();

            // Update pots
            if( !player._player.isEliminated() ) {
                _potManager.updatePlayerPot(player._player.getPlayerId(), player._player.placeOnBetMoney(), player._player.isFolded());
            }

            // Update player status for next round
            // Avoid folded
            player._hasActed = (player._player.isAllIn() || player._player.isFolded() || player._player.isEliminated());
        }
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

        // Notify spectator about showdown round
        if(_spectator != null) {
            try {
                _spectator.notifyGameRound(PokerStreet.SHOWDOWN);
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        }

        List<PotDistribution> distribution = _potManager.calculatePrizeDistribution(players);
        for(PotDistribution dist : distribution) {
            givePotToPlayerWithID(dist.playerID(), dist.potPrize(), dist.rankName());
        }

        notifyPlayerStateToAllPlayers(true);
    }

    public void calculatePrizeForPlayerLeft() throws CancelGameException {

        // Notify spectator about showdown round
        if(_spectator != null) {
            try {
                _spectator.notifyGameRound(PokerStreet.SHOWDOWN);
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        }

        Iterator<Node> it = iterator();
        Node winner = null;
        while( it.hasNext() && winner == null ) {

            Node player = it.next();
            if(!player._player.isFolded() && !player._player.isEliminated()) {
                winner = player;
            }
        }

        PotDistribution dist = _potManager.calculatePrizeForPlayer(winner._player.getPlayerId());
        givePotToPlayerWithID(dist.playerID(), dist.potPrize(), dist.rankName());

        notifyPlayerStateToAllPlayers(false);
    }

    public void sendTableCardToAllPlayers(final Card card) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._player.isEliminated()) {

                try {
                    player._player.receiveTableCard(card);
                }
                catch (IOException e) {
                    
                    player._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

        if(_spectator != null) {

            try {
                _spectator.notifyTableCard(card);
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        }
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
            if(_spectator != null) {

                try {
                    _spectator.notifyOtherPlayerCards(modelPlayer._player);
                }
                catch (IOException e) {
                    throw new CancelGameException();
                }
            }

            // Send cards from modelPlayer to every player
            Iterator<Node> receiverIT = iterator();
            while( receiverIT.hasNext() ) {

                Node receiver = receiverIT.next();
                try {

                    if(receiver._player != modelPlayer._player)
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
            if(!player._isDisconnected && !player._player.isEliminated() && !player._player.isFolded())
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

    private Node calculatePlayerOnTurn(final int numPlayers, final PokerStreet street) {

        if(street == PokerStreet.PREFLOP) {

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

    private void givePotToPlayerWithID(final int id, final int amount, final String rankName) {

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
        winner._player.receiveHandRankName(rankName);
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
            player._hasActed = false;
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


    /* ------ Notify methods ------ */
    private void notifyWaitExceptTo(final Node playerOnTurn) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if( player.equals(playerOnTurn) )
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

    private void notifyTurnPlayer(Player p) throws CancelGameException {

        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(player._isDisconnected || player._player == p)
                continue;


            try {
                player._player.notifyCurrentTurnPlayer(p);
            }
            catch(IOException e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

        
        if(_spectator != null) {
            try {
                _spectator.notifyCurrentTurnPlayer(p);
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        }
        
    }

    private void notifyPlayerOwnState(Node player, final boolean receiveRank) throws CancelGameException {

        try {
            player._player.notifyOwnState(receiveRank);
        }
        catch(IOException e) {
            
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
            catch(IOException e) {
                
                player._isDisconnected = true;
                if( checkIfGameCancel() )
                    throw new CancelGameException();
            }
        }

        if(_spectator != null) {
            try {
                _spectator.notifyOtherPlayerAction(p);
            }
            catch (IOException e) {
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

        
        if(_spectator != null) {
            try {
                _spectator.notifyTotalPot(totalPot);
            }
            catch (IOException e) {
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

        if(_spectator != null) {
            try {
                _spectator.notifyHandEndsByFolds();
            }
            catch (IOException e) {
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

        if(_spectator != null) {
            try {
                _spectator.notifyRoundEnded();
            }
            catch (IOException e) {
                throw new CancelGameException();
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

        if(_spectator != null) {
            try {

                if(gameEnds)
                    _spectator.notifyGameEnded();
                else
                    _spectator.notifyGameKeeps();
            }
            catch (IOException e) {
                throw new CancelGameException();
            }
        }
    }

    private void notifyPlayerStateToAllPlayers(final boolean receiveRank) throws CancelGameException {

        Iterator<Node> targetIt = iterator();
        while( targetIt.hasNext() ) {

            Node receiverPlayer = targetIt.next();
            if( !receiverPlayer._isDisconnected && !receiverPlayer._player.isEliminated() ) {

                // Notify own state to player
                notifyPlayerOwnState(receiverPlayer, receiveRank);

                // Notify player state to the remaining players
                Iterator<Node> it = iterator();
                while( it.hasNext() ) {

                    Node player = it.next();

                    // Ignore himself
                    if( player == receiverPlayer ) {
                        try {
                            if(_spectator != null)
                                _spectator.notifyOtherPlayerState(player._player, receiveRank);
                        }
                        catch (IOException e) {
                            throw new CancelGameException();
                        }

                        continue;
                    }  
                

                    // Notify player state to other player
                    try {
                        receiverPlayer._player.notifyOtherPlayerState(player._player, receiveRank);
                    }
                    catch(IOException e) {
                        
                        receiverPlayer._isDisconnected = true;
                        if( checkIfGameCancel() )
                            throw new CancelGameException();
                    }
                }

                // Notify end of player state to the receiver player in either case (Same/Other player)
                try {
                    receiverPlayer._player.notifyEndPlayerState();
                }
                catch(IOException e) {
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
                catch(IOException e) {
                    
                    node._isDisconnected = true;
                    if( checkIfGameCancel() )
                        throw new CancelGameException();
                }
            }
        }

    }
    /* ------ Notify methods ------ */


    private boolean checkAllPlayersActed() {

        boolean allActed = true;
        Iterator<Node> it = iterator();
        while( allActed && it.hasNext() ) {

            Node player = it.next();
            if(!player._isDisconnected && !player._player.isEliminated())   // Count only players that can play
                allActed &= player._hasActed;
        }

        return allActed;
    }

    private boolean checkAllPlayersAllIn() {

        boolean allPlayersAllIn = true;
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            if(!player._isDisconnected && !player._player.isEliminated() && !player._player.isFolded())
                allPlayersAllIn &= player._player.isAllIn();
        }

        return allPlayersAllIn;
    }

    private boolean checkIfGameCancel() {

        if(_host != null && _host._isDisconnected)
            return true;


        int playerOnGame = 0;
        Iterator<Node> it = iterator();
        while( it.hasNext() ) {

            Node player = it.next();
            playerOnGame += (!player._isDisconnected && !player._player.isEliminated()) ? 1 : 0;
        }

        return (playerOnGame <= 1);
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
    public int getTotalPot() { return _totalPot; }

    public List<Player> getPlayers() {

        List<Player> players = new ArrayList<>();
        Iterator<Node> it = iterator();
        while ( it.hasNext() ) {
            players.add(it.next()._player);
        }

        return players;
    }


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

    @Override
    public Iterator<Node> iterator() { return new PlayerIterator(); }
    public Iterator<Node> iterator(Node start) { return new PlayerIterator(start); }
    
}