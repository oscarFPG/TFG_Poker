package com.ucm.logic;


import com.ucm.gameobjects.Player;


public class PlayerList {
    
    public class Node{
        Node _prev;
        Player _player;
        Node _next;
        
        public Node(Node prev, Player p, Node next){
            _prev = prev;
            _player = p;
            _next = next;
        }
    }

    private Node _first;
    private Node _last;
    private int _playerCounter;
    private int _maxNumberOfPlayers;
    
    
    public PlayerList(int n){
        _first = null;
        _last = null;
        _playerCounter = 0;
        _maxNumberOfPlayers = n;
    }
           
    
    public void addPlayer(Player p){
        
        Node newNode = new Node(null, p, null);
        if(isEmpty()){
            _first = newNode;
            _last = _first;
        }
        else{
            newNode._prev = _last;
            newNode._next = _first;
            _last = newNode;
        }
        
        ++_playerCounter;
    }
    
    public void removePlayer(Player p){
        
        if(isEmpty())
            return;

        if(_first._player == p)
            delete(_first);

        Node i = _first._next;
        while(i._player != p && i != _first && i != null)
            i = i._next;

        delete(i);
    }


    private void delete(Node p){

        if(p == null)
            return ;


        Node previous = p._prev;
        Node next = p._next;
        if(previous == next){   // Solo hay dos elementos
            _first = (previous == _first) ? next : previous;
            _first._prev = null;
            _first._next = null;
        }
        else{
            previous._next = next;
            next._prev = previous;
        }

        --_playerCounter;
    }
    

    public boolean isEmpty(){ return _playerCounter == 0; }
    public boolean isFull(){ return size() == max(); }
    public int size(){ return _playerCounter; }
    public int max(){ return _maxNumberOfPlayers; }

}