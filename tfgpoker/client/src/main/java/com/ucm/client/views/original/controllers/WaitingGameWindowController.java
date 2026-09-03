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

/**
 * Controller for the waiting game window. It manages the UI elements and handles the communication with the server to update the player list and start the game.
 */
public class WaitingGameWindowController extends GenericController {

    /**
     * Button to start the game. Only visible to the host player.
     */
    @FXML
    private Button startButton;
    /**
     * Labels to display the room name and ID in the top-right corner of the window.
     */
    @FXML
    private Label roomNamePlaceholder;
    /**
     * Labels to display the room ID in the top-right corner of the window.
     */
    @FXML
    private Label roomIdPlaceholder;
    /**
     * StackPanes representing the player positions in the waiting room. Each StackPane corresponds to a player slot (0-8).
     */
    @FXML
    private StackPane pokerPlayer0, pokerPlayer1, pokerPlayer2, pokerPlayer3, pokerPlayer4, pokerPlayer5, pokerPlayer6, pokerPlayer7, pokerPlayer8;

    /**
     * Labels and ImageViews for displaying player names, money, and avatars in the waiting room. Each set corresponds to a player slot (0-8).
     */
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
    /**
     * List of ImageViews for player avatars, used to easily access and update avatar images based on player positions.
     */
    private List<ImageView> _listaAvatarProfiles;
    /**
     * ImageView for displaying the background table image in the waiting game window.
     */
    @FXML
    private ImageView imgTableInGame;
    /**
     *  Thread that handles the waiting for player information and game start events from the server. It runs in the background to keep the UI responsive.
     */
    private Thread _infoThread;

    /**
     * Sends a request to the server to start the game. This method is called when the host player clicks the "Start" button. It sends an integer event code to the server indicating that the game should start.
     */
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
     /**
     * {@inheritDoc}
     */
    @Override
    protected void onViewShown() {

        initializeAvatarProfiles();
        GUI_clearAllLabels();
        
        // Background table img
        imgTableInGame.setImage( new Image(getClass().getResource(_clientInfo.gameConfig._selectedTable).toExternalForm()) );
        
        // Room name and id from top-right corner
        roomNamePlaceholder.setText( _clientInfo.gameConfig._roomName );
        roomIdPlaceholder.setText( String.valueOf( _clientInfo.gameConfig._roomId ) );

        // Thread to wait for more players
        _infoThread = new Thread(() -> {
            waitToStartGame();
        });
        _infoThread.start();
    
        // Action listener to close the socket when client closes the window
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
    /**
     * Initializes the list of avatar profile ImageViews. This method populates the `_listaAvatarProfiles` list with references to the ImageView elements corresponding to each player slot (0-8). It allows for easy access and updating of avatar images based on player positions in the waiting room.
     */
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
    /**
     * Clears all player labels and resets the opacity of player StackPanes in the waiting room. This method iterates through all player positions (0-8) and sets the text of name and money labels to empty strings, while also setting the opacity of each player StackPane to 0.6, indicating that the slots are empty or inactive.
     */
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
    /**
     * Waits for new player information from the server and updates the UI accordingly. This method runs in a separate thread and continuously listens for events from the server, such as new players joining or the game starting. It updates the player list and UI elements based on the received information, ensuring that the waiting room reflects the current state of the game.
     */
    private void waitNewPlayersInfo() {

        try {

            InputStream input = _clientInfo.socket.getInputStream();
            OutputStream output = _clientInfo.socket.getOutputStream();

            if(_clientInfo.isHost) {

                _clientInfo.id = SocketUtils.receiveInt(input);
                System.out.printf("Player host ID is %d\n", _clientInfo.id);

                if(_clientInfo.gameConfig._joinedAsSpectator) {
                    
                    System.out.printf("Player host is specting!\n");
                }
                else {
                    System.out.printf("Player host is playing!\n");
                    Platform.runLater(() -> {
                        playerName0.setText(_clientInfo.name);
                        playerMoney0.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
                        pokerPlayer0.setOpacity(1);
                        GUI_updateAvatarPosition(0, _clientInfo.name);
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
                    GUI_updateAvatarPosition(0, _clientInfo.name);
                    startButton.setVisible(false);
                });
            }

            boolean kepWaiting = true;
            while(kepWaiting) {

                int event = SocketUtils.receiveInt(input);
                if(event == GameType.EVENT_PLAYER_JOINED) {

                    _clientInfo.playerPositions = PokerPreGame.receivePlayerListWaiting(input, output);
                    Platform.runLater(() -> {
                        GUI_resetPlayers();
                        GUI_showPlayers(_clientInfo.playerPositions);
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
    /**
     * Retrieves the StackPane corresponding to a given player position. This method is used to access the UI element representing a specific player slot in the waiting room, allowing for updates to the player's display (e.g., name, money, avatar) based on their position in the game.
     * @param position
     * @return The StackPane associated with the specified player position (0-8).
     */
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
    /**
     * Retrieves the Label corresponding to a given player position for displaying the player's name. This method is used to access the UI element that shows the name of a specific player in the waiting room, allowing for updates based on their position in the game.
     * @param position
     * @return The Label associated with the specified player position (0-8) for displaying the player's name.
     */
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
    /**
     * Retrieves the Label corresponding to a given player position for displaying the player's money. This method is used to access the UI element that shows the amount of money a specific player has in the waiting room, allowing for updates based on their position in the game.
     * @param position
     * @return The Label associated with the specified player position (0-8) for displaying the player's money.
     */
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
    /**
     * Waits for the game to start by listening for events from the server. This method runs in a separate thread and continuously checks for player join events, game start confirmations, and error messages. It updates the UI accordingly and transitions to the next view when the game is ready to start.
     */
    private void waitToStartGame() {

        try {

            InputStream input = _clientInfo.socket.getInputStream();
            OutputStream output = _clientInfo.socket.getOutputStream();

            if(_clientInfo.isHost) {

                _clientInfo.id = SocketUtils.receiveInt(input);
                System.out.printf("Player host ID is %d\n", _clientInfo.id);

                if(_clientInfo.gameConfig._joinedAsSpectator) {
                    
                    System.out.printf("Player host is specting!\n");
                }
                else {
                    System.out.printf("Player host is playing!\n");
                    Platform.runLater(() -> {
                        playerName0.setText(_clientInfo.name);
                        playerMoney0.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
                        pokerPlayer0.setOpacity(1);
                        GUI_updateAvatarPosition(0, _clientInfo.name);
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
                    GUI_updateAvatarPosition(0, _clientInfo.name);
                    startButton.setVisible(false);
                });
            }

            boolean kepWaiting = true;
            while(kepWaiting) {

                int event = SocketUtils.receiveInt(input);
                if(event == GameType.EVENT_PLAYER_JOINED) {

                    _clientInfo.playerPositions.clear();
                    _clientInfo.playerPositions = PokerPreGame.receivePlayerListWaiting(input, output);

                    Platform.runLater(() -> {
                        GUI_resetPlayers();
                        GUI_showPlayers(_clientInfo.playerPositions);
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
    /**
     * Clears all player labels and resets the opacity of player StackPanes in the waiting room. This method iterates through all player positions (0-8) and sets the text of name and money labels to empty strings, while also setting the opacity of each player StackPane to 0.6, indicating that the slots are empty or inactive. This method should be called from the JavaFX Application Thread to ensure thread safety when updating UI components.
     */
    private void GUI_clearAllLabels() {

        for(int i = 0; i < 9; i++) {

            Label nameLabel = getNameLabelByPosition(i);
            Label moneyLabel = getMoneyLabelByPosition(i);
            StackPane playerStackPane = getPlayerStackPaneByPosition(i);

            playerStackPane.setOpacity( 0.6 );
            nameLabel.setText("");
            moneyLabel.setText("");
        }
    }
    /**
     * Updates the avatar image for a specific player position in the waiting room. This method retrieves the avatar image associated with the player's name and sets it to the corresponding ImageView. It also applies a circular clip to the avatar image to create a rounded appearance. The avatar is made visible after being updated.
     * @param position
     * @param name
     */
    private void GUI_updateAvatarPosition(final int position, String name) {

        ImageView avatarImage = _listaAvatarProfiles.get(position);
        Image avatar = _clientInfo.getAvatar(name, 80);

        avatarImage.setImage(avatar);
        avatarImage.setFitWidth(80);
        avatarImage.setFitHeight(80);
        avatarImage.setPreserveRatio(true);

        Circle clip = new Circle();
        clip.centerXProperty().bind(avatarImage.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(avatarImage.fitWidthProperty().divide(2));
        clip.radiusProperty().bind(avatarImage.fitWidthProperty().divide(2));

        avatarImage.setClip(clip);
        avatarImage.setVisible(true);
    }
    /**
     * Displays the list of players in the waiting room UI. This method takes a list of PlayerInfo objects and updates the corresponding UI elements (name labels, money labels, and avatar images) based on the players' positions. It handles the display logic for both spectators and active players, ensuring that the current player's information is shown in the correct position, with other players displayed relative to them.
     * @param players
     */
    private void GUI_showPlayers(final List<PlayerInfo> players) {

        if(players == null || players.isEmpty()) {
            GUI_resetPlayers();
            return;
        }

        int myID = _clientInfo.id;
        int myIndex = IntStream.range(0, players.size())
                        .filter(i -> players.get(i).id == myID)
                        .findFirst()
                        .orElse(-1);

        // Current player is an spectator
        if(myID == -1) {
         
            int seatIndex = 8;
            for(int i = players.size() - 1; 0 <= i; i--) {

                PlayerInfo p = players.get(i);
                GUI_drawPlayer(p, seatIndex);
                
                seatIndex--;
            }

            return;
        }


        // Show myself
        PlayerInfo me = players.get(myIndex);
        GUI_drawPlayer(me, 0);

        // Show players behind me(in the list) : position 1, 2, 3, ...
        int beforePosition = 8;
        for(int i = myIndex - 1; i >= 0; i--) {

            PlayerInfo p = players.get(i);
            GUI_drawPlayer(p, beforePosition);

            --beforePosition;
        }

        // Show players ahead of me(in the list) : position 8, 7, 6, ...
        int nextPosition = 1;
        for(int i = myIndex + 1; i < players.size(); i++) {

            PlayerInfo p = players.get(i);
            GUI_drawPlayer(p, nextPosition);

            ++nextPosition;
        }


        System.out.printf("Players in the waiting room:\n");
        for(PlayerInfo cl : players) {
            System.out.printf("Player %s[%d] in waiting room\n", cl.name, cl.id);
        }
        System.out.printf("\n");
    }
    /**
     * Draws a player's information in the waiting room UI at a specified position. This method updates the name label, money label, and avatar image for the given player based on their position in the waiting room. It sets the opacity of the player's StackPane to fully visible and calls the method to update the avatar image accordingly.
     * @param player
     * @param pos
     */
    private void GUI_drawPlayer(final PlayerInfo player, final int pos) {

        Label nameLabel = getNameLabelByPosition(pos);
        Label moneyLabel = getMoneyLabelByPosition(pos);
        StackPane playerStackPane = getPlayerStackPaneByPosition(pos);

        nameLabel.setText(player.name);
        moneyLabel.setText(String.valueOf(_clientInfo.gameConfig._initialMoney));
        playerStackPane.setOpacity( 1 );
        GUI_updateAvatarPosition(pos, player.name);
    }
    /**
     * Clears all player labels and resets the opacity of player StackPanes in the waiting room. This method iterates through all player positions (0-8) and sets the text of name and money labels to empty strings, while also setting the opacity of each player StackPane to 0.6, indicating that the slots are empty or inactive. It also hides the avatar images for each player slot. This method should be called from the JavaFX Application Thread to ensure thread safety when updating UI components.
     */
    private void GUI_resetPlayers() {

        for(int seatIndex = 0; seatIndex < 9; seatIndex++) {
            
            Label nameLabel = getNameLabelByPosition(seatIndex);
            Label moneyLabel = getMoneyLabelByPosition(seatIndex);
            StackPane playerStackPane = getPlayerStackPaneByPosition(seatIndex);
            ImageView avatarImage = _listaAvatarProfiles.get(seatIndex);

            nameLabel.setText("");
            moneyLabel.setText("");
            playerStackPane.setOpacity(0.6);
            avatarImage.setVisible(false);
        }
    }

}