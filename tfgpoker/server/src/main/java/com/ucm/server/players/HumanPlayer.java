package com.ucm.server.players;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.exceptions.TurnTimeoutException;
import com.ucm.server.interfaces.IPlayerInfo;
import com.ucm.server.interfaces.IPlayerNotificator;
import com.ucm.server.logic.Game;


public class HumanPlayer implements IPlayerNotificator {

    private static final Logger log = LogManager.getLogger(HumanPlayer.class);

    /**
     * Socket used for communication with the player.
     * Check {@link Socket} and {@link SocketUtils}.
     */
    private Socket _socket;

    private Scanner _scanner = (Game.DEBUG_PLAYERS) ? new Scanner(System.in) : null;

    /**
     * Constructor for the Player class.
     * 
     * @param id     assignated to the player, it must be unique
     * @param name   of the player, it does not have to be unique
     * @param socket used for communicating with the real player
     * @param money  received at the beginning of the game
     */
    public HumanPlayer(Socket socket) {
        _socket = socket;
    }


    @Override
    public String notifyMakePlay(int sb, int bb, int maxBet, IPlayerInfo player) throws IOException {
        if(Game.DEBUG_PLAYERS) {
            System.out.printf("%s hace: ", player.getPlayerName());
            return _scanner.nextLine();
        }

        // Send round info
        SocketUtils.sendInteger(_socket.getOutputStream(), sb);
        SocketUtils.sendInteger(_socket.getOutputStream(), bb);
        SocketUtils.sendInteger(_socket.getOutputStream(), maxBet);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());

        _socket.setSoTimeout(60 * 1000);

        String commandInput = null;
        try {
            commandInput = SocketUtils.receiveString( _socket.getInputStream() );
            log.debug("Command received from player {}: {}", player.getPlayerName(), commandInput);
        }
        catch(SocketTimeoutException e) {
            log.warn("Player {} turn timeout", player.getPlayerName());
            throw new TurnTimeoutException();
        }
        finally {
            _socket.setSoTimeout(0);
        }
        
        return commandInput;
    }

    @Override
    public void notifyPlayerRole(final PlayerRole role) throws IOException {
        if(Game.DEBUG_PLAYERS) return;
        
        SocketUtils.sendInteger(_socket.getOutputStream(), role.getNetworkCode());
    }

    @Override
    public void notifyPlayerCard(final Card c) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), c.getCardValueNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), c.getSuit().getNetworkCode());
    }

    @Override
    public void notifyTableCard(final Card c) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), c.getCardValueNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), c.getSuit().getNetworkCode());
    }

    @Override
    public void notifySmallBlindBet(final int amount, IPlayerInfo player) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_FORCED_SB);
        SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        
    }

    @Override
    public void notifyBigBlindBet(final int amount, IPlayerInfo player) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_FORCED_BB);
        SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
    }

    @Override
    public void notifyTotalPot(int total) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TOTAL_POT);
        SocketUtils.sendInteger(_socket.getOutputStream(), total);
    }

    @Override
    public void notifyOtherPlayerAction(IPlayerInfo other) throws IOException {
        if(Game.DEBUG_PLAYERS) return;
            
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_OTHER_PLAYER);
        SocketUtils.sendInteger(_socket.getOutputStream(), other.getPlayerId());
        SocketUtils.sendString(_socket.getOutputStream(), other.getPlayerName());
        SocketUtils.sendInteger(_socket.getOutputStream(), other.getRole().getNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), other.isFolded() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), other.isWinner() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendString(_socket.getOutputStream(), other.getLastCommand());
        SocketUtils.sendInteger(_socket.getOutputStream(), other.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), other.getMoneyOnBet());
    }

    @Override
    public void notifyTurnWait() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_WAIT);
    }

    @Override
    public void notifyTurnPlay() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_PLAY);
    }

    @Override
    public void notifyRoundEnded() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.ROUND_ENDS);
    }

    @Override
    public void notifyHandEndsByFolds() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.HAND_ENDS_BY_FOLD);
    }

    @Override
    public void notifyGameEnded() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.GAME_ENDS);
    }

    @Override
    public void notifyGameKeeps() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.GAME_KEEPS);
    }
    
    @Override
    public void notifyGameWinner() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.PLAYER_WINS_GAME);
    }

    @Override
    public void notifyGameLoser() throws IOException {
        if(Game.DEBUG_PLAYERS) return;
        
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.PLAYER_LOSES_GAME);
    }

    @Override
    public void notifyOwnState(IPlayerInfo player) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.MY_PLAYER_STATUS);
        SocketUtils.sendString(_socket.getOutputStream(), player.getPlayerName());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getRole().getNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isFolded() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isWinner() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isEliminated() ? GameType.TRUE : GameType.FALSE);
    }

    @Override
    public void notifyOtherPlayerState(IPlayerInfo player) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.OTHER_PLAYER_STATUS);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getPlayerId());
        SocketUtils.sendString(_socket.getOutputStream(), player.getPlayerName());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getRole().getNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isFolded() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isWinner() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isEliminated() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());
    }

    @Override
    public void notifyEndPlayerState() throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.PLAYER_STATUS_END);
    }

    @Override
    public void notifyEquity(double equity) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.EQUITY_UPDATE);
        String equityStr = String.format("%.2f%%", equity * 100);
        SocketUtils.sendString(_socket.getOutputStream(), equityStr);
    }

    @Override
    public void notifyCurrentTurnPlayer(IPlayerInfo player) throws IOException {
        if(Game.DEBUG_PLAYERS) return;

        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_BEFORE_PLAY);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getPlayerId());
    }

}