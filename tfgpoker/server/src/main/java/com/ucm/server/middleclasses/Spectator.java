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

    public Spectator(String name, Socket socket) {
        _name = name;
        _socket = socket;
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

}