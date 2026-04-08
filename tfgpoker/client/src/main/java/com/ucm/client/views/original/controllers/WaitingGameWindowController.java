package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
    private Label roomNamePlaceholder;

    @FXML
    private Label roomIdPlaceholder;

    // Always the client position
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


    private Thread _infoThread;


    @FXML
    private void startGame() {
        
        try {
            SocketUtils.sendInteger(_clientInfo.socket.getOutputStream(), GameType.EVENT_GAME_STARTS);
        }
        catch(IOException e) {
            System.out.printf("Error sending EVENT_GAME_STARTS to server: %s\n", e.getMessage());
        }
    }


    @Override
    protected void onViewShown() {

        clearAllLabels();

        playerName0.setText( _clientInfo.name );
        playerMoney0.setText( String.valueOf( _clientInfo.gameConfig._initialMoney ) );

        roomNamePlaceholder.setText( _clientInfo.gameConfig._roomName );
        roomIdPlaceholder.setText( String.valueOf( _clientInfo.gameConfig._roomId ) );

        _infoThread = new Thread(() -> {
            waitNewPlayersInfo();
        });
        _infoThread.start();
    
        _stage.setOnCloseRequest(event -> {

            System.out.printf("Intentando cerrar!\n");
            try {
                _clientInfo.socket.close();
            }
            catch (IOException e) {
                System.out.printf("Cerrando sockets!\n");    
            }
            System.out.printf("Cerrado ¿?\n");
        });
    }

    @Override
    public void onNextEvent() {}

    @Override
    public void onBackEvent() {}


    private void clearAllLabels() {

        for(int i = 0; i < 9; i++) {

            Label nameLabel = getNameLabelByPosition(i);
            Label moneyLabel = getMoneyLabelByPosition(i);

            nameLabel.setText("");
            moneyLabel.setText("");
        }
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

                    List<PlayerInfo> playerPositions = PokerPreGame.receivePlayerListWaiting(input, output);
                    showPlayers(playerPositions);
                    
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
        catch(IOException e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }
    }

    private Label getNameLabelByPosition(final int position) {
        switch(position) {
            case 0: return playerName0;
            case 1: return playerName1;
            case 2: return playerName2;
            case 3: return playerName3;
            case 4: return playerName4;
            case 5: return playerName5;
            case 6: return playerName6;
            case 7: return playerName7;
            case 8: return playerName8;
            default: throw new IllegalArgumentException("Invalid player position");
        }
    }

    private Label getMoneyLabelByPosition(final int position) {
        switch(position) {
            case 0: return playerMoney0;
            case 1: return playerMoney1;
            case 2: return playerMoney2;
            case 3: return playerMoney3;
            case 4: return playerMoney4;
            case 5: return playerMoney5;
            case 6: return playerMoney6;
            case 7: return playerMoney7;
            case 8: return playerMoney8;
            default: throw new IllegalArgumentException("Invalid player position");
        }
    }

    private void showPlayers(final List<PlayerInfo> players) {

        int myID = _clientInfo.id;
        int myIndex = IntStream.range(0, players.size())
                        .filter(i -> players.get(i).id == myID)
                        .findFirst()
                        .orElse(-1);

        if(myIndex == -1){
            System.out.printf("We are not in the list! Something is wrong...\n");
            return;
        }

        // Show players behind me(in the list) : position 1, 2, 3, ...
        int beforePosition = 1;
        for(int i = myIndex - 1; 0 <= i; i--) {

            PlayerInfo p = players.get(i);

            Label nameLabel = getNameLabelByPosition(beforePosition);
            Label moneyLabel = getMoneyLabelByPosition(beforePosition);

            Platform.runLater(() -> {
                nameLabel.setText(p.name);
                moneyLabel.setText(String.valueOf(_clientInfo.gameConfig._initialMoney));
            });

            ++beforePosition;
        }

        // Show players ahead of me(in the list) : position 8, 7, 6, ...
        int nextPosition = 8;
        for(int i = myIndex + 1; i < players.size(); i++) {

            PlayerInfo p = players.get(i);

            Label nameLabel = getNameLabelByPosition(nextPosition);
            Label moneyLabel = getMoneyLabelByPosition(nextPosition);

            Platform.runLater(() -> {
                nameLabel.setText(p.name);
                moneyLabel.setText(String.valueOf(_clientInfo.gameConfig._initialMoney));
            });

            --nextPosition;
        }

        
    }

}