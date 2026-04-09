package com.ucm.server.players;

import java.io.IOException;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.SocketUtils;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.control.GameAdapter;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.logic.Game;


/**
 * This class represents any king of player in the game, whether its a human
 * player or and AI player.
 * It works as a representation of the player entity in the server side.
 */
public class HumanPlayer extends Player {

    private static final Logger log = LogManager.getLogger(HumanPlayer.class);


    /**
     * Socket used for communication with the player.
     * Check {@link Socket} and {@link SocketUtils}.
     */
    private Socket _socket;


    /**
     * Constructor for the Player class.
     * 
     * @param id     assignated to the player, it must be unique
     * @param name   of the player, it does not have to be unique
     * @param socket used for communicating with the real player
     * @param money  received at the beginning of the game
     */
    public HumanPlayer(final int id, final String name, Socket socket, final int money) {
        super(id, name, money);
        _socket = socket;
    }


    @Override
    public final String actionMakePlay(final int sb, final int bb, final int maxBet) {
        
        String commandInput = null;
        try {

            // Send round info
            SocketUtils.sendInteger(_socket.getOutputStream(), sb);
            SocketUtils.sendInteger(_socket.getOutputStream(), bb);
            SocketUtils.sendInteger(_socket.getOutputStream(), maxBet);
            SocketUtils.sendInteger(_socket.getOutputStream(), _offBetMoney);
            SocketUtils.sendInteger(_socket.getOutputStream(), _onBetMoney);

            commandInput = SocketUtils.receiveString( _socket.getInputStream() );
        }
        catch (IOException e) {
            log.error("Receiving the command for {} player: {}", _name, e.getMessage());
        }

        return commandInput;
    }


    @Override
    public void notifyMoneyAmount(final int amount) {

       try {
            SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        }
        catch (IOException e) {
            log.error("Trying to send the money value to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyPlayerRole(final PlayerRole role) {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), role.getNetworkCode());
        }
        catch (IOException e) {
            log.error("Receiving the role for {} player: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyPlayerCard(final Card c) {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), c.getCardValueNetworkCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), c.getSuit().getNetworkCode());
        }
        catch (IOException e) {
            log.error("Giving the card {} to player {}: {}", c.toString(), _name, e.getMessage());
        }
    }

    @Override
    public void notifyTableCard(final Card c) {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), c.getCardValueNetworkCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), c.getSuit().getNetworkCode());
        }
        catch(IOException e) {
            log.error("Trying to send a table card to {}: {}", _name,  e.getMessage());
        }
    }

    @Override
    public void notifySmallBlindBet(final int amount) {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnForcedSBToCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        }
        catch(IOException e) {
            log.error("Player {} making the small blind bet: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyBigBlindBet(final int amount) {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnForcedSBToCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        }
        catch(IOException e) {
            log.error("Player {} making the small blind bet: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyTurnWait() {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnWaitToCode());
        }
        catch (IOException e) {
            log.error("Sending the WAIT order to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyTurnPlay() {

        try {

            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnPlayToCode());
        }
        catch (IOException e) {
            log.error("Receiving the command for {} player: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyRoundEnded() {
 
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameRoundEnded());
        }
        catch (IOException e) {
            log.error("Notifing ROUND_ENDS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyHandEnded() {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameHandEnded());
        }
        catch(IOException e) {
            log.error("Notifying HAND_ENDS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyGameEnded() {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameEnded());
        }
        catch(IOException e) {
            log.error("Notifying GAME_ENDS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyGameKeeps() {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.gameKeeps());
        }
        catch(IOException e) {
            log.error("Notifying GAME_KEEPS to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyHandWinner() {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerWinsHand());
        }
        catch(IOException e) {
            log.error("Notifying HAND_WINNER to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyHandLoser() {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerLosesHand());
        }
        catch(IOException e) {
            log.error("Notifying HAND_LOSER to player {}: {}", _name, e.getMessage());
        }
    }
    
    @Override
    public void notifyGameWinner() {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerWinsGame());
        }
        catch(IOException e) {
            log.error("Notifying GAME_WINNER to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyGameLoser() {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerLosesGame());
        }
        catch(IOException e) {
            log.error("Notifying GAME_LOSER to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyHandEndsByFolds() {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.handEndsByFolds());
        }
        catch(IOException e) {
            log.error("Notifying HAND_ENDS_BY_FOLDS to player {}: {}", _name, e.getMessage());
        }
    }

    
}