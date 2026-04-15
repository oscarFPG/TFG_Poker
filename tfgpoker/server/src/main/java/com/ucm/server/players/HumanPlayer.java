package com.ucm.server.players;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.logic.Game;


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
    public String actionMakePlay(int sb, int bb, int maxBet) throws IOException {
        
        // Send round info
        SocketUtils.sendInteger(_socket.getOutputStream(), sb);
        SocketUtils.sendInteger(_socket.getOutputStream(), bb);
        SocketUtils.sendInteger(_socket.getOutputStream(), maxBet);
        SocketUtils.sendInteger(_socket.getOutputStream(), _offBetMoney);
        SocketUtils.sendInteger(_socket.getOutputStream(), _onBetMoney);

        String commandInput = SocketUtils.receiveString( _socket.getInputStream() );
        log.debug("Command received from player {}: {}", _name, commandInput);

        return commandInput;
    }

    @Override
    public void notifyMoneyAmount(final int amount) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), amount);
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
    public void notifySmallBlindBet(final int amount) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_FORCED_SB);
        SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        SocketUtils.sendInteger(_socket.getOutputStream(), _onBetMoney);
        SocketUtils.sendInteger(_socket.getOutputStream(), _offBetMoney);
    }

    @Override
    public void notifyBigBlindBet(final int amount) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_FORCED_BB);
        SocketUtils.sendInteger(_socket.getOutputStream(), amount);
        SocketUtils.sendInteger(_socket.getOutputStream(), _onBetMoney);
        SocketUtils.sendInteger(_socket.getOutputStream(), _offBetMoney);
    }

    @Override
    public void notifyTotalPot(int total) throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TOTAL_POT);
        SocketUtils.sendInteger(_socket.getOutputStream(), total);
    }

    @Override
    public void notifyOtherPlayerAction(IPokerPlayer p) throws IOException {

        if(this == p)
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.MY_TURN_ACTION);
        else
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_OTHER_PLAYER);
        
        SocketUtils.sendInteger(_socket.getOutputStream(), p.getPlayerId());
        SocketUtils.sendString(_socket.getOutputStream(), p.getPlayerName());
        SocketUtils.sendInteger(_socket.getOutputStream(), p.getRole().getNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), p.isFolded() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), p.isWinner() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), p.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), p.getMoneyOnBet());
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
    public void notifyPlayerState(final IPokerPlayer player, final boolean last) throws IOException {

        SocketUtils.sendInteger(_socket.getOutputStream(), player.getPlayerId());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getRole().getNetworkCode());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isFolded() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.isWinner() ? GameType.TRUE : GameType.FALSE);
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOffBet());
        SocketUtils.sendInteger(_socket.getOutputStream(), player.getMoneyOnBet());

        if(last)
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TRUE);
        else
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.FALSE);
    }

    @Override
    public void notifyRoundEnded() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.ROUND_ENDS);
    }

    @Override
    public void notifyHandEnded() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.HAND_ENDS);
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
    public void notifyHandEndsByFolds() throws IOException {
        SocketUtils.sendInteger(_socket.getOutputStream(), GameType.HAND_ENDS_BY_FOLD);
    }

    @Override
    public void notifyEquity(double equity) {
        try {
            String equityStr = String.format("%.2f%%", equity * 100);
            SocketUtils.sendString(_socket.getOutputStream(), equityStr);
        }
        catch (IOException e) {
            log.error("Trying to send the equity value to player {}: {}", _name, e.getMessage());
        }

    }

    
}