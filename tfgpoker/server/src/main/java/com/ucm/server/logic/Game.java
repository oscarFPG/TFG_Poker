package com.ucm.server.logic;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Deck;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.HandInfo;


public class Game {

    private static final Logger log = LogManager.getLogger(Game.class);

    public static final boolean DEBUG = false;

    public static final int INITIAL_SB = 1;
    public static final int INITIAL_BB = 2;
    public static final int NUM_MIN_PLAYERS = 2;
    public static final int NUM_MAX_PLAYERS = 9;
    public static final int MAX_CARDS_IN_TABLE = 5;

    private int _initialSmallBlind;
    private int _initialBigBlind;
    private int _handCounter;

    private PlayerList _playerList;
    private Deck _deck;
    private Card[] _tableCards;
    private int _actualTableCards;

    private int _totalPot;
    private boolean _isPreflop;
    private boolean _showdownSkipped;

    private int _currentSB;
    private int _currentBB;
    
    
    public Game() throws EvaluatorException {
        
        _initialSmallBlind = Game.INITIAL_SB;
        _initialBigBlind = Game.INITIAL_BB;
        _handCounter = 1;

        _playerList = new PlayerList(Game.NUM_MAX_PLAYERS);
        _deck = new Deck();
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _actualTableCards = 0;

        _totalPot = 0;
        _isPreflop = true;
        _showdownSkipped = false;

        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;

        try{
            Evaluator.getInstance();
        }
        catch(IOException e){
            throw new EvaluatorException("Error creating the evaluator for the game");
        }
        
    }

    public void addPlayer(Player p) {

        log.debug("Intentando asignar jugador [{}]", p.getName());
        _playerList.addPlayer(p);
    }

    public void assignRolesToAllPlayers() {
        _playerList.assignRolesToAllPlayers();
    }

    public void shareOutCardsToAllPlayers() {

        log.debug("Repartiendo cartas a los jugadores...");
        for (int i = 0; i < _playerList.size(); i++) {
            Card randomCard1 = _deck.takeRandomCard();
            Card randomCard2 = _deck.takeRandomCard();
            _playerList.shareOutAllCardsFromPlayer(randomCard1, randomCard2);
        }
    }

    public void addCardToTable() {

        if (_actualTableCards >= 5)
            return;


        Card c = _deck.takeRandomCard();
        _playerList.sendTableCardToAllPlayers(c);
        _tableCards[_actualTableCards] = c;
        _actualTableCards++;

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < MAX_CARDS_IN_TABLE; i++){
            if(_tableCards[i] == null)
                sb.append(Card.FlippedDownCardToString()).append(" ");
            else
                sb.append(_tableCards[i].toString()).append(" ");
        }
        log.debug("Cartas en la mesa: {}", sb.toString());
    }

    public void retrieveCardsFromTable() {

        for (int i = 0; i < _actualTableCards; i++){
            _deck.retrieveCard( _tableCards[i] );
            _tableCards[i] = null;
        }

        _actualTableCards = 0;
    }

    public void passTurn() {

        log.debug("Pasando turno...");
        _playerList.passTurn();
    }

    public void playHand() throws OnlyOnePlayerLeftException {

        int pot = 0;
        try {
            log.debug("Playing hand number {}...", _handCounter);
            _playerList.playHand(_currentSB, _currentBB, _isPreflop);
        } 
        catch (OnlyOnePlayerLeftException e) {  // Collect remaining bets only if the round ended because all players
                                                // folded in their turn and there is only one left
            _isPreflop = false;
            _showdownSkipped = true;
            pot = _playerList.collectAllBets();
            _totalPot += pot;
            throw e;
        }
        log.debug("Hand number {} finished!", _handCounter);

        _isPreflop = false;
        pot = _playerList.collectAllBets();
        _totalPot += pot;
        ++_handCounter;
    }

    public void giveRewardToWinner() {

        HandInfo[] playerHands = _playerList.getPlayerHandsInfo();
        List<Player> winners = null;

        if(_showdownSkipped){
            winners = new ArrayList<Player>();
            winners.add( playerHands[0].player() );
        }
        else{
            winners = Evaluator.evaluateAllHands(playerHands, _tableCards);
        }

        if (winners.size() == 1) {    
            log.debug("{} ha ganado {}$!", winners.get(0).getName(), _totalPot);
        }
        else {
            log.debug("Empate entre {} jugadores: ", winners.size());
            for(Player p : winners)
                log.debug("{} ", p.getName());
        }

        int rewardPerPlayer = _totalPot / winners.size();
        for(Player p : winners)
            p.receivePriceMoney(rewardPerPlayer);

        _totalPot = 0;
    }

    public void restartRound() {

        log.debug("------------------------ Reiniciando ronda... ------------------------");
        
        retrieveCardsFromTable();
        _playerList.resetPlayers();
        _deck.resetDeck();

        _isPreflop = true;
        _showdownSkipped = false;
    }

    public boolean isGameFinished() {
        return false;
    }

    public void showStateDEBUG() {

        // Mostrar estado de los jugadores y sus cartas
        _playerList.showPlayersStateDEBUG();

        // Mostrar estado de las cartas de la mesa
        for (int i = 0; i < _tableCards.length; i++) {

            if (_tableCards[i] == null) {
                log.debug(Card.FlippedDownCardToString());
            } else {
                log.debug(_tableCards[i].toString());
            }
        }
    }

}