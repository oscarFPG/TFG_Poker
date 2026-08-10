package com.ucm.server.logic;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotStruct;
import com.ucm.common.BotStyle;
import com.ucm.common.ClientStruct;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PokerStreet;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Deck;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.history.PokerHistory;
import com.ucm.server.managers.BotManager;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.players.HumanPlayer;
import com.ucm.server.players.Spectator;
import com.ucm.server.statistics.EquityCalculator;
import com.ucm.server.statistics.PlayerExperimentData;


/**
 * Represents a poker game instance with all necessary components and logic.
 * It handles the game flow, including player management, card dealing, betting rounds, and determining winners.
 * The class also manages the game history and supports dynamic blind adjustments based on the game configuration.
 */
public class Game {

    private static final Logger log = LogManager.getLogger(Game.class);

    /**
     * Atomic integer to generate unique match IDs for each game instance.
     * This ensures that each game has a distinct identifier, which is useful for tracking and logging purposes.
     */
    private static final AtomicInteger NEXT_MATCH_ID = new AtomicInteger(1);

    /**
     * DateTimeFormatter to format the match ID with a timestamp.
     * The format used is "yyyyMMdd_HHmmss_SSS", which includes the year, month, day, hour, minute, second, and millisecond.
     * This helps in creating a unique and easily readable match ID for each game instance.
     */
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    
    
    /**
     * Flag to keep the game in debug mode, enabling additional logging and diagnostic information.
     * This can be useful for development and troubleshooting purposes, allowing developers to trace the flow of the game and identify issues more easily.
     */
    public static final boolean DEBUG = true;

    /**
     * Maximum number of cards that can be placed on the table during a poker game.
     * In Texas Hold'em, this is typically 5 cards (the flop, turn, and river).
     */
    public static final int MAX_CARDS_IN_TABLE = 5;


    /* ---------------------- Poker history ---------------------- */
    /**
     * The current poker history instance for the ongoing game.
     * This object is responsible for recording the events and actions that occur during the game, such as player actions, card distributions, and betting rounds.
     * It allows for tracking the progress of the game and can be used for analysis or replay purposes.
     */
    PokerHistory _history = null;

    /**
     * The unique identifier for the current match
     */
    private final String _matchId;

    /**
     * Counter to keep track of the number of hands played in the current game.
     */
    private int _handCounter = 1;


    /* ---------------------- Game state and config ---------------------- */
    /**
     * Game configuration
     * @see GameConfig
     */
    private GameConfig _gameConfig;

    /**
     * The current street of the poker game, indicating the stage of the hand being played (e.g., pre-flop, flop, turn, river)
     * @see PokerStreet
     */
    private PokerStreet _currentStreet;

    /**
     * The initial small blind value for the game
     */
    private int _initialSmallBlind;

    /**
     * The initial big blind value for the game
     */
    private int _initialBigBlind;

    /**
     * The current small blind value for the game.
     * This value can change during the game if the blinds increase over time.
     * @see GameConfig for dynamic blind settings
     */
    private int _currentSB;

    /**
     * The current big blind value for the game.
     * This value can change during the game if the blinds increase over time.
     * @see GameConfig for dynamic blind settings
     */
    private int _currentBB;

    /**
     * The percentage by which the blinds will increase at each level.
     * This value is used to calculate the new blind values when the game progresses to a new level.
     * @see GameConfig for dynamic blind settings
     */
    private float _hikePercentage;

    /**
     * The current level of the game, which is used to determine the blind values and other game parameters.
     * This value is incremented each time the blinds increase, and it can be used to track the progression of the game.
     * @see GameConfig for dynamic blind settings
     */
    private int _level;


    /* ---------------------- Game objects ---------------------- */
    /**
     * The list of players participating in the game, including both human players and bots.
     * This object manages the players' states, actions, and interactions during the game.
     * @see PlayerList
     */
    private PlayerList _playerList;

    /**
     * The deck of cards used in the game, which is responsible for shuffling and dealing cards to players and the table.
     * @see Deck
     */
    private Deck _deck;

    /**
     * The array of cards currently on the table, representing the community cards that players can use to form their hands.
     * The maximum number of cards on the table is defined by {@link #MAX_CARDS_IN_TABLE}.
     */
    private Card[] _tableCards;

    /**
     * Counter to keep track of the number of cards currently on the table.
     * This value is used to ensure that no more than the maximum allowed number of cards are placed on the table during the game.
     */
    private int _tableCardsCounter;

    /**
     * Flag indicating whether the game is currently in the pre-flop stage.
     * This flag is used to determine the current stage of the hand and to control the flow of the game, including betting rounds and card dealing.
     * @see PokerStreet
     */
    private boolean _isPreflop;


    /**
     * Timer object used to manage the timing of blind increases in the game.
     * This timer is activated at the start of the game and is used to trigger blind increases
     * @see Timer
     */
    private Timer _timer = null;

    /**
     * One time flag to activate the timer only at the very first hand of the game.
     */
    private boolean _firstHand = true;


    /**
     * Constructs a new Game instance with the specified players, bots, spectator, evaluator and game configuration.
     * @param players List of players to join in the game
     * @param bots List of bots to join in the game
     * @param spectator Spectator to join in the game. This can be null if there is no spectator.
     * @param config Game configuration settings for the game, including blinds, initial money, and other parameters.
     * @throws EvaluatorException if there is an error initializing the poker hand evaluator.
     */
    public Game(
        List<ClientStruct> players, 
        List<BotStruct> bots, 
        Spectator spectator, 
        GameConfig config
    ) throws EvaluatorException {

        // Initialize history
        _matchId = String.format("%s_%03d", LocalDateTime.now().format(FORMAT), NEXT_MATCH_ID.getAndIncrement());
        PokerHistory.startMatch(_matchId);

        // Initial state
        _gameConfig = config;
        String[] parts = config._blindsValue.split("/");
        _currentStreet = PokerStreet.PREFLOP;
        _initialSmallBlind = Integer.parseInt(parts[0]);    // TODO : Controlar errores de formato
        _initialBigBlind = Integer.parseInt(parts[1]);      // TODO : Controlar errores de formato
        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;
        _hikePercentage =  Float.parseFloat( config._hikePercentage ) / 100;
        _level = 1;

        // Player list
        _playerList = new PlayerList(config.getTotalPlayers());
        addAllPlayersInitial(players, bots, spectator, config);

        // Deck and table cards
        _deck = new Deck( selectSeed_DEBUG(players, bots) );
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _tableCardsCounter = 0;
        _isPreflop = true;

        // Static/dynamic blinds
        if(config._dynamicBlinds) {
            int seconds = Integer.parseInt( config._levelDuration );
            _timer = new Timer(seconds * 60);
            log.debug("Dynamic blinds enabled! Level duration: {} seconds", seconds);
        }
        
        // Instanciate poker-hand evaluator
        Evaluator.getInstance();
    }

    
    /**
     * Initializes the game by setting up the player list and preparing the game state for play.
     * @throws CancelGameException if there is an error during the initialization process, such as issues with player setup or game configuration.
     * @see PlayerList#initialize() for the actual implementation of player list initialization.
     */
    public void initialize() throws CancelGameException {
        _playerList.initialize();
    }

    /**
     * Delegates the assignment of roles to all players in the game.
     * @throws CancelGameException if there is an error during the role assignment process, such as issues with player setup or game configuration.
     * @see PlayerList#assignRolesToAllPlayers() for the actual implementation of role assignment.
     */
    public void assignRolesToAllPlayers() throws CancelGameException {
        _playerList.assignRolesToAllPlayers();
    }

    /**
     * Delegates the sharing of cards to all players in the game.
     * @throws CancelGameException if there is an error during the card sharing process, such as issues with the deck or player setup.
     * @see PlayerList#shareOutCardsToSomePlayer(Card, Card) for the actual implementation of card sharing.
     */
    public void shareOutCardsToAllPlayers() throws CancelGameException {
        
        for (int i = 0; i < _playerList.size(); i++) {
            Card randomCard1 = _deck.takeRandomCard();
            Card randomCard2 = _deck.takeRandomCard();
            _playerList.shareOutCardsToSomePlayer(randomCard1, randomCard2);
        }
    }

    /**
     * Adds a random card from the deck to the table, updating the game state and notifying all players of the new card.
     * Also notifies the game history of the card addition and prints the current table cards for debugging purposes.
     * @throws CancelGameException if there is an error during the card addition process, such as issues with the deck or player setup.
     * @see PlayerList#sendTableCardToAllPlayers(Card) for the actual implementation of notifying players about the new table card.
     */
    public void addCardToTable() throws CancelGameException {

        if (_tableCardsCounter >= MAX_CARDS_IN_TABLE)
            return;


        // Take a random card from the Deck
        Card c = _deck.takeRandomCard();
        _tableCards[_tableCardsCounter] = c;
        _tableCardsCounter++;

        // Put card on the table and notify all players
        _playerList.sendTableCardToAllPlayers(c);

        // Display table cards via console for debug purpose
        printTableCards();
    }

    /**
     * Plays a hand of poker, managing the game flow through the different streets (pre-flop, flop, turn, river).
     * It handles the betting rounds, card dealing, and updates the game history accordingly.
     * This also manages the dynamic blind increases if enabled in the game configuration and the history of the game.
     * @throws OnlyOnePlayerLeftException if all players except one have folded, indicating that the hand has ended prematurely.
     * @throws CancelGameException if there is an error during the hand play process, such as issues with player actions or game state management.
     */
    public void playHand() throws OnlyOnePlayerLeftException, CancelGameException {

        try {

            if(_isPreflop && _gameConfig._dynamicBlinds) {

                if( !_timer.isRunning() ) {

                    // Start timer and increase blinds only if it is enabled and it is NOT the first hand
                    if(_firstHand) {
                        _firstHand = false;
                        _timer.start();
                    }
                    else {
                        increaseBlinds();
                        _timer.restart();
                    }
                }
            }

            // Calculate current street
            _currentStreet = (_isPreflop) ? PokerStreet.PREFLOP : PokerStreet.nextRound(_currentStreet);
            
            // Update history
            if(_currentStreet == PokerStreet.PREFLOP) {
                initializeExperimentData();
                _history = new PokerHistory(this, _handCounter);
                PokerHistory.set(_history);
                _history.startHand();
            }
            else if (_currentStreet == PokerStreet.FLOP) {
                _history.flop( _tableCards );
            }
            else if (_currentStreet == PokerStreet.TURN) {
                _history.turn( _tableCards );
            }
            else if (_currentStreet == PokerStreet.RIVER) {
                _history.river( _tableCards );
            }

            // Play hand
            _playerList.playHand(_currentSB, _currentBB, _currentStreet);
            _isPreflop = false;
        }
        // Collect remaining bets only if the round ended because all players folded
        catch (OnlyOnePlayerLeftException e) {
            _isPreflop = false;
            throw e;
        }
    }

    /**
     * Handles the showdown phase of the poker game, where players reveal their hands and the winner is determined.
     * Delegates the calculation of the hand winner, distribution of the prize, and management of eliminated players.
     * This also updates the game history and waits for a specified duration to allow players to view the winner before proceeding.
     * @throws CancelGameException if there is an error during the showdown process, such as issues with hand evaluation or prize distribution.
     * @see PlayerList#calculatePrizeDistribution(List) for the actual implementation of prize distribution.
     * @see PlayerList#manageEliminatedPlayers() for the actual implementation of managing eliminated players.
     */
    public void showdown() throws CancelGameException {

        // Game history
        _history.showdown();

        // Notify to all players about the other player cards
        _playerList.broadcastAllPlayerCards();

        // Calculate hand winner. If there is only a player left, give him the prize
        List<HandInfo> playersHands = _playerList.getPlayerHandsInfo();
        if(playersHands.size() == 1) {
            _playerList.calculatePrizeForPlayerLeft();
        }
        else {

            try {
                List<PlayerEvaluation> playersEval = Evaluator.getInstance().evaluateAllHands(playersHands, _tableCards);
                _playerList.calculatePrizeDistribution(playersEval);
            }
            catch (EvaluatorException e) {
                throw new CancelGameException(e.getMessage());
            }
        }
        _playerList.manageEliminatedPlayers();
        ++_handCounter;

        // Game history
        finishExperimentData();

        
        // Wait to display player cards for the user
        try {
            System.out.printf("%d seconds pause to see the winner...\n", GameType.SHOWDOWN_WAIT_TIME_SEC);
            Thread.sleep(GameType.SHOWDOWN_WAIT_TIME_SEC * 1000);
        }
        catch (InterruptedException e) {}
    }

    /**
     * Delegates the passing of the turn to the next player in the game, managing the transition between betting rounds and updating the game state accordingly.
     * It also handles the end of the game if all players have completed their actions, and resets the game state for the next round if the game continues.
     * @return true if the game has ended, false if the game continues to the next round.
     * @throws CancelGameException if there is an error during the turn passing process, such as issues with player actions or game state management.
     * @see PlayerList#checkEndOfGame() for the actual implementation of checking if the game has ended.
     * @see PlayerList#notifyGameEnds(boolean) for the actual implementation of notifying players about the end of the game.
     * @see PlayerList#passTurn() for the actual implementation of passing the turn to the next player.
     */
    public boolean passTurn() throws CancelGameException {

        // Game history
        _history.summary( _tableCards );
        _history.experimentData();
        _history.endHand();
        PokerHistory.clear(); 

        // Pass turn for the next round
        boolean endOfGame = _playerList.checkEndOfGame();
        _playerList.notifyGameEnds(endOfGame);

        if(!endOfGame) {
            retrieveCardsFromTable();
            _deck.resetDeck();
            _playerList.passTurn();
            _isPreflop = true;
        }
        else {
            PokerHistory.endMatch();    // End game history
        }

        return endOfGame;
    }

    /**
     * Updates the equity for all players in the game based on their current hands and the community cards on the table.
     * It calculates the equity using the EquityCalculator and notifies each player of their respective equity
     * @throws CancelGameException if there is an error during the equity calculation process, such as issues with player hands or game state management.
     * @see EquityCalculator#calculateEquity(List, Card[], Deck) for the actual implementation of equity calculation.
     * @see PlayerList#notifyEquityToPlayers(Map) for the actual implementation of notifying players about their equity.
     */
    public void updateEquity() throws CancelGameException {

        List<HandInfo> players = _playerList.getPlayerHandsInfo();
        if (players.size() <= 1)
            return; 


        Map<Integer, Double> equity = EquityCalculator.calculateEquity(players, _tableCards, _deck);
        _playerList.notifyEquityToPlayers(equity);

        PokerHistory history = PokerHistory.current();
        if (history != null)
            history.equity(equity);
    }

    /**
     * Initializes the history data for all players in the game.
     */
    private void initializeExperimentData() {

        for(Player p : _playerList.getPlayers()) {

            p.getExperimentData().reset();

            p.getExperimentData().setInitialStack(
                p.getMoneyOffBet()
            );
        }
    }

    /**
     * Finishes the experiment data for all players in the game.
     */
    private void finishExperimentData() {

        for(Player p : _playerList.getPlayers()) {

            PlayerExperimentData e = p.getExperimentData();

            e.setFinalStack(
                p.getMoneyOffBet()
            );

            e.setWonHand(
                p.isWinner()
            );

            e.setNetChips(
                e.getFinalStack() - e.getInitialStack()
            );
        }
    }


    
    /**
     * Prints the current cards on the table to the debug log for diagnostic purposes.
     */
    private void printTableCards() {

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < MAX_CARDS_IN_TABLE; i++) {

            if(_tableCards[i] == null)
                sb.append( Card.FlippedDownCardToString() ).append(" ");
            else
                sb.append( _tableCards[i].toString() ).append(" ");
        }

        log.debug("Table cards: {}", sb.toString());
    }

    /**
     * Increases the small and big blinds based on the current level and the hike percentage defined in the game configuration.
     */
    private void increaseBlinds() {

        double increase = Math.pow( (1 + _hikePercentage) , _level);
        double newSB = _currentSB * increase;
        ++_level;

        _currentSB = (int)Math.round(newSB);
        _currentBB = _currentSB * 2;

        log.debug("Blinds will increase to {}/{}", _currentSB, _currentBB);
    }

    /**
     * Adds all players to the game at the beginning of the match.
     * @param players the list of client players
     * @param bots the list of bot players
     * @param spectator the spectator player
     * @param config the game configuration
     */
    private void addAllPlayersInitial(List<ClientStruct> players, List<BotStruct> bots, Spectator spectator, GameConfig config) {

        for(ClientStruct cs : players) {

            HumanPlayer hp = new HumanPlayer(cs.socket());
            Player p = new Player(cs.playerID(), cs.name(), config._initialMoney, hp);
            
            if( cs.isHost() )
                _playerList.assignHost(p);

            _playerList.addPlayer( p );
        }

        for(BotStruct bs : bots) {
            
            Bot bot = BotManager.createBot( bs.botId() );
            if(bot != null) {
                Bot specificBot = bot.create(bs.style());
                String botName = String.format("%s#%d", bs.botName(), bs.matchId());
                _playerList.addPlayer( new Player(bs.matchId(), botName, config._initialMoney, specificBot) );
            }
            else {
                log.error("Bot with ID {} could not be found! Ignoring request", bs.botId());
            }
        }
    
        if(spectator._spectatorSocket != null) {
            spectator = new Spectator(spectator._spectatorSocket);  // Important !!
            _playerList.addSpectator(spectator);
        }
    }

    /**
     * Retrieves all cards from the table and returns them to the deck, resetting the table state for the next hand.
     * This method is called at the end of a hand to ensure that the deck is replenished and ready for the next round of play.
     */
    private void retrieveCardsFromTable() {

        for (int i = 0; i < _tableCardsCounter; i++){
            _deck.retrieveCard( _tableCards[i] );
            _tableCards[i] = null;
        }

        _tableCardsCounter = 0;
    }

    /**
     * Selects a seed for the deck based on the number of players and bots in the game.
     * This method is used for debugging purposes to ensure consistent card distribution in specific scenarios.
     * @param players the list of client players
     * @param bots the list of bot players
     * @return the selected seed
     */
    private long selectSeed_DEBUG(final List<ClientStruct> players, final List<BotStruct> bots) {

        long seed = 1;
        if(players.size()  == 1 && bots.size() == 1) {          // Heads-up(1vs1)       - Prueba 1

            BotStyle style = bots.get(0).style();
            switch (style) {
                case BotStyle.MANIAC:
                    seed = 60;
                    break;

                case BotStyle.LOOSE_AGGRESSIVE:
                    seed = 50;
                    break;

                case BotStyle.LOOSE_PASSIVE:
                    seed = 40;
                    break;

                case BotStyle.TIGHT_AGGRESSIVE:
                    seed = 30;
                    break;

                case BotStyle.TIGHT_PASSIVE:
                    seed = 20;
                    break;

                default: // Default
                    seed = 10; 
                    break;
            }

        }
        else if (players.size() == 1 && bots.size() == 4) {     // 1 humano vs 4 bots   - Prueba 2
            seed = 1777;
        }
        else if (players.size() == 0 && bots.size() == 6) {     // 6 bots               - Prueba 3
            seed = 2732;
        }
        else if(players.size() == 0 && bots.size() == 2) {      // 1 bot vs 1 bot       - Prueba 4
            seed = 3387;
        }
        else {
            Random rand = new Random();
            seed = rand.nextLong();
        }

        log.debug("Selected seed for the deck: {}", seed);
        return seed;
    }


    /**
     * Returns the current small blind value for the game.
     * @return the current small blind value
     */
    public int getCurrentSmallBlind() { return _currentSB; }

    /**
     * Returns the current big blind value for the game.
     * @return the current big blind value
     */
    public int getCurrentBigBlind() { return _currentBB; }

    /**
     * Returns the current level of the game, which is used to determine the blind values and other game parameters.
     * @return the current level of the game
     */
    public PlayerList getPlayerList() { return _playerList; }

}