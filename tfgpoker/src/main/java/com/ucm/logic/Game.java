package com.ucm.logic;

import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.Deck;
import com.ucm.gameobjects.Player;


public class Game {

    public static int NUM_MIN_PLAYERS = 2;
    public static int NUM_MAX_PLAYERS = 9;
    public static final int MAX_CARDS_IN_TABLE = 5;

    
    private PlayerList _playerList;
    private Card[] _tableCards;
    private int _actualTableCards = 0;
    private Deck _deck;

    
    public Game() {
        _deck = new Deck();
        _playerList = new PlayerList(3);
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
    }

    public void addPlayer(Player p) {
        _playerList.addPlayer(p);
    }

    /**
     * Repartir 2 cartas a todos los jugadores al principio de la partida
     */
    public void shareOutAllCards() {
        
        //for (int i = 0; i < (_playerList.getContPlayers() * 2); i++) {
            //Card randomCard = _deck.takeRandomCard();
            //_playerList.shareOutAllCardsFromPlayer(randomCard, (i % _playerList.getContPlayers()));
        //}
    }

    /**
     * Devolver todas las cartas que se hayan cogido
     */
    public void retrieveAllCards() {
        //_playerList.retrieveAllCardsFromPlayers();
        retrieveCardsFromTable();
    }

    /**
     * Devolver todas las cartas que haya cogido un jugador
     */
    public void retrieveAllCardsFromPlayer(Player p) {
        //_playerList.retrieveAllCardsFromPlayer(p);
    }

    public void addCardToTable() {
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

    }

    public void passTurn() {

    }

    public void playHand() {
        
        
    }

    public void printGame() {

    }

    public boolean isGameFinished() {
        return true;
    }

}