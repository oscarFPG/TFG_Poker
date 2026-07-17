package com.ucm.common;

import java.util.HashMap;
import java.util.Map;

public class GameConfig {
    
    private static final int DEFAULT_INITIAL_MONEY = 100;
    private static final boolean DEFAULT_ALLOW_BOTS = true;
    private static final boolean DEFAULT_JOIN_AS_SPECTATOR = false;
    private static final String DEFAULT_BLINDS_VALUE = "1/2";
    private static final boolean DEFAULT_DINAMIC_VALUE = false;
    private static final String DEFAULT_LEVEL_DURATION = "15";
    private static final String DEFAULT_HIKE_PERCENTAGE = "25";
    private static final int DEFAULT_NUM_BOTS = 0;
    private static final int DEFAULT_NUM_PLAYERS = 8;
    private static final int DEFAULT_NUM_PLAYERS_WITH_SPECTATOR = 9;
    private static final String DEFAULT_TURN_TIMER = "60";

    /**
     * Variables para crear partida: Home
     */
    public String _roomName;
    public String _userName;
    public int _roomId;
    public int _initialMoney = DEFAULT_INITIAL_MONEY;
    public boolean _allowBots = DEFAULT_ALLOW_BOTS;
    public String _blindsValue = DEFAULT_BLINDS_VALUE;
    public boolean _dinamicBlinds = DEFAULT_DINAMIC_VALUE;
    public String _levelDuration = DEFAULT_LEVEL_DURATION;
    public String _hikePercentage = DEFAULT_HIKE_PERCENTAGE;

    /**
     * Variables para crear partida: Add bots
    */
    public Map<Integer, Integer> _botsByType = new HashMap<>();
    public Map<Integer, Map<BotStyle, Integer>> _botStylesByType =  new HashMap<>();

    /**
     * Variables para crear partida: Add players
     */
    public int _numPlayers = DEFAULT_NUM_PLAYERS;

    /**
     * Variables para crear partida: Add tables
     */
    public String _selectedTable = null;
    public String _selectedCard = null;

    /**
     * Variable para determinar el tiempo de jugada de cada jugador
    */
    public String _turnTimerPlayer = DEFAULT_TURN_TIMER;

    /**
     * Indicated if there is an spectator or not
     */
    public boolean _joinedAsSpectator = DEFAULT_JOIN_AS_SPECTATOR;

    public GameConfig() {}

    // Deep-copy constructor
    public GameConfig(GameConfig other) {
        _roomName =  (other._roomName != null) ? String.copyValueOf(other._roomName.toCharArray()) : null;
        _userName =  (other._userName != null) ? String.copyValueOf(other._userName.toCharArray()) : null;
        _roomId = other._roomId;
        _initialMoney = other._initialMoney;
        _allowBots = other._allowBots;
        _blindsValue = (other._blindsValue != null) ? String.copyValueOf(other._blindsValue.toCharArray()) : null;
        _levelDuration = (other._levelDuration != null) ? String.copyValueOf(other._levelDuration.toCharArray()) : null;
        _hikePercentage = (other._hikePercentage != null) ? String.copyValueOf(other._hikePercentage.toCharArray()) : null;
        _botsByType = new HashMap<>(other._botsByType);
        _botStylesByType = new HashMap<>();
        for(var entry : other._botStylesByType.entrySet()){
            _botStylesByType.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        _numPlayers = other._numPlayers;
        _selectedTable = (other._selectedTable != null) ? String.copyValueOf(other._selectedTable.toCharArray()) : null;
        _selectedCard = (other._selectedCard != null) ? String.copyValueOf(other._selectedCard.toCharArray()) : null;
    }

    public static boolean isValidRoomName(String roomName){
        if(roomName == null) { return false; }
        return !roomName.trim().isEmpty();
    }

    public void reset() {
        _initialMoney = DEFAULT_INITIAL_MONEY;
        _allowBots = DEFAULT_ALLOW_BOTS;
        _blindsValue = DEFAULT_BLINDS_VALUE;
        _dinamicBlinds = DEFAULT_DINAMIC_VALUE;
        _levelDuration = DEFAULT_LEVEL_DURATION;
        _hikePercentage = DEFAULT_HIKE_PERCENTAGE;
        _botsByType.clear();
        _botStylesByType.clear();
        _numPlayers = DEFAULT_NUM_PLAYERS;
        _selectedTable = null;
        _selectedCard = null;
    }

    public void setBotCount(int botId, int count) {
        if(count <= 0) {
            _botsByType.remove(botId);
            _botStylesByType.remove(botId);
        }
        else {
            _botsByType.put(botId, count);
        }
    }

    public int getBotCount(int botId) {
        return _botsByType.getOrDefault(botId, 0);
    }

    public void setBotStyleCount(int botId, BotStyle style,int count){
        _botStylesByType.computeIfAbsent(botId, k -> new HashMap<>());

        if(count <= 0) {
            Map<BotStyle,Integer> styles = _botStylesByType.get(botId);
            if(styles != null){
                styles.remove(style);
            }
        }
        else {
            _botStylesByType.get(botId).put(style, count);
        }
    }

    public int getBotSyleCount(int botId, BotStyle style){
        return _botStylesByType.getOrDefault(botId, Map.of()).getOrDefault(style, 0);
    }

    private int getTotalBots() {
        return _botsByType.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getTotalPlayers() {
        return _numPlayers + getTotalBots();
    }

}