package com.ucm.server.logic;

import com.ucm.server.interfaces.IPokerPlayer;

public class Node {

    public IPokerPlayer _player;
    public Node _prev;
    public Node _next;
    public boolean _isDisconnected;

    public Node(Node prev, IPokerPlayer p, Node next) {
        _player = p;
        _prev = prev;
        _next = next;
        _isDisconnected = false;
    }

}