package com.ucm.common;

public class GameConfig {
    
    private static final int DEFAULT_INITIAL_MONEY = 100;
    private static final boolean DEFAULT_ALLOW_BOTS = true;
    private static final String DEFAULT_BLINDS_VALUE = "1/2";
    private static final boolean DEFAULT_DINAMIC_VALUE = false;
    private static final String DEFAULT_LEVEL_DURATION = "15";
    private static final String DEFAULT_HIKE_PERCENTAGE = "25";
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
    
    public static boolean isValidRoomName(String roomName){
        if(roomName == null) { return false; }
        return !roomName.trim().isEmpty();
    }

    /**
     * Variables para crear partida: Add bots
    */

    /*
        - Por defecto es estatico (1 y 2)
        - Dinamicas
            - Decidir valor de small y big -> menu desplegable
            - Cada cuanto aumentan (minutos) -> menu desplegable
            - Que factor de aumento (0%, 200%) -> menu desplegable
    */
    

}
