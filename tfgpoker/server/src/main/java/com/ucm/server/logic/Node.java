package com.ucm.server.logic;

import com.ucm.server.gameobjects.Player;


public class Node {

    public Player _player;
    public Node _prev;
    public Node _next;
    public boolean _isDisconnected;
    public boolean _hasActed;

    public Node(Node prev, Player p, Node next) {
        _player = p;
        _prev = prev;
        _next = next;
        _isDisconnected = false;
        _hasActed = false;
    }

    public boolean equals(Node other) {
        return _player == other._player;
    }

}