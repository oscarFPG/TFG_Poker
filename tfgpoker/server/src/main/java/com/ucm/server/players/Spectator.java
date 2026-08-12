package com.ucm.server.players;

import java.io.IOException;
import java.net.Socket;

import com.ucm.common.GameType;
import com.ucm.common.PokerStreet;
import com.ucm.common.SocketUtils;
import com.ucm.common.gameobjects.Card;
import com.ucm.server.interfaces.IPlayerInfo;


/**
 * Represents a spectator in the poker game, allowing them to observe the game without participating as a player.
 * This class extends {@link HumanPlayer} and implements the {@link IPlayerInfo} interface to provide information about the spectator's state and actions.
 * Spectators can receive notifications about game events, such as table cards and game rounds, but they do not participate in betting or other player actions.
 */
public class Spectator extends HumanPlayer {

    private static final String DEFAULT_NAME = "Spectator";

    /**
     * The socket connection used to communicate with the spectator's client.
     */
    public Socket _spectatorSocket;

    /**
     * The name of the spectator, which can be set to a default value or customized as needed.
     */
    public String _name;


    /**
     * Constructs a new Spectator instance with the specified socket connection.
     * @param socket the socket connection to the spectator's client
     * @throws IOException if an I/O error occurs when creating the input/output streams
     */
    public Spectator(Socket socket) {
        super(socket);
        _name = DEFAULT_NAME;
        _spectatorSocket = socket;
    }


    @Override
    public void notifyPlayerID(IPlayerInfo player) throws IOException {}

    /**
     * Notifies the spectator of a new table card that has been dealt in the game.
     * @param c the card that has been dealt to the table
     */
    @Override
    public void notifyTableCard(final Card c) throws IOException {
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), GameType.TABLE_CARD);
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), c.getCardValueNetworkCode());
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), c.getSuit().getNetworkCode());
    }

    /**
     * Notifies the spectator of a new game round that has started, indicating the current street (e.g., pre-flop, flop, turn, river).
     * @param street the current street of the game round
     * @throws IOException if an I/O error occurs when sending the notification to the spectator's client
     */
    public void notifyGameRound(final PokerStreet street) throws IOException {
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), GameType.NEW_ROUND);
        SocketUtils.sendInteger(_spectatorSocket.getOutputStream(), street.getNetworkCode());
    }


}