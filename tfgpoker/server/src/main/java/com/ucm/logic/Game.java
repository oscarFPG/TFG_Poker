package com.ucm.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ucm.middleclasses.CommandResult;
import com.ucm.middleclasses.DTOClient;
import com.ucm.GameType;
import com.ucm.commands.AllInCommand;
import com.ucm.commands.CallCommand;
import com.ucm.commands.CheckCommand;
import com.ucm.commands.Command;
import com.ucm.commands.FoldCommand;
import com.ucm.commands.RaiseCommand;
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
    private boolean _showdownSkipped;

    private int _currentSB;
    private int _currentBB;


    public Game(final List<DTOClient> sockets) {

        _initialSmallBlind = Game.INITIAL_SB;
        _initialBigBlind = Game.INITIAL_BB;
        _handCounter = 1;

        _playerList = new PlayerList(NUM_MAX_PLAYERS, sockets);
        _deck = new Deck();
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _actualTableCards = 0;

        _totalPot = 0;
        _isPreflop = true;
        _showdownSkipped = false;

        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;
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

    public void playHand() throws OnlyOnePlayerLeftException, IOException {

        int pot = 0;
        try {

            int currentBet = 0;
            int maxBet = _currentBB;
            int playsToMake = (_isPreflop) ? _playerList.activePlayersCounter() - 1 : _playerList.activePlayersCounter();
            int playerRemaining = playsToMake + 1;
            Player player = (_isPreflop) ? _playerList.smallBlindAndBigBlindPlays(_currentSB, _currentBB, playsToMake) : _playerList.getFirst();

            // Keep players betting until all have reach the same bet or only one player is left
            while( !(playsToMake == 0) ){   // If all players remaining have checked -> Exit loop

                int codePlay = -1;
                try{
                    // Player executes a command
                    codePlay = player.makePlay();
                }
                catch(IOException e){   // Problems with socket -> Ignore player but keep in match
                    player.fold();
                }

                // Command receives all necessary info
                Command play = Command.parse(codePlay, player);
        
                // Execute command
                CommandResult result = play.execute(_currentSB, _currentBB, maxBet);

                if(Game.DEBUG)
                    System.out.printf("Jugador %s hace %s!\n\n", player.getName(), play.getCommandName());

                // Check number of active players to break normal execution if there is only one left
                if(result.folds()){
                    --playerRemaining;
                    if(playerRemaining == 1)
                        throw new OnlyOnePlayerLeftException("Only one player left to play mid round");
                }

                // Update remaining players loop
                playsToMake = result.raises() ? (_playerList.activePlayersCounter() - 1) : (playsToMake - 1);

                // Update maxBet and get next player
                currentBet = result.bet();
                maxBet = Integer.max(maxBet, currentBet);
                player = _playerList.getNextPlayerActive(player);
            }
        }
        
        catch (OnlyOnePlayerLeftException e) { 
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
        System.out.print("\n");
    }
}