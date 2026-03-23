package com.ucm.server.logic;


import java.io.IOException;
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
import com.ucm.server.middleclasses.PlayerEvaluation;


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
    private int _tableCardsCounter;

    private boolean _isPreflop;

    private int _currentSB;
    private int _currentBB;
    
    
    public Game() throws EvaluatorException {
        
        _initialSmallBlind = Game.INITIAL_SB;
        _initialBigBlind = Game.INITIAL_BB;
        _handCounter = 0;

        _playerList = new PlayerList(Game.NUM_MAX_PLAYERS);
        _deck = new Deck();
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _tableCardsCounter = 0;

        _isPreflop = true;

        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;

        try {
            Evaluator.getInstance();
        }
        catch(IOException e) {
            log.error("Trying to create the evaluator: {}", e.getMessage());
            throw new EvaluatorException("Error creating the evaluator for the game");
        }
        
    }


    public void addPlayer(Player p) {

        log.debug("Intentando asignar jugador [{}]", p.getPlayerName());
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

        if (_tableCardsCounter >= 5)
            return;


        Card c = _deck.takeRandomCard();
        _playerList.sendTableCardToAllPlayers(c);
        _tableCards[_tableCardsCounter] = c;
        _tableCardsCounter++;

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

        for (int i = 0; i < _tableCardsCounter; i++){
            _deck.retrieveCard( _tableCards[i] );
            _tableCards[i] = null;
        }

        _tableCardsCounter = 0;
    }

    public void playHand() throws OnlyOnePlayerLeftException {

        ++_handCounter;
        try {
            log.debug("Playing hand number {}...", _handCounter);

            _playerList.playHand(_currentSB, _currentBB, _isPreflop);
            _isPreflop = false;
        } 
        // Collect remaining bets only if the round ended because all players folded
        catch (OnlyOnePlayerLeftException e) {
            _playerList.notifyHandEndsByFold();
            _isPreflop = false;

            log.debug("Hand number {} finished!", _handCounter);
            throw e;
        }
    
        log.debug("Hand number {} finished!", _handCounter);
    }

    public void giveRewardToWinner() {

        List<HandInfo> playersHands = _playerList.getPlayerHandsInfo();
        if(playersHands.size() == 1) {
            _playerList.calculatePrizeForPlayerLeft();
        }
        else {
            List<PlayerEvaluation> playersEval = Evaluator.evaluateAllHands(playersHands, _tableCards);
            _playerList.calculatePrizeDistribution(playersEval);
        }
        
        _playerList.manageEliminatedPlayers();
        _playerList.notifyRankingsToAllPlayers();
    }

    public boolean passTurn() {

        boolean endOfGame = _playerList.checkEndOfGame();
        _playerList.notifyGameEnds(endOfGame);

        if(!endOfGame) {
            log.debug("Preparing for next hand...");

            retrieveCardsFromTable();
            _deck.resetDeck();
            _playerList.passTurn();
            _isPreflop = true;
        }
        else {
            log.debug("End of game");
        }

        return endOfGame;
    }


}