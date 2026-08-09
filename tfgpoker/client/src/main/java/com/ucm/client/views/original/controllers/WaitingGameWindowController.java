package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.IntStream;

import com.ucm.client.utils.Messages;
import com.ucm.client.utils.NotificationManager;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerPreGame;
import com.ucm.common.SocketUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;



public class WaitingGameWindowController extends GenericController {

    @FXML
    private Button startButton;

    @FXML
    private Label roomNamePlaceholder;

    @FXML
    private Label roomIdPlaceholder;

    @FXML
    private StackPane pokerPlayer0;

    @FXML
    private StackPane pokerPlayer1;

    @FXML
    private StackPane pokerPlayer2;

    @FXML
    private StackPane pokerPlayer3;

    @FXML
    private StackPane pokerPlayer4;

    @FXML
    private StackPane pokerPlayer5;

    @FXML
    private StackPane pokerPlayer6;

    @FXML
    private StackPane pokerPlayer7;

    @FXML
    private StackPane pokerPlayer8;

    // Always the client position
    @FXML
    private Label playerName0, playerMoney0;
    @FXML
    private ImageView imgAvatarProfile0;

    @FXML
    private Label playerName1, playerMoney1;
    @FXML
    private ImageView imgAvatarProfile1;

    @FXML
    private Label playerName2, playerMoney2;
    @FXML
    private ImageView imgAvatarProfile2;

    @FXML
    private Label playerName3, playerMoney3;
    @FXML
    private ImageView imgAvatarProfile3;

    @FXML
    private Label playerName4, playerMoney4;
    @FXML
    private ImageView imgAvatarProfile4;

    @FXML
    private Label playerName5, playerMoney5;
    @FXML
    private ImageView imgAvatarProfile5;

    @FXML
    private Label playerName6, playerMoney6;
    @FXML
    private ImageView imgAvatarProfile6;

    @FXML
    private Label playerName7, playerMoney7;
    @FXML
    private ImageView imgAvatarProfile7;

    @FXML
    private Label playerName8, playerMoney8;
    @FXML
    private ImageView imgAvatarProfile8;

    private List<ImageView> _listaAvatarProfiles;

    @FXML
    private ImageView imgTableInGame;

    private Thread _infoThread;

    @FXML
    private void startGame() {
        
        try {
            SocketUtils.sendInteger(_clientInfo.socket.getOutputStream(), GameType.EVENT_GAME_STARTS);
        }
        catch(IOException e) {
            System.out.printf("Error sending EVENT_GAME_STARTS to server: %s\n", e.getMessage());
            NotificationManager.showError(Messages.Notifications.ERROR_SENDING_EVENT_GAME_STARTS + e.getMessage());
        }
    }

    @Override
    protected void onViewShown() {

        clearAllLabels();
        initializeAvatarProfiles();
        imgTableInGame.setImage( new Image(getClass().getResource(_clientInfo.gameConfig._selectedTable).toExternalForm()) );
        
        roomNamePlaceholder.setText( _clientInfo.gameConfig._roomName );
        roomIdPlaceholder.setText( String.valueOf( _clientInfo.gameConfig._roomId ) );

        _infoThread = new Thread(() -> {
            waitNewPlayersInfo();
        });
        _infoThread.start();
    
        _stage.setOnCloseRequest(event -> {

            try {
                _clientInfo.socket.close();
                System.out.printf("Connection closed by user before closing the window!\n"); 
            }
            catch (IOException e) {
                System.out.printf("Minor error closing client socket!\n");    
            }
        });

    }

    private void initializeAvatarProfiles () {
        _listaAvatarProfiles = List.of(
            imgAvatarProfile0,
            imgAvatarProfile1,
            imgAvatarProfile2,
            imgAvatarProfile3,
            imgAvatarProfile4,
            imgAvatarProfile5,
            imgAvatarProfile6,
            imgAvatarProfile7,
            imgAvatarProfile8
        );
    }

    private void clearAllLabels() {

        for(int i = 0; i < 9; i++) {

            Label nameLabel = getNameLabelByPosition(i);
            Label moneyLabel = getMoneyLabelByPosition(i);
            StackPane playerStackPane = getPlayerStackPaneByPosition(i);

            playerStackPane.setOpacity( 0.6 );
            nameLabel.setText("");
            moneyLabel.setText("");
        }
    }

    private void waitNewPlayersInfo() {

        try {

            InputStream input = _clientInfo.socket.getInputStream();
            OutputStream output = _clientInfo.socket.getOutputStream();

            if(_clientInfo.isHost) {

                _clientInfo.id = SocketUtils.receiveInt(input);
                System.out.printf("Player host ID is %d\n", _clientInfo.id);

                if(_clientInfo.gameConfig._joinedAsSpectator) {
                    // TODO : Mostrar como espectador
                    System.out.printf("Player host is specting!\n");
                }
                else {
                    System.out.printf("Player host is playing!\n");
                    Platform.runLater(() -> {
                        playerName0.setText(_clientInfo.name);
                        playerMoney0.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
                        pokerPlayer0.setOpacity(1);
                        updateAvatarPosition(0, _clientInfo.name, false, false);
                    });
                }

                System.out.printf("Server response: This client is the host of the game!\n");
                NotificationManager.showSuccess(Messages.Notifications.PLAYER_IS_HOST);
            }
            else {

                _clientInfo.id = SocketUtils.receiveInt(input);
                System.out.printf("Player guest ID is %d\n", _clientInfo.id);

                System.out.printf("Server response: This client is a guest!\n");
                NotificationManager.showSuccess(Messages.Notifications.PLAYER_IS_GUEST);
                
                Platform.runLater(() -> {
                    playerName0.setText(_clientInfo.name);
                    playerMoney0.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
                    pokerPlayer0.setOpacity(1);
                    updateAvatarPosition(0, _clientInfo.name, false, false);
                    startButton.setVisible(false);
                });
            }

            boolean kepWaiting = true;
            while(kepWaiting) {

                int event = SocketUtils.receiveInt(input);
                if(event == GameType.EVENT_PLAYER_JOINED) {

                    _clientInfo.playerPositions = PokerPreGame.receivePlayerListWaiting(input, output);
                    Platform.runLater(() -> {
                        resetPlayers(_clientInfo.playerPositions);
                        showPlayers(_clientInfo.playerPositions);
                    });    
                }
                else if(event == GameType.CONFIRMATION_GAME_STARTS) {
                    System.out.printf("Event GAME_STARTS!\n");
                    kepWaiting = false;
                }
                else if(event == GameType.ERROR_GAME_CANNOT_START) {
                    System.out.printf("Game cannot start! Missing players\n");
                    NotificationManager.showError(Messages.Notifications.ERROR_MISSING_PLAYERS);
                }
                else {
                    System.out.printf("Waiting phase: event %d unknown!\n", event);
                    NotificationManager.showError(Messages.Notifications.ERROR_UNKNOWN_EVENT);
                }
            }
            System.out.printf("Game ready to start!\n");
            SocketUtils.sendInteger(output, GameType.CONFIRMATION_PLAYER_STARTS);

            Platform.runLater(() -> {
                next();
            });
        }
        catch(IOException e) {
            System.out.printf("Error: %s\n", e.getMessage());
            NotificationManager.showError(e.getMessage());
        }

        System.out.printf("Finished waiting for players info!\n");
    }

    private StackPane getPlayerStackPaneByPosition(final int position) {
        switch(position) {
            case 0: return pokerPlayer0;
            case 1: return pokerPlayer1;
            case 2: return pokerPlayer2;
            case 3: return pokerPlayer3;
            case 4: return pokerPlayer4;
            case 5: return pokerPlayer5;
            case 6: return pokerPlayer6;
            case 7: return pokerPlayer7;
            case 8: return pokerPlayer8;
            default: throw new IllegalArgumentException("Invalid player position");
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

    private void updateAvatarPosition(final int position, String name, boolean clear, boolean isBot) {

        ImageView avatarImage = _listaAvatarProfiles.get(position);
        Image avatar = _clientInfo.getAvatar(name, 80, isBot);

        avatarImage.setImage(avatar);
        avatarImage.setFitWidth(80);
        avatarImage.setFitHeight(80);
        avatarImage.setPreserveRatio(true);

        Circle clip = new Circle();
        clip.centerXProperty().bind(avatarImage.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(avatarImage.fitWidthProperty().divide(2));
        clip.radiusProperty().bind(avatarImage.fitWidthProperty().divide(2));

        avatarImage.setClip(clip);
        avatarImage.setVisible(!clear);
    }

    private void showPlayers(final List<PlayerInfo> players) {

        int myID = _clientInfo.id;
        int myIndex = IntStream.range(0, players.size())
                        .filter(i -> players.get(i).id == myID)
                        .findFirst()
                        .orElse(-1);

        if(myID == -1) {
         
            int seatIndex = 8;
            for(int i = players.size() - 1; 0 <= i; i--) {

                PlayerInfo p = players.get(i);

                Label nameLabel = getNameLabelByPosition(seatIndex);
                Label moneyLabel = getMoneyLabelByPosition(seatIndex);
                StackPane playerStackPane = getPlayerStackPaneByPosition(seatIndex);
                final int pos = seatIndex;

                Platform.runLater(() -> {
                    nameLabel.setText(p.name);
                    moneyLabel.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
                    playerStackPane.setOpacity(1);
                    updateAvatarPosition(pos, p.name, false, p.isBot);
                });

                seatIndex--;
            }

            return;
        }

        // Show players behind me(in the list) : position 1, 2, 3, ...
        int beforePosition = 8;
        for(int i = myIndex - 1; i >= 0; i--) {

            PlayerInfo p = players.get(i);

            Label nameLabel = getNameLabelByPosition(beforePosition);
            Label moneyLabel = getMoneyLabelByPosition(beforePosition);
            StackPane playerStackPane = getPlayerStackPaneByPosition(beforePosition);
            
            final int pos = beforePosition;
            Platform.runLater(() -> {
                nameLabel.setText(p.name);
                moneyLabel.setText(String.valueOf(_clientInfo.gameConfig._initialMoney));
                playerStackPane.setOpacity( 1 );
                updateAvatarPosition(pos, p.name, false, p.isBot);
            });

            --beforePosition;
        }

        // Show players ahead of me(in the list) : position 8, 7, 6, ...
        int nextPosition = 1;
        for(int i = myIndex + 1; i < players.size(); i++) {

            PlayerInfo p = players.get(i);

            Label nameLabel = getNameLabelByPosition(nextPosition);
            Label moneyLabel = getMoneyLabelByPosition(nextPosition);
            StackPane playerStackPane = getPlayerStackPaneByPosition(nextPosition);

            final int pos = nextPosition;
            Platform.runLater(() -> {
                nameLabel.setText(p.name);
                moneyLabel.setText(String.valueOf(_clientInfo.gameConfig._initialMoney));
                playerStackPane.setOpacity( 1 );
                updateAvatarPosition(pos, p.name, false, p.isBot);
            });

            ++nextPosition;
        }

        for(PlayerInfo cl : players) {
            System.out.printf("Player [%d]%s in waiting room\n", cl.id, cl.name);
        }
        System.out.printf("\n");
    }

    private void resetPlayers(final List<PlayerInfo> players) {

        int myID = _clientInfo.id;
        if(myID == -1) {
         
            int seatIndex = 0;
            for(int i = players.size() - 1; 0 <= i; i--) {

                Label nameLabel = getNameLabelByPosition(seatIndex);
                Label moneyLabel = getMoneyLabelByPosition(seatIndex);
                StackPane playerStackPane = getPlayerStackPaneByPosition(seatIndex);

                final int pos = i;

                Platform.runLater(() -> {
                    nameLabel.setText("");
                    moneyLabel.setText("");
                    playerStackPane.setOpacity(0.6);
                    updateAvatarPosition(pos, _clientInfo.name, true, false);
                });

                seatIndex++;
            }

            return;
        }

        for(int seatIndex = 1; seatIndex < 9; seatIndex++) {
            
            Label nameLabel = getNameLabelByPosition(seatIndex);
            Label moneyLabel = getMoneyLabelByPosition(seatIndex);
            StackPane playerStackPane = getPlayerStackPaneByPosition(seatIndex);

            final int pos = seatIndex;

            Platform.runLater(() -> {
                nameLabel.setText("");
                moneyLabel.setText("");
                playerStackPane.setOpacity(0.6);
                updateAvatarPosition(pos, _clientInfo.name, true, false);
            });
        }
    }
}