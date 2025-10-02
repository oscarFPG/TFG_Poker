package com.ucm.logic;

import com.ucm.gameobjects.Player;
import com.ucm.gameobjects.Deck;


public class Game{
    
    public static int NUM_MIN_PLAYERS = 2;
    public static int NUM_MAX_PLAYERS = 9;
    
    private PlayerList _playerList;
    private Deck _deck;

    public Game(){
        _deck = new Deck();
        _playerList = new PlayerList();
    }
  
    
    public void addPlayer(Player p){
       _playerList.addPlayer(p);
    }
    
    public void shareOutAllCards(){
        
    }
    
    public void retrieveAllCards(){
      
    }
    
    public void addCardToTable(){
       
    }
    
    public void retrieveCardsFromTable(){
       
    }
    
    public void assignRolesToAllPlayers(){
       
    }
    
    public void passTurn(){
       
    }
    
    public void playHand(){
       
    }

    public void printGame(){
       
    }
    
    public boolean isGameFinished(int n, float h, Game c){
        return false;
    }
    
}