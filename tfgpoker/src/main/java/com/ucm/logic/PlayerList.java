package com.ucm.logic;


import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.PlayerAction;


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
    
    public int playHand(final int sb, final int bb){
        
        boolean handCompleted = false;
        int totalPot = 0;
        int playsMade = 0;
        int lastBet = 0;
        
        Node pNode = _first;
        Node lastNode = _first;
        while(!handCompleted){

            PlayerAction action = pNode._player.makePlay(sb, bb);
            if(action == PlayerAction.FOLD){
                pNode._player.fold();
            }
            else if(action == PlayerAction.RAISE){
                lastNode = pNode;
                playsMade = 0;
            }
            
            pNode = pNode._next;
        }
        
        return totalPot;
    }
    
    public void shareOutAllCardsFromPlayer(Card c, Player p) {
        
    }

    public boolean isEmpty(){ return size() == 0; }
    public boolean isFull(){ return size() == max(); }
    public int size(){ return _playerCounter; }
    public int max(){ return _maxNumberOfPlayers; }

}