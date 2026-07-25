package com.ucm.server.logic;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ucm.common.BotStruct;
import com.ucm.common.ClientStruct;
import com.ucm.common.GameConfig;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.gameobjects.Card;
import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.exceptions.EvaluatorException;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.Deck;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.managers.BotManager;
import com.ucm.server.middleclasses.HandInfo;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.Spectator;
import com.ucm.server.players.HumanPlayer;
import com.ucm.server.statistics.EquityCalculator;



public class Game {

    private static final Logger log = LogManager.getLogger(Game.class);
    private static final AtomicInteger NEXT_MATCH_ID = new AtomicInteger(1);
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private final String _matchId;
    

    public static final boolean DEBUG = true;
    public static final boolean DEBUG_PLAYERS = false;
    public static final int MAX_CARDS_IN_TABLE = 5;

    private GameConfig _gameConfig;
    private int _initialSmallBlind;
    private int _initialBigBlind;
    private int _currentSB;
    private int _currentBB;
    private float _hikePercentage;

    private PlayerList _playerList;
    private Deck _deck;
    private Card[] _tableCards;
    private int _tableCardsCounter;
    private boolean _isPreflop;

    private Timer _timer;
    private int _level;
    private int _handCounter = 0;
    private boolean _firstHand = true;

    public Game(
        List<ClientStruct> players, 
        List<BotStruct> bots, 
        Spectator spectator, 
        GameConfig config
    ) throws EvaluatorException {

       _matchId = String.format("%s_%03d",LocalDateTime.now().format(FORMAT), NEXT_MATCH_ID.getAndIncrement());

        _gameConfig = config;

        String[] parts = config._blindsValue.split("/");
        _initialSmallBlind = Integer.parseInt(parts[0]);    // TODO : Controlar errores de formato
        _initialBigBlind = Integer.parseInt(parts[1]);      // TODO : Controlar errores de formato
        _hikePercentage =  Float.parseFloat( config._hikePercentage ) / 100;
        _currentSB = _initialSmallBlind;
        _currentBB = _initialBigBlind;

        _playerList = new PlayerList(config.getTotalPlayers());
        addAllPlayersInitial(players, bots, spectator, config);

        _deck = new Deck();
        _tableCards = new Card[MAX_CARDS_IN_TABLE];
        _tableCardsCounter = 0;
        _isPreflop = true;

        if(config._dinamicBlinds) {
            int seconds = Integer.parseInt( config._levelDuration );
            _timer = new Timer(seconds);
            log.debug("Dynamic blinds enabled! Level duration: {} seconds", seconds);
        }
        else {
            _timer = null;
        }
        _level = 1;

        Evaluator.getInstance();
    }

    

    public void assignRolesToAllPlayers() throws CancelGameException {
       _playerList.assignRolesToAllPlayers();
    }

    public void shareOutCardsToAllPlayers() throws CancelGameException {
        
        for (int i = 0; i < _playerList.size(); i++) {
            Card randomCard1 = _deck.takeRandomCard();
            Card randomCard2 = _deck.takeRandomCard();
            _playerList.shareOutCardsToSomePlayer(randomCard1, randomCard2);
        }
    }

    public void addCardToTable() throws CancelGameException {

        if (_tableCardsCounter >= 5)
            return;


        Card c = _deck.takeRandomCard();
        _tableCards[_tableCardsCounter] = c;
        _tableCardsCounter++;

        _playerList.sendTableCardToAllPlayers(c);

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

            if(_isPreflop && _gameConfig._dinamicBlinds) {

                if( !_timer.isRunning() ) {

                    // Start timer and increase blinds only if it is enabled and it is NOT the first hand
                    if(_firstHand) {
                        _firstHand = false;
                    }
                    else {
                        increaseBlinds();
                        _timer.restart();
                    }
                }
            }

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

    
    private void increaseBlinds() {

        double increase = Math.pow( (1 + _hikePercentage) , _level);
        double newSB = _currentSB * increase;
        ++_level;

        _currentSB = (int)Math.round(newSB);
        _currentBB = _currentSB * 2;

        log.debug("Blinds increased to {}/{}", _currentSB, _currentBB);
    }

    private void addAllPlayersInitial(List<ClientStruct> players, List<BotStruct> bots, Spectator spectator, GameConfig config) {

        int id = 0;
        for(ClientStruct cs : players) {

            HumanPlayer hp = new HumanPlayer(cs.socket());
            Player p = new Player(id, cs.name(), config._initialMoney, hp);
            
            if( cs.isHost() )
                _playerList.assignHost(p);

            _playerList.addPlayer( p );
            ++id;
        }

        for(BotStruct bs : bots) {
            
            Bot bot = BotManager.createBot( bs.botId() );
            if(bot != null) {
                Bot specificBot = bot.create(bs.style());
                String botName = String.format("%s#%d", bs.botName(), id);
                _playerList.addPlayer( new Player(id, botName, config._initialMoney, specificBot) );
                ++id;
            }
            else {
                log.error("Bot with ID {} could not be found! Ignoring request", bs.botId());
            }
        }
    
        if(spectator._socket != null) {
            _playerList.addSpectator(spectator);
        }
    }

    private void retrieveCardsFromTable() {

        for (int i = 0; i < _tableCardsCounter; i++){
            _deck.retrieveCard( _tableCards[i] );
            _tableCards[i] = null;
        }

        _tableCardsCounter = 0;
    }

    public void updateEquity() throws CancelGameException {

        List<HandInfo> players = _playerList.getPlayerHandsInfo();
        if (players.size() <= 1)
            return; 


        Map<Integer, Double> equity = EquityCalculator.calculateEquity(players, _tableCards, _deck);
        _playerList.notifyEquityToPlayers(equity);
    }

    public int getHandCounter() {
    return _handCounter;
    }

    public int getCurrentSmallBlind() {
        return _currentSB;
    }

    public int getCurrentBigBlind() {
        return _currentBB;
    }

    public PlayerList getPlayerList() {
        return _playerList;
    }

    public String getMatchId() {
        return _matchId;
    }

    public Card[] getTableCards() {
        return _tableCards;
    }
}

