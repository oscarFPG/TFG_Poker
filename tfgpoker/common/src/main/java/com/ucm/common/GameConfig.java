package com.ucm.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the configuration of a poker game before it is created or joined.
 * <p>
 * This class contains all settings selected by the user during the game
 * creation workflow, including room information, blind structure, bot
 * configuration, player capacity, visual customization options, and spectator
 * settings.
 * </p>
 * <p>
 * A {@code GameConfig} instance can also be cloned using the copy constructor
 * to safely preserve a snapshot of the current configuration state.
 * </p>
 */
public class GameConfig {
    
    private static final int DEFAULT_INITIAL_MONEY = 100;
    private static final boolean DEFAULT_ALLOW_BOTS = true;
    private static final boolean DEFAULT_JOIN_AS_SPECTATOR = false;
    private static final String DEFAULT_BLINDS_VALUE = "1/2";
    private static final boolean DEFAULT_DYNAMIC_VALUE = false;
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
    public boolean _dynamicBlinds = DEFAULT_DYNAMIC_VALUE;
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

    /**
     * Default constructor for GameConfig. Initializes the configuration with default values.
     */
    public GameConfig() {}

    /**
     * Copy constructor for GameConfig. Creates a new instance by copying the values from another GameConfig instance.
     * @param other config instance to copy values from
     */
    public GameConfig(GameConfig other) {

        // Mode configuration
        _roomName = (other._roomName != null) ? String.copyValueOf(other._roomName.toCharArray()) : null;
        _userName = (other._userName != null) ? String.copyValueOf(other._userName.toCharArray()) : null;
        _roomId = other._roomId;
        _initialMoney = other._initialMoney;
        _allowBots = other._allowBots;
        _blindsValue = (other._blindsValue != null) ? String.copyValueOf(other._blindsValue.toCharArray()) : null;

        // Dymaic/static blinds
        _dynamicBlinds = other._dynamicBlinds;
        _levelDuration = (other._levelDuration != null) ? String.copyValueOf(other._levelDuration.toCharArray()) : null;
        _hikePercentage = (other._hikePercentage != null) ? String.copyValueOf(other._hikePercentage.toCharArray()) : null;

        // Bots and other players
        _botsByType = new HashMap<>(other._botsByType);
        _botStylesByType = new HashMap<>(other._botStylesByType);
        _numPlayers = other._numPlayers;

        // Visuals
        _selectedTable = (other._selectedTable != null) ? String.copyValueOf(other._selectedTable.toCharArray()) : null;
        _selectedCard = (other._selectedCard != null) ? String.copyValueOf(other._selectedCard.toCharArray()) : null;

        // Spectator
        _joinedAsSpectator = other._joinedAsSpectator;
    }

    /**
     * Validates if the provided room name is valid (not null and not empty).
     * @param roomName
     * @return true if the room name is valid, false otherwise
     */
    public static boolean isValidRoomName(String roomName){
        if(roomName == null) { return false; }
        return !roomName.trim().isEmpty();
    }
    /**
     * Resets the game configuration to its default values.
     * <p>
     * This method clears all settings and restores the initial state of the configuration.
     * </p>
     */
    public void reset() {
        _initialMoney = DEFAULT_INITIAL_MONEY;
        _allowBots = DEFAULT_ALLOW_BOTS;
        _blindsValue = DEFAULT_BLINDS_VALUE;
        _dynamicBlinds = DEFAULT_DYNAMIC_VALUE;
        _levelDuration = DEFAULT_LEVEL_DURATION;
        _hikePercentage = DEFAULT_HIKE_PERCENTAGE;
        _botsByType.clear();
        _botStylesByType.clear();
        _numPlayers = DEFAULT_NUM_PLAYERS;
        _selectedTable = null;
        _selectedCard = null;
    }
    /**
     * Sets the number of bots of a specific type.
     * <p>
     * If the count is less than or equal to zero, the bot type and its style
     * configuration are removed from the configuration.
     * </p>
     * 
     * @param botId
     * @param count
     */
    public void setBotCount(int botId, int count) {
        if(count <= 0) {
            _botsByType.remove(botId);
            _botStylesByType.remove(botId);
        }
        else {
            _botsByType.put(botId, count);
        }
    }
    /**
     * Returns the configured number of bots of a specific type.
     * @param botId identifier of the bot type
     * @return the number of configured bots, or {@code 0} if none exist
     */
    public int getBotCount(int botId) {
        return _botsByType.getOrDefault(botId, 0);
    }
    /**
     * Sets the number of bots assigned to a specific style.
     * <p>
     * If the count is less than or equal to zero, the style assignment is
     * removed. If the style map becomes empty, the corresponding bot type
     * entry is also removed.
     * </p>
     * 
     * @param botId identifier of the bot type
     * @param style bot styñe to configure
     * @param count number of bots assigned to the style
     */
    public void setBotStyleCount(int botId, BotStyle style,int count) {
        
        _botStylesByType.computeIfAbsent(botId, k -> new HashMap<>());

        if(count <= 0) {
            Map<BotStyle,Integer> styles = _botStylesByType.get(botId);
            if(styles != null){
                styles.remove(style);
                if(styles.isEmpty()){
                    _botStylesByType.remove(botId);
                }
            }
        }
        else {
            _botStylesByType.get(botId).put(style, count);
        }
    }
    /**
     * Returns the number of bots assigned to a specific style for a given bot type.
     * @param botId identifier of the bot type
     * @param style bot style to query
     * @return number of bots that use the specified style, or {@code 0} if none exist
     */
    public int getBotStyleCount(int botId, BotStyle style){
        return _botStylesByType.getOrDefault(botId, Map.of()).getOrDefault(style, 0);
    }
    /**
     * Calculates the total number of bots configured across all bot types.
     * @return the total number of bots
     */
    private int getTotalBots() {
        return _botsByType.values().stream().mapToInt(Integer::intValue).sum();
    }
    /**
     * Calculates the total number of players in the game, including both human players and bots.
     * @return the total number of players in the game
     */
    public int getTotalPlayers() {
        return _numPlayers + getTotalBots();
    }
}

