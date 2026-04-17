package com.ucm.server.logic;

import com.ucm.server.gameobjects.Player;


public class Node {

    public Player _player;
    public Node _prev;
    public Node _next;
    public boolean _isDisconnected;

    public Node(Node prev, Player p, Node next) {
        _player = p;
        _prev = prev;
        _next = next;
        _isDisconnected = false;
    }

}