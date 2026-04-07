package com.ucm.common;

public class GameConfig {
    
    private static final int DEFAULT_INITIAL_MONEY = 100;
    private static final String DEFAULT_BLINDS_VALUE = "1/2";
    private static final String DEFAULT_LEVEL_DURATION = "15";
    private static final String DEFAULT_HIKE_PERCENTAGE = "25";

    /**
     * Variables para crear partida: Home
    */
    public String _roomName;
    public String _userName;
    public int _roomId;
    public int _initialMoney = DEFAULT_INITIAL_MONEY;
    public boolean _allowBots = true;
    public String _blindsValue = DEFAULT_BLINDS_VALUE;
    public String _levelDuration = DEFAULT_LEVEL_DURATION;
    public String _hikePercentage = DEFAULT_HIKE_PERCENTAGE;
    

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
    }

    public static boolean isValidRoomName(String roomName){
        if(roomName == null) { return false; }
        return !roomName.trim().isEmpty();
    }

    /**
     * Variables para crear partida: Add bots
    */
    
}