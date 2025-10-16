package com.ucm.logic;

import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Card;
import com.ucm.middleclasses.CommandResult;
import com.ucm.gameobjects.PlayerRole;


public class PlayerList {
    
    Player[] players;
    int _contPlayers;

    public PlayerList(){
        _contPlayers = 0;
        players = new Player[Game.NUM_MAX_PLAYERS];
    }

    public PlayerList(int n){
        _contPlayers = 0;
        players = new Player[n];
    }

    public int getContPlayers(){ return _contPlayers; }

    public void addPlayer(Player p){
        players[_contPlayers] = p;
        _contPlayers++;
    }

    public boolean isEmpty() { return size() == 0; }
    public boolean isFull() { return size() == max(); }
    public int size() { return _playerCounter; }
    public int max() { return _maxNumberOfPlayers; }

    public void assignRolesToAllPlayers(){

        int n = getActivePlayers();
        Node _current = _first;

        //NO HAY DEALER --> SOLO SB Y BB
        if ( n == 0 || n == 1 ) return;

        if ( n == 2) {
            _current = getNextPlayerActive(_first);
            _current._player.setRole(PlayerRole.SMALL_BLIND);

            _current = getNextPlayerActive(_current._next);
            _current._player.setRole(PlayerRole.BIG_BLIND);
        }
        else {
            _current = getNextPlayerActive(_first);
            _current._player.setRole(PlayerRole.DEALER);

            _current = getNextPlayerActive(_current._next);
            _current._player.setRole(PlayerRole.SMALL_BLIND);

            _current = getNextPlayerActive(_current._next);
            _current._player.setRole(PlayerRole.BIG_BLIND);
        
            while ( _current != _first){
                _current = getNextPlayerActive(_current._next);
                _current._player.setRole(PlayerRole.NO_ROLE);
                _current = _current._next;
            }
        }
    }


    public int getActivePlayers(){
        
        int cont = (_first._player.hasFolded()) ? 0: 1;
        Node _current = _first._next;
        
        while ( _current != _first){
           cont += (_current._player.hasFolded()) ? 0 : 1;
           _current = _current._next;
        }
        return cont;
    }

    private Node getNextPlayerActive(Node current){
        while ( current._player.hasFolded()){
            current._player.setRole(PlayerRole.NO_ROLE);
            current = current._next;
        }
        return current;
    }

    public void passTurn(){
        _first = _first._next;
        _last = _last._next;
        assignRolesToAllPlayers();

    }

}