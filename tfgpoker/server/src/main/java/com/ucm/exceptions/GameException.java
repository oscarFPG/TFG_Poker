package com.ucm.exceptions;


public class GameException extends Exception {

    public GameException(String specificCause){
        super(specificCause);
    }

}
