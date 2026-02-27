package com.ucm.server.exceptions;


public class GameException extends Exception {

    public GameException(String specificCause){
        super(specificCause);
    }

}
