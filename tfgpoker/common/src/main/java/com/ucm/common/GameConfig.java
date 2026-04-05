package com.ucm.common;

public class GameConfig {
    
    /**
     * Variables para crear partida: Home
    */
    public String _roomName;
    public int _roomId;
    public int _initialMoney;
    public boolean _allowBots;
    
    
    public static int checkRoomName(String roomName){

        if(roomName.length() < 3) {
            return GameType.ERROR_NAME_TOO_SHORT;
        }
        else if(10 < roomName.length()) {
            return GameType.ERROR_NAME_TOO_LONG;
        }
        else {
            return GameType.CONFIRMATION_NAME_VALID;
        }
    }

    public static boolean isValidRoomName(String roomName){
        return (roomName.length() >= 3 && roomName.length() <= 10);
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
