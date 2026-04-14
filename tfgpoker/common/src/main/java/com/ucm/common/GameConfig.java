package com.ucm.common;

public class GameConfig {
    
    private static final int DEFAULT_INITIAL_MONEY = 100;
    private static final boolean DEFAULT_ALLOW_BOTS = true;
    private static final String DEFAULT_BLINDS_VALUE = "1/2";
    private static final boolean DEFAULT_DINAMIC_VALUE = false;
    private static final String DEFAULT_LEVEL_DURATION = "15";
    private static final String DEFAULT_HIKE_PERCENTAGE = "25";
    private static final int DEFAULT_NUM_BOTS = 0;
    private static final int DEFAULT_NUM_PLAYERS = 8;
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
    public int _numBots1 = DEFAULT_NUM_BOTS;
    public int _numBots2 = DEFAULT_NUM_BOTS;
    /**
     * Variables para crear partida: Add players
    */
    public int _numPlayers = DEFAULT_NUM_PLAYERS;
    /**
     * Variables para crear partida: Add tables
    */
    public String _selectedTable = null;
    /**
     * Variables para crear partida: Add tables
    */
    public String _selectedCard = null;

    public GameConfig() {}

    // Deep-copy constructor
    public GameConfig(GameConfig other) {
        _roomName =  (other._roomName != null) ? String.copyValueOf(other._roomName.toCharArray()) : null;
        _userName =  (other._userName != null) ? String.copyValueOf(other._userName.toCharArray()) : null;
        _roomId = other._roomId;
        _initialMoney = other._initialMoney;
        _allowBots = other._allowBots;
        _dinamicBlinds = other._dinamicBlinds;
        _blindsValue = (other._blindsValue != null) ? String.copyValueOf(other._blindsValue.toCharArray()) : null;
        _levelDuration = (other._levelDuration != null) ? String.copyValueOf(other._levelDuration.toCharArray()) : null;
        _hikePercentage = (other._hikePercentage != null) ? String.copyValueOf(other._hikePercentage.toCharArray()) : null;
        _numBots1 = other._numBots1;
        _numBots2 = other._numBots2;
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
        _numBots1 = DEFAULT_NUM_BOTS;
        _numBots2 = DEFAULT_NUM_BOTS;
        _numPlayers = DEFAULT_NUM_PLAYERS;
        _selectedTable = null;
        _selectedCard = null;
    }

    public int getTotalPlayers() {
        return _numPlayers + _numBots1 + _numBots2;
    }

}