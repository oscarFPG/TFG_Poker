package com.ucm.logic;

import com.ucm.gameobjects.Player;


public class Game{
    
    public static int NUM_MIN_PLAYERS = 2;
    public static int NUM_MAX_PLAYERS = 9;
    
    private Table _table;
    private PlayerList _playerList;
    public Game(){
        _table = new Table();
    }
  
    
    public void addPlayer(Player p){
       _playerList.addPlayer(p);
    }
    
    /*
    
    */
    public void shareOutAllCards(){
        _table.shareOutAllCards();
    }
    
    public void retrieveAllCards(){
        _table.retrieveAllCards();
    }
    
    public void addCardToTable(){
        _table.addCardToTable();
    }
    
    public void retrieveCardsFromTable(){
        _table.retrieveCardsFromTable();
    }
    
    public void assignRolesToAllPlayers(){
        _table.assignRolesToAllPlayers();
    }
    
    public void passTurn(){
        _table.passTurn();
    }
    
    public void playHand(){
        _table.playFullHand();
    }

    public void printGame(){
        _table.printTable();
    }
    
    public boolean isGameFinished(int n, float h, Game c){
        return false;
    }
    
}