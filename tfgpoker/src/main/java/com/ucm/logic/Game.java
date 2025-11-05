package com.ucm.logic;

import com.ucm.exceptions.OnlyOnePlayerLeftException;
import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.Deck;
import com.ucm.gameobjects.Player;


public class Game {

    public static final boolean DEBUG = true;

    public static final int NUM_MIN_PLAYERS = 2;
    public static final int NUM_MAX_PLAYERS = 9;
    public static final int MAX_CARDS_IN_TABLE = 5;

    private int _initialSmallBlind = 1;
    private int _initialBigBlind = 2;

    private PlayerList _playerList;
    private Card[] _tableCards;
    private Deck _deck;
    private int _actualTableCards;
    private int _totalPot;
    private boolean _isPreflop;

    private int _currentSB;
    private int _currentBB;


    public Game() {
        _deck = new Deck();
        _playerList = new PlayerList(3);
        _tableCards = new Card[MAX_CARDS_IN_TABLE];

        _actualTableCards = 0;
        _totalPot = 0;

        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;
    }


    public void addPlayer(Player p) {

        if(Game.DEBUG){
            System.out.printf("Intentando añadir jugador [%s]\n", p.getName());   
        }

        _playerList.addPlayer(p);
    }

    /**
     * Repartir 2 cartas a todos los jugadores al principio de la partida
     */
    public void shareOutCardsToAllPlayers() {

        if(Game.DEBUG){
            System.out.printf("Repartiendo cartas a los jugadores...\n");
        }

        for (int i = 0; i < _playerList.size(); i++) {
            Card randomCard1 = _deck.takeRandomCard();
            Card randomCard2 = _deck.takeRandomCard();
            _playerList.shareOutAllCardsFromPlayer(randomCard1, randomCard2);
        }
    }

    /**
     * Devolver todas las cartas que se hayan cogido
     */
    public void retrieveAllCards() {
        
        if(Game.DEBUG){
            System.out.printf("Devolviendo todas las cartas...\n");
        }
        
        _playerList.retrieveAllCardsFromPlayers();
        retrieveCardsFromTable();
    }

    /**
     * Devolver todas las cartas que haya cogido un jugador
     */
    public void retrieveAllCardsFromPlayer(Player p) {
        _playerList.retrieveAllCardsFromPlayer(p);
    }

    public void addCardToTable() {
        if ( _tableCards.length >= 5){
             return;
        }
          _tableCards[_actualTableCards] = _deck.takeRandomCard();
         _actualTableCards++;
    }

    public void retrieveCardsFromTable() {

        for (Card old_card : _tableCards) {
            _deck.retrieveCard(old_card);
        }

        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _actualTableCards = 0;
    }

    public void assignRolesToAllPlayers() {
        _playerList.assignRolesToAllPlayers();
    }

    public void passTurn() {
        
        if(Game.DEBUG){
            System.out.printf("Pasando turno...\n");
        }
        
        _playerList.passTurn();
    }

    public void playHand() throws OnlyOnePlayerLeftException {

        if(Game.DEBUG){
            System.out.printf("Jugando mano...\n");
        }
        
        int pot = 0;
        try{
            _playerList.playHand(_currentSB, _currentBB, _isPreflop);
        }
        catch(OnlyOnePlayerLeftException e){    // Collect remaining bets only if the round ended because all players folded in their turn and there is only one left
            pot = _playerList.collectAllBets();
            _totalPot += pot;
            throw e;
        }
        
        pot = _playerList.collectAllBets();
        _totalPot += pot;
    }

    public Player giveRewardToWinner(){

        Player p = _playerList.selectWinner();
        p.receivePriceMoney(_totalPot);

        if(Game.DEBUG){
            System.out.printf("%s ha ganado %d€!\n", p.getName(), _totalPot);
        }
        
        return p;
    }

    public void restartRound(){
        
        _deck.resetDesk();
        _playerList.resetPlayers();
        
        if(Game.DEBUG){
            System.out.printf("Reiniciando ronda...\n");
        }
        
        // TODO
        _isPreflop = true;
    }
    
    public boolean isGameFinished() {
        return false;
    }

    public void showStateDEBUG(){
        
        // Mostrar estado de los jugadores y sus cartas
        _playerList.showPlayersStateDEBUG();
        
        // Mostrar estado de las cartas de la mesa
        for(int i = 0; i < _tableCards.length; i++){
            
            if(_tableCards[i] == null){
                System.out.print( Card.FlippedDownCardToString() );
            }
            else{
                System.out.print( _tableCards[i].toString() );
            }
        }
    }
    
}