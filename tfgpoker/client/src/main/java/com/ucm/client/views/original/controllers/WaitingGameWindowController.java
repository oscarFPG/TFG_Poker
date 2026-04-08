package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ucm.client.ClientInfo;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


public class WaitingGameWindowController extends GenericController {

    @FXML
    private Button startButton;

    @FXML
    private Label playerName0, playerMoney0;

    @FXML
    private Label playerName1, playerMoney1;

    @FXML
    private Label playerName2, playerMoney2;

    @FXML
    private Label playerName3, playerMoney3;

    @FXML
    private Label playerName4, playerMoney4;

    @FXML
    private Label playerName5, playerMoney5;

    @FXML
    private Label playerName6, playerMoney6;

    @FXML
    private Label playerName7, playerMoney7;

    @FXML
    private Label playerName8, playerMoney8;


    @FXML
    private void startGame() {
        
        try {
            SocketUtils.sendInteger(_clientInfo.socket.getOutputStream(), GameType.EVENT_GAME_STARTS);
        }
        catch(IOException e) {

        } 
    }


    @Override
    protected void onViewShown() {

        playerName0.setText( _clientInfo.name );
        playerMoney0.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );

        Thread infoThread = new Thread(() -> {
            waitNewPlayersInfo();
        });
        infoThread.start();
    }

    @Override
    public void onNextEvent() {
        
    }

    @Override
    public void onBackEvent() {
       
    }


    private void waitNewPlayersInfo() {

        try {

            InputStream input = _clientInfo.socket.getInputStream();
            OutputStream output = _clientInfo.socket.getOutputStream();

            _clientInfo.id = SocketUtils.receiveInt(input);
            System.out.printf("Player ID is %d\n", _clientInfo.id);

            if(_clientInfo.isHost) {
                System.out.printf("Server response: This client is the host of the game!\n");
            }
            else {
                System.out.printf("Server response: This client is a guest!\n");

                Platform.runLater(() -> {
                    startButton.setVisible(false);
                });
            }

            boolean kepWaiting = true;
            while(kepWaiting) {

                int event = SocketUtils.receiveInt(input);
                if(event == GameType.EVENT_PLAYER_JOINED) {

                    PlayerInfo p = PokerPreGame.receivePlayerInRoomInfo(input, output);
                    System.out.printf(
                        "Event PLAYER_JOINED! Player %s with ID %d has joined the game!\n", 
                        p.name, 
                        p.id
                    );

                    Platform.runLater(() -> {
                        showPlayer(p.id, p.name);
                    });
                }
                else if(event == GameType.CONFIRMATION_GAME_STARTS) {
                    System.out.printf("Event GAME_STARTS!\n");
                    kepWaiting = false;
                }
                else if(event == GameType.ERROR_GAME_CANNOT_START) {
                    System.out.printf("Game cannot start! Missing players\n");
                }
                else {
                    System.out.printf("Event %d unknown!\n", event);
                }
            }
            System.out.printf("Game has to start!\n");

            SocketUtils.sendInteger(output, GameType.CONFIRMATION_PLAYER_STARTS);

            Platform.runLater(() -> {
                next();
            });
        }
        catch(Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }
    }

    private void showPlayer(final int ID, final String name) {

        if(ID == 0) {
            playerName0.setText(name);
            playerMoney0.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 1) {
            playerName1.setText(name);
            playerMoney1.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 2) {
            playerName2.setText(name);
            playerMoney2.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 3) {
            playerName3.setText(name);
            playerMoney3.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 4) {
            playerName4.setText(name);
            playerMoney4.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 5) {
            playerName5.setText(name);
            playerMoney5.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 6) {
            playerName6.setText(name);
            playerMoney6.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 7) {
            playerName7.setText(name);
            playerMoney7.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }
        else if(ID == 8) {
            playerName8.setText(name);
            playerMoney8.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );
        }

    }

}