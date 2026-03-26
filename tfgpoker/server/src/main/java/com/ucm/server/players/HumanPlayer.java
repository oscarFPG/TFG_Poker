package com.ucm.server.players;

import java.io.IOException;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.SocketUtils;
import com.ucm.server.control.GameAdapter;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.gameobjects.PlayerRole;
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
    public void receiveRole(PlayerRole r) {
        
        if (Game.DEBUG) {
            return;
        }

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerRoleToCode(r));
            _role = r;
        }
        catch (IOException e) {
            log.error("Receiving the role for {} player: {}", _name, e.getMessage());
        }
    }

    @Override
    public void receiveCard(Card c) {

        if (_numCards == 2)
            return ;


        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardValueToCode(c));
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardSuitToCode(c));
            _cards[_numCards++] = c;
        }
        catch (IOException e) {
            log.error("Giving the card {} to player {}: {}", c.toString(), _name, e.getMessage());
        }
    }

    @Override
    public void receiveTableCard(Card c) {
        
        if (Game.DEBUG) {
            return;
        }

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardValueToCode(c));
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.cardSuitToCode(c));
        }
        catch(IOException e) {
            log.error("Trying to send a table card to {}: {}", _name,  e.getMessage());
        }
    }

    @Override
    public void receiveNewMoney(int money) {

       try {
            SocketUtils.sendInteger(_socket.getOutputStream(), money);
        }
        catch (IOException e) {
            log.error("Trying to force a move on the player {}: {}", _name, e.getMessage());
        }
    }

    /**
     * The player receives money
     * 
     * @param money received by the player
     */
    @Override
    public void receivePriceMoney(int money) {
        _offBetMoney += money;
    }

    

    @Override
    public void notifyTurnWait() {

        if (Game.DEBUG) {
            return;
        }

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnWaitToCode());
        }
        catch (IOException e) {
            log.error("Sending the WAIT order to player {}: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyTurnPlay() {

        if (Game.DEBUG) {
            return ;
        }

        try {

            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnPlayToCode());
        }
        catch (IOException e) {
            log.error("Receiving the command for {} player: {}", _name, e.getMessage());
        }
    }

    @Override
    public void notifyRoundEnded() {
        
        if (Game.DEBUG) {
            return;
        }

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

        try{
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerWinsHand());
        }
        catch(IOException e){
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

    @Override
    public void actionSmallBlindBet(final int sb) {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnForcedSBToCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), sb);

            decreaseOffBetMoney(sb);
            increaseOnBetMoney(sb);
        }
        catch(IOException e) {
            log.error("Player {} making the small blind bet: {}", _name, e.getMessage());
        }
    }

    @Override
    public void actionBigBlindBet(final int bb) {
        
        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameAdapter.playerTurnForcedSBToCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), bb);

            decreaseOffBetMoney(bb);
            increaseOnBetMoney(bb);
        }
        catch(IOException e) {
            log.error("Player {} making the small blind bet: {}", _name, e.getMessage());
        }
    }

    @Override
    public String actionMakePlay(final int sb, final int bb, final int maxBet) {
        
        String commandInput = null;
        try {

            // Send round info
            SocketUtils.sendInteger(_socket.getOutputStream(), sb);
            SocketUtils.sendInteger(_socket.getOutputStream(), bb);
            SocketUtils.sendInteger(_socket.getOutputStream(), maxBet);
            SocketUtils.sendInteger(_socket.getOutputStream(), _offBetMoney);

            commandInput = SocketUtils.receiveString( _socket.getInputStream() );
        }
        catch (IOException e) {
            log.error("Receiving the command for {} player: {}", _name, e.getMessage());
        }

        return commandInput;
    }

    
}