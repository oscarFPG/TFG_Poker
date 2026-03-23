package com.ucm.client.exceptions;

public class OnlyOnePlayerLeftException extends Exception {
 
    public OnlyOnePlayerLeftException() {
        super("There is only one player left on the current hand");
    }    

}