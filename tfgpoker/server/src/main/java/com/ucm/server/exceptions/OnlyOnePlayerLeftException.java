package com.ucm.server.exceptions;


public class OnlyOnePlayerLeftException extends GameException {


    public OnlyOnePlayerLeftException(String message) {
        super(message);
    }

}