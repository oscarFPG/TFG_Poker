package com.ucm.server.logic;


import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.gameobjects.Card;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.common.ClientStruct;
import com.ucm.common.GameConfig;
import com.ucm.common.GameInfo;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Deck;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.players.HumanPlayer;


public class Game {

    private static final Logger log = LogManager.getLogger(Game.class);

    public static final boolean DEBUG = true;
    public static final int MAX_CARDS_IN_TABLE = 5;

    private GameConfig _gameConfig;
    private int _initialSmallBlind;
    private int _initialBigBlind;
    private int _currentSB;
    private int _currentBB;

    private PlayerList _playerList;
    private Deck _deck;
    private Card[] _tableCards;
    private int _tableCardsCounter;
    private boolean _isPreflop;

    private int _handCounter = 0;
    

    public Game(GameInfo gameInfo) throws EvaluatorException {

        _gameConfig = gameInfo.gameConfig;

        String[] parts = _gameConfig._blindsValue.split("/");
        _initialSmallBlind = Integer.parseInt(parts[0]);    // TODO : Controlar errores de formato
        _initialBigBlind = Integer.parseInt(parts[1]);      // TODO : Controlar errores de formato
        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;

        _playerList = new PlayerList(_gameConfig._numPlayers + 1);
        addPlayerInitial(gameInfo);
        _deck = new Deck();
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _tableCardsCounter = 0;
        _isPreflop = true;

        try {
            Evaluator.getInstance();
        }
        catch(IOException e) {
            log.error("Trying to create the evaluator: {}", e.getMessage());
            throw new EvaluatorException( String.format("Error creating the evaluator for the game : %s", e.getMessage()) );
        }
        
    }

    
    public void assignRolesToAllPlayers() throws CancelGameException {
       _playerList.assignRolesToAllPlayers();
    }

    public void shareOutCardsToAllPlayers() throws CancelGameException {
        
        for (int i = 0; i < _playerList.size(); i++) {
            Card randomCard1 = _deck.takeRandomCard();
            Card randomCard2 = _deck.takeRandomCard();
            _playerList.shareOutAllCardsFromPlayer(randomCard1, randomCard2);
        }
    }

    public void addCardToTable() throws CancelGameException {

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
        log.debug("Table cards: {}", sb.toString());
    }

    public void playHand() throws OnlyOnePlayerLeftException, CancelGameException {

        ++_handCounter;
        try {
            _playerList.playHand(_currentSB, _currentBB, _isPreflop);
            _isPreflop = false;
        }
        // Collect remaining bets only if the round ended because all players folded
        catch (OnlyOnePlayerLeftException e) {
            _isPreflop = false;
            throw e;
        }
    }

    public void giveRewardToWinner() throws CancelGameException {

        List<HandInfo> playersHands = _playerList.getPlayerHandsInfo();
        if(playersHands.size() == 1) {
            _playerList.calculatePrizeForPlayerLeft();
        }
        else {
            List<PlayerEvaluation> playersEval = Evaluator.evaluateAllHands(playersHands, _tableCards);
            _playerList.calculatePrizeDistribution(playersEval);
        }
        
        _playerList.manageEliminatedPlayers();
    }

    public boolean passTurn() throws CancelGameException {

        boolean endOfGame = _playerList.checkEndOfGame();
        _playerList.notifyGameEnds(endOfGame);

        if(!endOfGame) {
            retrieveCardsFromTable();
            _deck.resetDeck();
            _playerList.passTurn();
            _isPreflop = true;
        }

        return endOfGame;
    }

    
    private void addPlayerInitial(GameInfo gameInfo) {

        int id = 0;
        for(ClientStruct cs : gameInfo.players) {
            _playerList.addPlayer( new HumanPlayer(id, cs.name(), cs.socket(), gameInfo.gameConfig._initialMoney));
            ++id;
        }
    }

    private void retrieveCardsFromTable() {

        for (int i = 0; i < _tableCardsCounter; i++){
            _deck.retrieveCard( _tableCards[i] );
            _tableCards[i] = null;
        }

        _tableCardsCounter = 0;
    }

}