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
    
    
    public PlayerList(){
        _first = new Node(null, null, null);
        _playerCounter = 0;
        _maxNumberOfPlayers = 3;
    }
           
    
    public void addPlayer(Player p){
        
        if(isEmpty()){
            _first._player = p;
            _first._next = _first;
            _first._prev = _first;
            
            _last = _first;
            _last._next = _first;
            _last._prev = _first;
            
            ++_playerCounter;
        }
        else if(size() == max() - 1){   // Last player, bond last with first
        
        }
        else{
            
        }
    }
    
    public void removePlayer(Player p){
        
        Node previous = _first;
        Node next = null;
        int i = 0;
        
        while(i < size() && previous._next._player != p){
            previous = previous._next;
            ++i;
        }
        next = previous._next._next;
        
        
    }
    
    public boolean isEmpty(){
        return _playerCounter == 0;
    }
    
    public int size(){
        return _playerCounter;
    }
    
    public int max(){
        return _maxNumberOfPlayers;
    }
    
            
            
}