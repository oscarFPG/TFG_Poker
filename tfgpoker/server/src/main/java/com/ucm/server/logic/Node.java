package com.ucm.server.logic;

import com.ucm.server.gameobjects.Player;

/**
 * Represents a node in a doubly linked list of players in the game.
 */
public class Node {

    /**
     * The player associated with this node.
     * @see Player
     */
    public Player _player;

    /**
     * The previous node in the linked list.
     */
    public Node _prev;

    /**
     * The next node in the linked list.
     */
    public Node _next;

    /**
     * Indicates whether the player associated with this node is disconnected from the game.
     * This is only useful for human players or bots holding any kind of connection to the server.
     */
    public boolean _isDisconnected;

    /**
     * Indicates whether the player associated with this node has acted in the current round.
     */
    public boolean _hasActed;

    /**
     * Constructs a new Node with the specified previous node, player, and next node.
     * @param prev the previous node in the linked list, can be null if this is the first node
     * @param p the player associated with this node
     * @param next the next node in the linked list, can be null if this is the first node
     */
    public Node(Node prev, Player p, Node next) {
        _player = p;
        _prev = prev;
        _next = next;
        _isDisconnected = false;
        _hasActed = false;
    }

    /**
     * Checks if this node is equal to another node based on the associated player.
     * This is made by comparing the player references, not their contents.
     * @param other the other node to compare with
     * @return true if the players are the same, false otherwise
     */
    public boolean equals(Node other) {
        return _player == other._player;
    }

}