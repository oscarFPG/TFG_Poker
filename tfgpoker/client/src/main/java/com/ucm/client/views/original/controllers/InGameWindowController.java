package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.client.AvatarGenerator;
import com.ucm.common.GameConfig;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerGame;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;
import com.ucm.common.SocketUtils;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;


public class InGameWindowController extends GenericController {


    @FXML private Label usernamePlaceHolder;

    @FXML
    private ImageView imgAvatarProfile;

    @FXML
    private VBox buttonsHolder;

    /* Call, Raise, Fold and Check buttons */
    @FXML private Button btnFold;
    @FXML private Button btnCall;
    @FXML private Button btnRaise;

    @FXML
    private ImageView imgTableInGame;
    /* Min, 1/2 pot and Max buttons */
    @FXML private Button btnRound;
    @FXML private Button btnMinBet, btnHalfBet, btnMaxBet;

    /* Money buttons, labels and slider */
    @FXML private Label labelMoney;
    @FXML private Slider sliderMoney;
    @FXML private Button btnDecreaseMoney;
    @FXML private Button btnIncreaseMoney;
    

    /* All poker players seats */
    @FXML private StackPane pokerPlayer0;           // Always playing-client seat
    @FXML private Label playerName0, playerMoney0;  // Always playing-client seat

    @FXML private StackPane pokerPlayer1;
    @FXML private Label playerName1, playerMoney1;

    @FXML private StackPane pokerPlayer2;
    @FXML private Label playerName2, playerMoney2;

    @FXML private StackPane pokerPlayer3;
    @FXML private Label playerName3, playerMoney3;

    @FXML private StackPane pokerPlayer4;
    @FXML private Label playerName4, playerMoney4;

    @FXML private StackPane pokerPlayer5;
    @FXML private Label playerName5, playerMoney5;

    @FXML private StackPane pokerPlayer6;
    @FXML private Label playerName6, playerMoney6;

    @FXML private StackPane pokerPlayer7;
    @FXML private Label playerName7, playerMoney7;

    @FXML private StackPane pokerPlayer8;
    @FXML private Label playerName8, playerMoney8;

    /* Table info */
    @FXML private ImageView tableCard0, tableCard1, tableCard2, tableCard3, tableCard4;
    @FXML private Label labelTotalPot;
    
    /*All reverse image cards for players*/
    @FXML
    private HBox hboxImageCards0;
    @FXML
    private ImageView imgLeftCard0, imgRightCard0, imgAvatarProfile0;

    @FXML
    private HBox hboxImageCards1;
    @FXML
    private ImageView imgLeftCard1, imgRightCard1, imgAvatarProfile1;

    @FXML
    private HBox hboxImageCards2;
    @FXML
    private ImageView imgLeftCard2, imgRightCard2, imgAvatarProfile2;

    @FXML
    private HBox hboxImageCards3;
    @FXML
    private ImageView imgLeftCard3, imgRightCard3, imgAvatarProfile3;

    @FXML
    private HBox hboxImageCards4;
    @FXML
    private ImageView imgLeftCard4, imgRightCard4, imgAvatarProfile4;

    @FXML
    private HBox hboxImageCards5;
    @FXML
    private ImageView imgLeftCard5, imgRightCard5, imgAvatarProfile5;

    @FXML
    private HBox hboxImageCards6;
    @FXML
    private ImageView imgLeftCard6, imgRightCard6, imgAvatarProfile6;

    @FXML
    private HBox hboxImageCards7;
    @FXML
    private ImageView imgLeftCard7, imgRightCard7, imgAvatarProfile7;

    @FXML
    private HBox hboxImageCards8;
    @FXML
    private ImageView imgLeftCard8, imgRightCard8, imgAvatarProfile8;

    private List<HBox> _listImageCards;

    private List<ImageView> _listPaintCards;

    private List<ImageView> _listaAvatarProfiles;


    private Thread _gameThread = null;
    private BlockingQueue<String> _commandQueue = new LinkedBlockingQueue<>();
    private boolean _swapCallToCheck = false;
    private List<Integer> _playerSeatLabel = null; // Player id = i -> list[i] = m -> m stackpane label


    @Override
    protected void onViewShown() {

        clearAllLabels();
        imgTableInGame.setImage(new Image(getClass().getResource(_clientInfo.gameConfig._selectedTable).toExternalForm()));
        imgAvatarProfile.setImage(_clientInfo.getAvatar(_clientInfo.name,64));
        //TODO: cambair cuando haya bots. Tener en cuenta cuando un cliente se sale o hace fold
        initializeImageCards ();
        initializeCardsPosition ();
        initializeAvatarProfiles ();
        showPlayers(_clientInfo.playerPositions);
        initializeSlider(_clientInfo.gameConfig);

        usernamePlaceHolder.setText( _clientInfo.name );
        labelTotalPot.setText("0");

        _stage.setOnCloseRequest(event -> {

            try {
                if(_clientInfo.socket != null && !_clientInfo.socket.isClosed())
                    _clientInfo.socket.close();

                if(_gameThread != null && _gameThread.isAlive())
                    _gameThread.interrupt();
            }
            catch (IOException e) {
                System.out.printf("Error closing socket: %s\n", e.getMessage());
            }
        });

        _gameThread = new Thread(() -> {

            final boolean ok = pokerGame(_clientInfo.name, _clientInfo.socket);
            if(ok) {
                System.out.printf("All OK! Game finished!\n");
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game finished");
                    alert.setHeaderText("The game has finished successfully!");
                    alert.setContentText("You will return to the main menu");
                    //alert.showAndWait();

                });
            }
            else {
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Game was cancelled");
                    alert.setHeaderText("All players disconnected");
                    alert.setContentText("You will return to the main menu");
                    //alert.showAndWait();

                });
            }
            
        });
        _gameThread.start();
    }

    private void initializeCardsPosition () {
        _listImageCards = List.of (
            hboxImageCards0,
            hboxImageCards1,
            hboxImageCards2,
            hboxImageCards3,
            hboxImageCards4,
            hboxImageCards5,
            hboxImageCards6,
            hboxImageCards7,
            hboxImageCards8
        );
        _listImageCards.forEach(hbox -> hbox.setVisible(false));
    }

    private void initializeImageCards () {
        _listPaintCards = List.of(
            imgLeftCard0, imgRightCard0,
            imgLeftCard1, imgRightCard1,
            imgLeftCard2, imgRightCard2,
            imgLeftCard3, imgRightCard3,
            imgLeftCard4, imgRightCard4,
            imgLeftCard5, imgRightCard5,
            imgLeftCard6, imgRightCard6,
            imgLeftCard7, imgRightCard7,
            imgLeftCard8, imgRightCard8
        );

        Image cardImage = new Image(getClass().getResource(_clientInfo.gameConfig._selectedCard).toExternalForm());
        _listPaintCards.subList(1, _listPaintCards.size()).forEach(iv -> iv.setImage(cardImage));
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


    @FXML
    private void foldAction() {

        if(!_commandQueue.isEmpty())
            return;


        _commandQueue.offer(GameType.CALL_ACTION_FULL);
    }

    @FXML
    private void callAction() {

        if(!_commandQueue.isEmpty())
            return;


        if(_swapCallToCheck)
            _commandQueue.offer(GameType.CHECK_ACTION_FULL);    
        else
            _commandQueue.offer(GameType.CALL_ACTION_FULL);
    }

    @FXML
    private void raiseAction() {

        if(!_commandQueue.isEmpty())
            return;


        int amount = Integer.parseInt( labelMoney.getText() );
        _commandQueue.offer( String.format("%s %d", GameType.RAISE_ACTION_FULL, amount) );
    }

    @FXML
    private void minBetAction() {
        int MIN_VALUE = 10;
        sliderMoney.setValue(MIN_VALUE);
    }

    @FXML
    private void halfBetAction() {
        int HALF_VALUE = 50;
        sliderMoney.setValue(HALF_VALUE);
    }

    @FXML
    private void maxBetAction() {
        double MAX_VALUE = sliderMoney.getMax();
        sliderMoney.setValue(MAX_VALUE);
    }

    @FXML
    private void decreaseMoneyAction() {
        double newValue = sliderMoney.getValue() - 10;
        newValue = Math.clamp(newValue, sliderMoney.getMin(), sliderMoney.getMax());
        sliderMoney.setValue(newValue);
    }

    @FXML
    private void increaseMoneyAction() {
        
        double newValue = sliderMoney.getValue() + 10;
        newValue = Math.clamp(newValue, sliderMoney.getMin(), sliderMoney.getMax());
        sliderMoney.setValue(newValue);
    }


    private boolean pokerGame(String name, Socket socket) {

		int gameStatusCode;
        PlayerRole role;
		Card[] playerCards = new Card[2];
		Card[] tableCardValues = new Card[5];
        try {

            InputStream input = socket.getInputStream();
            boolean endOfGame = false;
            while(!endOfGame) {

                try {

                    clearTableCards();

                    // Player role and cards
                    role = PokerGame.receivePlayerRole(input);
                    playerCards[0] = PokerGame.receiveCard(input);
                    playerCards[1] = PokerGame.receiveCard(input);
                    System.out.printf("Assigned role: %s\n", role.toString());
                    System.out.printf(
                        "Received cards: %s - %s\n",
                        playerCards[0].toString(), 
                        playerCards[1].toString()
                    );

                    // Preflop
                    System.out.printf("-- Preflop --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("PREFLOP");
                    });
                    receivePlayersUpdatedInfo(socket);
                    playRound(playerCards[0], playerCards[1], role, socket);
                    
                    tableCardValues[0] = PokerGame.receiveCard(input);
                    tableCardValues[1] = PokerGame.receiveCard(input);
                    tableCardValues[2] = PokerGame.receiveCard(input);
                    showTableCard(tableCard0, tableCardValues[0]);
                    showTableCard(tableCard1, tableCardValues[1]);
                    showTableCard(tableCard2, tableCardValues[2]);

                    // Flop
                    System.out.printf("-- Flop --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("FLOP");
                    });
                    receivePlayersUpdatedInfo(socket);
                    playRound(playerCards[0], playerCards[1], role, socket);

                    tableCardValues[3] = PokerGame.receiveCard(input);
                    showTableCard(tableCard3, tableCardValues[3]);

                    // Turn
                    System.out.printf("-- Turn --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("TURN");
                    });
                    receivePlayersUpdatedInfo(socket);
                    playRound(playerCards[0], playerCards[1], role, socket);

                    tableCardValues[4] = PokerGame.receiveCard(input);
                    showTableCard(tableCard4, tableCardValues[4]);

                    // River
                    System.out.printf("-- River --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("RIVER");
                    });
                    receivePlayersUpdatedInfo(socket);
                    playRound(playerCards[0], playerCards[1], role, socket);

                    // Showdown
                    System.out.printf("-- Showdown --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("SHOWDOWN");
                    });
                    receivePlayersUpdatedInfo(socket);
                    
                    final int seconds = 3;
                    System.out.printf("%d seconds pause to see the winner...\n", seconds);
                    Thread.sleep(seconds * 1000);

                    // Game ends or keeps
                    gameStatusCode = SocketUtils.receiveInt(input);
                    endOfGame = (gameStatusCode == GameType.GAME_ENDS);
                    if(gameStatusCode == GameType.GAME_ENDS)
                        System.out.printf("Match ended!\n\n");
                    else if(gameStatusCode == GameType.GAME_KEEPS)
                        System.out.printf("Match keeps!\n\n");

                }
                catch (OnlyOnePlayerLeftException e) {

                    System.out.printf("There is only one player left!\n");
                    try {

                        // Get winner/loser state
                        receivePlayersUpdatedInfo(socket);

                        final int seconds = 3;
                        System.out.printf("%d seconds pause to see the winner...\n", seconds);
                        Thread.sleep(seconds * 1000);

                        // Game ends or keeps
                        gameStatusCode = SocketUtils.receiveInt(input);
                        endOfGame = (gameStatusCode == GameType.GAME_ENDS);
                        if(gameStatusCode == GameType.GAME_ENDS)
                            System.out.printf("Match ended!\n\n");
                        else if(gameStatusCode == GameType.GAME_KEEPS)
                            System.out.printf("Match keeps!\n\n");

                    }
                    catch(IOException | InterruptedException ex) {
                        System.out.printf("Error receiving the rank after a fold exception: %s", ex.getMessage());
                    }
                }
                catch(InterruptedException e) {
                    System.out.printf("Game thread interrupted: %s\n", e.getMessage());
                }

            }
        }
        catch(IOException e) {
            System.out.printf("Error on client socket: %s\n", e.getMessage());
            return false;
        }
        catch(CancelGameException e) {
            System.out.printf("Game cancelled by server: %s\n", e.getMessage());
            return false;
        }

        return true;
    }

    private void playRound(Card card1, Card card2, PlayerRole role, Socket socket) 
    throws OnlyOnePlayerLeftException, CancelGameException, IOException, InterruptedException {

        boolean handEndsByFold = false;

		int serverCode = SocketUtils.receiveInt(socket.getInputStream());
		while(serverCode != GameType.ROUND_ENDS && !handEndsByFold) {

			if(serverCode == GameType.TURN_FORCED_SB) {

				final int amountSB = SocketUtils.receiveInt(socket.getInputStream());
                final int onBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                final int offBetMoney = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the small blind with %d chips\n", amountSB);

                Platform.runLater(() -> {
                    updatePlayerInfo(0, role, onBetMoney, offBetMoney, false, false);
                });
			}
			else if(serverCode == GameType.TURN_FORCED_BB) {

				final int amountBB = SocketUtils.receiveInt(socket.getInputStream());
                final int onBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                final int offBetMoney = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the big blind with %d chips\n", amountBB);

                Platform.runLater(() -> {
                    updatePlayerInfo(0, role, onBetMoney, offBetMoney, false, false);
                });
			}
			else if(serverCode == GameType.TURN_WAIT) {

				System.out.printf("Wait for the other players to play...\n");
                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                });

			}
			else if(serverCode == GameType.TURN_PLAY) {

				System.out.printf("It's your turn to play!\n");
                Platform.runLater(() -> {
                    buttonsHolder.setVisible(true);
                });

				// Receive round info
				final int sb = SocketUtils.receiveInt( socket.getInputStream() );
				final int bb = SocketUtils.receiveInt( socket.getInputStream() );
				final int maxBet = SocketUtils.receiveInt( socket.getInputStream() );
				final int offBetMoney = SocketUtils.receiveInt( socket.getInputStream() );
				final int onBetMoney = SocketUtils.receiveInt( socket.getInputStream() );
                System.out.printf(
                    "-- Round info --\n SB: %d, BB: %d, MaxBet: %d\n OnBetMoney: %d, OffBetMoney: %d\n", 
                    sb, bb, maxBet, 
                    onBetMoney, offBetMoney
                );

                // Do not allow to bet less than the current max bet
                Platform.runLater(() -> {
                    sliderMoney.setMin( (double)maxBet );
                    sliderMoney.setMax( (double)(offBetMoney + onBetMoney)  );

                    updatePlayerInfo(0, role, onBetMoney, offBetMoney, false, false);
                });

                selectCommand(socket, sb, bb, maxBet, offBetMoney, onBetMoney);

                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                });
			}
			else if(serverCode == GameType.HAND_ENDS_BY_FOLD) {
				handEndsByFold = true;
			}
            else if(serverCode == GameType.TURN_OTHER_PLAYER || serverCode == GameType.MY_TURN_ACTION ) {

                final int otherPlayerID = SocketUtils.receiveInt( socket.getInputStream() );
                final String otherPlayerName = SocketUtils.receiveString( socket.getInputStream() );
                final PlayerRole otherPlayerRole = PokerGame.receivePlayerRole( socket.getInputStream() );
                final boolean otherPlayerIsFolded = SocketUtils.receiveInt( socket.getInputStream() ) == GameType.TRUE;
                final boolean otherPlayerIsWinner = SocketUtils.receiveInt( socket.getInputStream() ) == GameType.TRUE;
                final int otherPlayerOffBetMoney = SocketUtils.receiveInt( socket.getInputStream() );
                final int otherPlayerOnBetMoney = SocketUtils.receiveInt( socket.getInputStream() );

                System.out.printf(
                    "Other player action - PlayerID: %d, OffBetMoney: %d, OnBetMoney: %d\n",
                    otherPlayerID,
                    otherPlayerOffBetMoney, otherPlayerOnBetMoney
                );

                final int seatID = _playerSeatLabel.get(otherPlayerID).intValue();
                Label nameLabel = getNameLabelByPosition(seatID);
                Label moneyLabel = getMoneyLabelByPosition(seatID);

                Platform.runLater(() -> {
                    nameLabel.setText( otherPlayerName );
                    updatePlayerInfo(
                        seatID, otherPlayerRole, 
                        otherPlayerOnBetMoney, otherPlayerOffBetMoney, 
                        otherPlayerIsFolded, otherPlayerIsWinner
                    );
                });

            }
            else if(serverCode == GameType.TOTAL_POT) {

                final int totalPot = SocketUtils.receiveInt( socket.getInputStream() );
                Platform.runLater(() -> {
                    labelTotalPot.setText( String.valueOf(totalPot) );
                });
            }
            else if(serverCode == GameType.ERROR_GAME_CANCELS) {
                System.out.printf("Game has been cancelled by the server!\n");
                throw new CancelGameException();
            }
            else {
				System.out.printf("Unknown turn code %d\n", serverCode);
			}

			if(!handEndsByFold)
				serverCode = SocketUtils.receiveInt(socket.getInputStream());
		}
		System.out.printf("Round has ended!\n\n");

		if(handEndsByFold)
			throw new OnlyOnePlayerLeftException();
    }

    private void selectCommand(
        Socket socket, 
        final int sb, 
        final int bb, 
        final int maxBet, 
        final int offBetMoney, 
        final int onBetMoney
    ) throws InterruptedException {

        try {

            boolean valid = false;
            while(!valid) {

                // Update buttons to match available actions
                if(maxBet == 0) {
                    _swapCallToCheck = true;
                    Platform.runLater(() -> {
                        btnCall.setText("CHECK");
                    });
                }

                String command = _commandQueue.take();
                String baseCommand = command.split(" ")[0];

                System.out.printf("Full command received: %s\n", command);
                System.out.printf("Base command extracted: %s\n", baseCommand);

                valid = true;
                if (baseCommand.equalsIgnoreCase("raise") || baseCommand.equalsIgnoreCase("r")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);
                }
                else if (baseCommand.equalsIgnoreCase("fold") || baseCommand.equalsIgnoreCase("f")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);
                }
                else if (baseCommand.equalsIgnoreCase("check") || baseCommand.equalsIgnoreCase("k")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);
                }
                else if (baseCommand.equalsIgnoreCase("call") || baseCommand.equalsIgnoreCase("c")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);
                }
                else if (baseCommand.equalsIgnoreCase("all in") || baseCommand.equalsIgnoreCase("a")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);
                }
                else {
                    System.out.printf("Command %s not valid! Try again\n", command);
                    valid = false;
                }
            }

            _swapCallToCheck = false;
            Platform.runLater(() -> {
               btnCall.setText("CALL");         
            });
        }
        catch(IOException e) {
            System.out.printf("Error sending the command: %s\n", e.getMessage());
        }
    }
    

    private void initializeSlider(final GameConfig config) {

        sliderMoney.setMin(0);
        sliderMoney.setMax(1_000_000);
        sliderMoney.setValue(0);

        sliderMoney.valueProperty().addListener((obs, oldVal, newVal) -> {
            
            labelMoney.setText( String.valueOf(newVal.intValue()) );

            if( newVal.intValue() == sliderMoney.getMin() )
                btnRaise.setDisable(true);
            else
                btnRaise.setDisable(false);
        });
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
            default: throw new IllegalArgumentException( String.format("Invalid player position %d", position) );
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
            default: throw new IllegalArgumentException( String.format("Invalid player position %d", position) );
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
            default: throw new IllegalArgumentException( String.format("Invalid player position %d", position) );
        }
    }

    private void receivePlayersUpdatedInfo(Socket socket) throws IOException {

        InputStream input = socket.getInputStream();

        int isLast = 0;
        do {
            int playerID = SocketUtils.receiveInt(input);
            PlayerRole r = PokerGame.receivePlayerRole(input);
            boolean isFolded = SocketUtils.receiveInt(input) == GameType.TRUE;
            boolean isWinner = SocketUtils.receiveInt(input) == GameType.TRUE;
            int offBetMoney = SocketUtils.receiveInt(input);
            int onBetMoney = SocketUtils.receiveInt(input);

            System.out.printf(
                "UPDATE - Player ID: %d, Role: %s, Off-Bet Money: %d, On-Bet Money: %d, Folded: %b, Winner: %b\n", 
                playerID, r.name(), offBetMoney, onBetMoney, isFolded, isWinner
            );

            Platform.runLater(() -> {

                int seatID = _playerSeatLabel.get(playerID).intValue();
                updatePlayerInfo(seatID, r, onBetMoney, offBetMoney, isFolded, isWinner);
            });

            isLast = SocketUtils.receiveInt(input);
        }
        while(isLast != GameType.TRUE);
    }

    private String getPlayerMoneyInfo(int onBetMoney, int offBetMoney) {
        return String.format(
            "%d - %d", 
            onBetMoney, offBetMoney
        );
    }

    private void updatePlayerInfo(int seatID, PlayerRole role, int onBetMoney, int offBetMoney, boolean isFolded, boolean isWinner) {

        Label nameLabel = getNameLabelByPosition(seatID);
        Label moneyLabel = getMoneyLabelByPosition(seatID);

        Platform.runLater(() -> {

            PauseTransition transition = new PauseTransition(Duration.seconds(3));
            if(isWinner) {
                nameLabel.setText( nameLabel.getText() + " (Winner!)" );
                transition.setOnFinished(event -> {
                    nameLabel.setText( nameLabel.getText().replace(" (Winner!)", "") );
                });
                transition.play();

                System.out.printf("Player %s is the winner of the hand!\n", nameLabel.getText());
            }

            moneyLabel.setText( getPlayerMoneyInfo(onBetMoney, offBetMoney) );
        });
    }
    
    private void getAvatarPosition (final int position, String name) {
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


        _playerSeatLabel = new ArrayList<>();
        for(int i = 0; i < players.size(); i++)
            _playerSeatLabel.add(-1);

        _playerSeatLabel.set(myIndex, 0);
        playerName0.setText( _clientInfo.name );
        playerMoney0.setText( getPlayerMoneyInfo(0, _clientInfo.gameConfig._initialMoney) );
        pokerPlayer0.setVisible(true);
        getAvatarPosition(0, _clientInfo.name);

        // Show players behind me(in the list) : position 1, 2, 3, ...
        int beforePosition = 1;
        for(int i = myIndex - 1; 0 <= i; i--) {

            PlayerInfo p = players.get(i);
            _playerSeatLabel.set(p.id, beforePosition);

            Label nameLabel = getNameLabelByPosition(beforePosition);
            Label moneyLabel = getMoneyLabelByPosition(beforePosition);
            StackPane playerStackPane = getPlayerStackPaneByPosition(beforePosition);
            HBox cardsHBox = _listImageCards.get(beforePosition);
            
            final int pos = beforePosition;
            Platform.runLater(() -> {
                nameLabel.setText(p.name);
                moneyLabel.setText( getPlayerMoneyInfo(0, _clientInfo.gameConfig._initialMoney) );
                playerStackPane.setVisible(true);
                cardsHBox.setVisible(true);
                getAvatarPosition(pos, p.name);
            });

            ++beforePosition;
        }

        // Show players ahead of me(in the list) : position 8, 7, 6, ...
        int nextPosition = 8;
        for(int i = myIndex + 1; i < players.size(); i++) {

            PlayerInfo p = players.get(i);
            _playerSeatLabel.set(p.id, nextPosition);

            Label nameLabel = getNameLabelByPosition(nextPosition);
            Label moneyLabel = getMoneyLabelByPosition(nextPosition);
            StackPane playerStackPane = getPlayerStackPaneByPosition(nextPosition);
            HBox cardsHBox = _listImageCards.get(nextPosition);
            
            final int pos = nextPosition;
            Platform.runLater(() -> {
                nameLabel.setText(p.name);
                moneyLabel.setText( getPlayerMoneyInfo(0, _clientInfo.gameConfig._initialMoney) );
                playerStackPane.setVisible(true);
                cardsHBox.setVisible(true);
                getAvatarPosition(pos, p.name);
            });

            --nextPosition;
        }
    }

    private void showTableCard(ImageView imageView, Card card) {

        System.out.printf("Received card: %s\n", card.toString());

        String path = "images/cards/";
        String cardValue = (card.getNumber() == 1) ? "ace"
                        : (card.getNumber() == 2) ? "2"
                        : (card.getNumber() == 3) ? "3"
                        : (card.getNumber() == 4) ? "4"
                        : (card.getNumber() == 5) ? "5"
                        : (card.getNumber() == 6) ? "6"
                        : (card.getNumber() == 7) ? "7"
                        : (card.getNumber() == 8) ? "8"
                        : (card.getNumber() == 9) ? "9"
                        : (card.getNumber() == 10) ? "10"
                        : (card.getNumber() == 11) ? "jack"
                        : (card.getNumber() == 12) ? "queen"
                        : (card.getNumber() == 13) ? "king"
                        : "unknown";

        String cardSuit = (card.getSuit() == Suit.HEARTS) ? "hearts"
                        : (card.getSuit() == Suit.DIAMONDS) ? "diamonds"
                        : (card.getSuit() == Suit.CLUBS) ? "clubs"
                        : (card.getSuit() == Suit.SPADES) ? "spades"
                        : "unknown";


        InputStream in = getClass().getClassLoader().getResourceAsStream(  
            String.format("%s%s_of_%s.png", path, cardValue, cardSuit)
        );
        Image cardImage = new Image(in);

        Platform.runLater(() -> {
            imageView.setImage( cardImage );
        });
    }

    private void clearAllLabels() {

        for(int i = 0; i < 9; i++) {

            Label nameLabel = getNameLabelByPosition(i);
            Label moneyLabel = getMoneyLabelByPosition(i);
            StackPane playerStackPane = getPlayerStackPaneByPosition(i);

            playerStackPane.setVisible(false);
            nameLabel.setText("");
            moneyLabel.setText("");
        }
    }

    private void clearTableCards() {

        Platform.runLater(() -> {
            tableCard0.setImage(null);
            tableCard1.setImage(null);
            tableCard2.setImage(null);
            tableCard3.setImage(null);
            tableCard4.setImage(null);
        });
    }

}