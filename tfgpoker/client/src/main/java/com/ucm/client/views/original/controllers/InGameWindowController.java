package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.ucm.common.SocketUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;


public class InGameWindowController extends GenericController {

    

    @Override
    protected void onViewShown() {

        Thread gameThread = new Thread(() -> {
            pokerGame(_clientInfo.name, _clientInfo.socket);
        });
        gameThread.start();
    }

    private void pokerGame(String name, Socket socket) {

        try {

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            int role = SocketUtils.receiveInt(input);
            System.out.printf("Role received: %d\n", role);

        }
        catch(IOException e) {
            System.out.printf("Error on game: %s\n", e.getMessage());
        }
    }

    @Override
    public void onNextEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onNextEvent'");
    }

    @Override
    public void onBackEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onBackEvent'");
    }
    
}
