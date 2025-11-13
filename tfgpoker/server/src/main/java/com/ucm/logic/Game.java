package com.ucm.logic;

import java.util.List;

import com.ucm.evaluator.Evaluator;
import com.ucm.exceptions.OnlyOnePlayerLeftException;
import com.ucm.gameobjects.Card;
import com.ucm.gameobjects.Deck;
import com.ucm.gameobjects.Player;
import com.ucm.middleclasses.HandInfo;


public class Game {

    public static final boolean DEBUG = true;

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

    private int _currentSB;
    private int _currentBB;


    public Game() {

        _initialSmallBlind = Game.INITIAL_SB;
        _initialBigBlind = Game.INITIAL_BB;
        _handCounter = 1;

        _playerList = new PlayerList(Game.NUM_MAX_PLAYERS);
        _deck = new Deck();
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _actualTableCards = 0;

        _totalPot = 0;
        _isPreflop = false;

        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;
    }


    public void addPlayer(Player p) {

        if (Game.DEBUG) {
            System.out.printf("Intentando añadir jugador [%s]\n", p.getName());
        }

        _playerList.addPlayer(p);
    }

    /**
     * Repartir 2 cartas a todos los jugadores al principio de la partida
     */
    public void shareOutCardsToAllPlayers() {

        if (Game.DEBUG) {
            System.out.printf("Repartiendo cartas a los jugadores...\n");
        }

        for (int i = 0; i < _playerList.size(); i++) {
            Card randomCard1 = _deck.takeRandomCard();
            Card randomCard2 = _deck.takeRandomCard();
            _playerList.shareOutAllCardsFromPlayer(randomCard1, randomCard2);
        }
    }

    public void addCardToTable() {

        if (_actualTableCards >= 5)
            return;

        _tableCards[_actualTableCards] = _deck.takeRandomCard();
        _actualTableCards++;
    }

    public void retrieveCardsFromTable() {

        for (int i = 0; i < _actualTableCards; i++){
            _deck.retrieveCard( _tableCards[i] );
            _tableCards[i] = null;
        }

        _actualTableCards = 0;
    }

    public void assignRolesToAllPlayers() {
        _playerList.assignRolesToAllPlayers();
    }

    public void passTurn() {

        if (Game.DEBUG) {
            System.out.printf("Pasando turno...\n");
        }

        _playerList.passTurn();
    }

    public void playHand() throws OnlyOnePlayerLeftException {

        int pot = 0;
        try {
            _playerList.playHand(_currentSB, _currentBB, _isPreflop);
        } 
        catch (OnlyOnePlayerLeftException e) {  // Collect remaining bets only if the round ended because all players
                                                // folded in their turn and there is only one left
            pot = _playerList.collectAllBets();
            _totalPot += pot;
            throw e;
        }

        if (Game.DEBUG) {
            System.out.printf("Mano numero %d terminada!\n\n", _handCounter);
        }

        pot = _playerList.collectAllBets();
        _totalPot += pot;
        ++_handCounter;
    }

    public void giveRewardToWinner() {

        HandInfo[] playerHands = _playerList.getPlayerHandsInfo();
        List<Player> winners = Evaluator.evaluateAllHands(playerHands, _tableCards);

        if (Game.DEBUG && winners.size() == 1) {    
            System.out.printf("%s ha ganado %d€!\n", winners.get(0).getName(), _totalPot);
        }
        else if(Game.DEBUG && winners.size() > 1){
            System.out.printf("Empate entre %d jugadores: ", winners.size());
            for(Player p : winners)
                System.out.printf("%s ", p.getName());
            System.out.print('\n');
        }

        int rewardPerPlayer = _totalPot / winners.size();
        for(Player p : winners)
            p.receivePriceMoney(rewardPerPlayer);

        _totalPot = 0;
    }

    public void restartRound() {

        retrieveCardsFromTable();
        _playerList.resetPlayers();
        _deck.resetDeck();

        if (Game.DEBUG) {
            System.out.printf("Reiniciando ronda...\n");
        }

        _isPreflop = true;
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
                System.out.print(Card.FlippedDownCardToString());
            } else {
                System.out.print(_tableCards[i].toString());
            }
        }
        System.out.print("\n\n");
    }

}