package com.ucm.server.logic;


import java.util.ArrayList;
import java.util.List;

import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Deck;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.middleclasses.HandInfo;


/**
 * Class representation of the entire poker game.
 * This class behaves as a delegator of the game logic and a container of the game state.
 * It is responsible for managing the game state and delegating the game logic to the {@link PlayerList} class, the {@link Deck} class and the {@link Evaluator} class.
 */
public class Game {

    public static final boolean DEBUG = true;

    /**
     * Initial small blind value
     */
    public static final int INITIAL_SB = 1;
    
    /**
     * Initial big blind value
     */
    public static final int INITIAL_BB = 2;

    /**
     * Minimum number of players in a game
     */
    public static final int NUM_MIN_PLAYERS = 2;
    
    /**
     * Maximum number of players in a game
     */
    public static final int NUM_MAX_PLAYERS = 9;
    
    /**
     * Maximum number of cards that can be placed on the table
     */
    public static final int MAX_CARDS_IN_TABLE = 5;
    
    /**
     * Counter of the hands played in the current game.
     * This is used to determine when to increase the blinds and to keep track of the game progress.
     */
    private int _handCounter;

    /**
     * List of players in the game.
     * @see PlayerList
     */
    private PlayerList _playerList;

    /**
     * Deck of cards used in the game.
     * @see Deck
     */
    private Deck _deck;

    /**
     * Array of cards placed on the table during the rounds of the game
     */
    private Card[] _tableCards;

    /**
     * Counter of the actual number of cards placed on the table
     */
    private int _actualTableCards;

    /**
     * Total amount of money in the pot for the current hand.
     */
    private int _totalPot;

    /**
     * Boolean flag to indicate if the current hand is in the preflop stage or not.
     */
    private boolean _isPreflop;

    /**
     * Boolean flag to indicate if the showdown stage of the current hand was skipped.
     * This occurs when all players except one folded in their turn so thes is no need to compare hands
     */
    private boolean _showdownSkipped;

    /**
     * Current small blind value.
     * This value can increase as the game progresses and more hands are played.
     */
    private int _currentSB;

    /**
     * Current big blind value.
     * This value can increase as the game progresses and more hands are played.
     */
    private int _currentBB;
    
    
    /**
     * Constructor of the Game class that initializes the game state and prepares it for the first hand.
     */
    public Game() {

        _handCounter = 1;

        _playerList = new PlayerList(Game.NUM_MAX_PLAYERS);
        _deck = new Deck();
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _actualTableCards = 0;

        _totalPot = 0;
        _isPreflop = true;
        _showdownSkipped = false;

        _currentSB = Game.INITIAL_SB;
        _currentBB = Game.INITIAL_BB;
    }

    /**
     * Method to add a player to the game.
     * @param p Player to be added to the game
     */
    public void addPlayer(Player p) {

        if (Game.DEBUG) {
            System.out.printf("Intentando añadir jugador [%s]\n", p.getName());
        }

        _playerList.addPlayer(p);
    }

    /**
     * Method to assign roles to all players at the beginning of each hand.
     */
    public void assignRolesToAllPlayers() {
        _playerList.assignRolesToAllPlayers();
    }

    /**
     * Share out two random cards from the deck to each player at the beginning of each hand.
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

    /**
     * Adds a random card from the deck to the table.
     */
    public void addCardToTable() {

        if (_actualTableCards >= 5)
            return;

        _tableCards[_actualTableCards] = _deck.takeRandomCard();
        _actualTableCards++;
    }

    /**
     * Retrieves all cards from the table and returns them to the deck, resetting the table state for the next hand.
     */
    public void retrieveCardsFromTable() {

        for (int i = 0; i < _actualTableCards; i++){
            _deck.retrieveCard( _tableCards[i] );
            _tableCards[i] = null;
        }

        _actualTableCards = 0;
    }

    /**
     * Pass the turn for the next player to make a play
     */
    public void passTurn() {

        if (Game.DEBUG) {
            System.out.printf("Pasando turno...\n");
        }

        _playerList.passTurn();
    }

    /**
     * Method to execute a hand of poker, where each player makes their play in turn until the hand is finished.
     * @throws OnlyOnePlayerLeftException thrown if all the players fold and there is one left
     */
    public void playHand() throws OnlyOnePlayerLeftException {

        int pot = 0;
        try {
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

        if (Game.DEBUG) {
            System.out.printf("Mano numero %d terminada!\n\n", _handCounter);
        }

        _isPreflop = false;
        pot = _playerList.collectAllBets();
        _totalPot += pot;
        ++_handCounter;
    }

    /**
     * Method to determine the winner(s) of the hand and give them the corresponding reward from the pot.
     */
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

        if (Game.DEBUG && winners.size() == 1) {    
            System.out.printf("%s ha ganado %d$!\n", winners.get(0).getName(), _totalPot);
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

    /**
     * Reset the game stat for the next hand.
     * It retrieves the cards from the table to the {@link Deck}
     * It resets the state of the players in the {@link PlayerList}
     * It resets the state of the {@link Deck}
     * It also sets the {@link #_isPreflop} flag to true and the {@link #_showdownSkipped} flag to false for the next hand
     */
    public void restartRound() {

        retrieveCardsFromTable();
        _playerList.resetPlayers();
        _deck.resetDeck();

        if (Game.DEBUG) {
            System.out.printf("------------------------ Reiniciando ronda... ------------------------\n\n\n");
        }

        _isPreflop = true;
        _showdownSkipped = false;
    }

    /**
     * Method to determine if the game is finished or not.
     * This is used to break the game loop
     * @return true if the game is finished, false otherwise
     */
    public boolean isGameFinished() {
        return false;
    }

    /**
     * Method to show the state of the game in the console for debugging purposes.
     * It shows the state of every player: ID, name, cards, money and role
     * It also shows the cards on the table, whether they are flipped down or not.
     */
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
        System.out.print("\n");
    }

}