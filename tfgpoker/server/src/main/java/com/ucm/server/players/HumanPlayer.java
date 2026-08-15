package com.ucm.server.players;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.exceptions.TurnTimeoutException;
import com.ucm.server.interfaces.IPlayerInfo;
import com.ucm.server.interfaces.IPlayerNotificator;


/**
 * Represents a human player in the poker game, responsible for handling communication with the player's client via a socket connection.
 * This class implements the {@link IPlayerNotificator} interface to send game state updates and receive player actions.
 * It manages the player's turn, notifies them of game events, and processes their responses.
 * 
 * This is directly related to the implementation of the client-side logic, which is expected to handle the messages sent by this class and respond accordingly.
 */
public class HumanPlayer implements IPlayerNotificator {

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
    public HumanPlayer(Socket socket) {
        _socket = socket;
    }


    @Override
    public String notifyMakePlay(int sb, int bb, int maxBet, int minRaise, IPlayerInfo player) throws IOException, TurnTimeoutException {

        // Send round info
        SocketUtils.sendInteger(_socket.getOutputStream(), sb);
        SocketUtils.sendInteger(_socket.getOutputStream(), bb);
        SocketUtils.sendInteger(_socket.getOutputStream(), maxBet);
        SocketUtils.sendInteger(_socket.getOutputStream(), minRaise);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());

        _socket.setSoTimeout(60 * 1000);

        // Wait for human response via socket
        String commandInput = null;
        try {
            commandInput = SocketUtils.receiveString( _socket.getInputStream() );
            log.debug("Player {} wants to: {}", player.getPlayerName(), commandInput);
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
        SocketUtils.sendInteger(_socket.getOutputStream(), role.getNetworkCode());
    }

    @Override
    public void notifyPlayerCard(final Card c) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), c.getCardValueNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), c.getSuit().getNetworkCode());
    }

    @Override
    public void notifyTableCard(final Card c) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), c.getCardValueNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), c.getSuit().getNetworkCode());
    }

    @Override
    public void notifySmallBlindBet(final int amount, IPlayerInfo player) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_FORCED_SB);
        SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        
    }

    @Override
    public void notifyBigBlindBet(final int amount, IPlayerInfo player) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_FORCED_BB);
        SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
    }

    @Override
    public void notifyTotalPot(int total) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TOTAL_POT);
        SocketUtils.sendInteger(_socket.getOutputStream(), total);
    }

    @Override
    public void notifyOtherPlayerAction(IPlayerInfo other) throws IOException {
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
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_WAIT);
    }

    @Override
    public void notifyTurnPlay() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_PLAY);
    }

    @Override
    public void notifyRoundEnded() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.ROUND_ENDS);
    }

    @Override
    public void notifyHandEndsByFolds() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.HAND_ENDS_BY_FOLD);
    }

    @Override
    public void notifyGameEnded() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.GAME_ENDS);
    }

    @Override
    public void notifyGameKeeps() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.GAME_KEEPS);
    }
    
    @Override
    public void notifyGameWinner() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.PLAYER_WINS_GAME);
    }

    @Override
    public void notifyGameLoser() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.PLAYER_LOSES_GAME);
    }

    @Override
    public void notifyOwnState(IPlayerInfo player, final boolean receiveRank) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.MY_PLAYER_STATUS);
        SocketUtils.sendString(_socket.getOutputStream(), player.getPlayerName());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getRole().getNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isFolded() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isWinner() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isEliminated() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendString(_socket.getOutputStream(), player.getLastCommand());

        if(receiveRank) {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TRUE);
            SocketUtils.sendString(_socket.getOutputStream(), player.getLastRankName());
        }
        else {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.FALSE);
        }
    }

    @Override
    public void notifyOtherPlayerState(IPlayerInfo player, final boolean receiveRank) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.OTHER_PLAYER_STATUS);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getPlayerId());
        SocketUtils.sendString(_socket.getOutputStream(), player.getPlayerName());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getRole().getNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isFolded() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isWinner() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isEliminated() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());

        if(receiveRank) {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TRUE);
            SocketUtils.sendString(_socket.getOutputStream(), player.getLastRankName());
        }
        else {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.FALSE);
        }
    }

    @Override
    public void notifyEndPlayerState() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.PLAYER_STATUS_END);
    }

    @Override
    public void notifyEquity(double equity) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.EQUITY_UPDATE);
        String equityStr = String.format("%.2f%%", equity * 100);
        SocketUtils.sendString(_socket.getOutputStream(), equityStr);
    }

    @Override
    public void notifyCurrentTurnPlayer(IPlayerInfo player) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_BEFORE_PLAY);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getPlayerId());
    }

    @Override
    public void notifyOtherPlayerCards(IPlayerInfo other) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.PLAYER_CARDS);
        SocketUtils.sendInteger(_socket.getOutputStream(), other.getPlayerId());

        Card c1 = other.getPlayerCards()[0];
        SocketUtils.sendInteger(_socket.getOutputStream(), c1.getCardValueNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), c1.getSuit().getNetworkCode());

        Card c2 = other.getPlayerCards()[1];
        SocketUtils.sendInteger(_socket.getOutputStream(), c2.getCardValueNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), c2.getSuit().getNetworkCode());
    }

    @Override
    public void notifyPlayerID(IPlayerInfo player) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getPlayerId());
    }


    @Override public BotStyle getStyle() { return BotStyle.DEFAULT; }
    @Override public String getPlayerType() { return "HUMAN"; }
    @Override public String getPlayerModel() { return "-"; }

}