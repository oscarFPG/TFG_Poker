package com.ucm.server.players;

import java.io.IOException;
import java.net.Socket;

import com.ucm.common.GameType;
import com.ucm.common.PokerStreet;
import com.ucm.common.SocketUtils;
import com.ucm.common.gameobjects.Card;
import com.ucm.server.logic.Game;


public class Spectator extends HumanPlayer {

    private static final String DEFAULT_NAME = "Spectator";

    public Socket _spectatorSocket;
    public String _name;


    public Spectator(Socket socket) {
        super(socket);
        _name = DEFAULT_NAME;
        _spectatorSocket = socket;
    }


    @Override
    public void notifyTableCard(final Card c) throws IOException {
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), GameType.TABLE_CARD);
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), c.getCardValueNetworkCode());
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), c.getSuit().getNetworkCode());
    }

    public void notifyGameRound(final PokerStreet street) throws IOException {
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), GameType.NEW_ROUND);
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), street.getNetworkCode());
    }
}