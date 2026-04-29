package com.ucm.server.middleclasses;

import java.io.IOException;
import java.net.Socket;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.gameobjects.Card;
import com.ucm.server.gameobjects.Player;


public class Spectator {

    public String _name;
    public Socket _socket;
    public boolean _isDisconnected;

    public Spectator(String name, Socket socket) {
        _name = name;
        _socket = socket;
        _isDisconnected = false;
    }


    public void notifyOtherPlayerState(Player p) throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.OTHER_PLAYER_STATUS);
            SocketUtils.sendInteger(_socket.getOutputStream(), p.getPlayerId());
            SocketUtils.sendString(_socket.getOutputStream(), p.getPlayerName());
            SocketUtils.sendInteger(_socket.getOutputStream(), p.getRole().getNetworkCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), p.isFolded() ? GameType.TRUE : GameType.FALSE);
            SocketUtils.sendInteger(_socket.getOutputStream(), p.isWinner() ? GameType.TRUE : GameType.FALSE);
            SocketUtils.sendInteger(_socket.getOutputStream(), p.isEliminated() ? GameType.TRUE : GameType.FALSE);
            SocketUtils.sendInteger(_socket.getOutputStream(), p.getMoneyOffBet());
            SocketUtils.sendInteger(_socket.getOutputStream(), p.getMoneyOnBet());
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyTableCard(Card c) throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TABLE_CARD);
            SocketUtils.sendInteger(_socket.getOutputStream(), c.getCardValueNetworkCode());
            SocketUtils.sendInteger(_socket.getOutputStream(), c.getSuit().getNetworkCode());
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyTotalPot(final int totalPot) throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TOTAL_POT);
            SocketUtils.sendInteger(_socket.getOutputStream(), totalPot);
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyTurnPlayer(Player p) throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.TURN_BEFORE_PLAY);
            SocketUtils.sendInteger(_socket.getOutputStream(), p.getPlayerId());
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyOtherPlayerAction(Player other) throws CancelGameException {

        try {
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
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyHandEndsByFolds() throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.HAND_ENDS_BY_FOLD);
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyRoundEnded() throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.ROUND_ENDS);
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyGameEnded() throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.GAME_ENDS);
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

    public void notifyGameKeeps() throws CancelGameException {

        try {
            SocketUtils.sendInteger(_socket.getOutputStream(), GameType.GAME_KEEPS);
        }
        catch(IOException e) {
            throw new CancelGameException();
        }
    }

}