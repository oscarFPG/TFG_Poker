package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
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


    /* Player info */
    @FXML private Label usernamePlaceHolder;
    @FXML private ImageView imgAvatarProfile;

    /* Hodler + Buttons */
    @FXML private VBox buttonsHolder;
    @FXML private Button btnFold, btnCall, btnRaise;
    @FXML private Button btnRound, btnMinBet, btnHalfBet, btnMaxBet;
    @FXML private Button btnDecreaseMoney, btnIncreaseMoney;

    /* Table info */
    @FXML private Label labelTotalPot;
    @FXML private ImageView imgTableInGame;
    @FXML private ImageView tableCard0, tableCard1, tableCard2, tableCard3, tableCard4;

    /* Money buttons, labels and slider */
    @FXML private Label labelMoney;
    @FXML private Slider sliderMoney;
    
    /* All poker players seats */
    @FXML private StackPane pokerPlayer0;           // Always playing-client seat
    @FXML private Label playerName0, playerMoney0, labelOnBetMoney0;  // Always playing-client seat
    @FXML private Label labelEquity0;
    @FXML private HBox hboxImageCards0, hboxHandBet0;
    @FXML private ImageView imgLeftCard0, imgRightCard0, imgAvatarProfile0;
    @FXML private ImageView ImgDealer0;

    @FXML private StackPane pokerPlayer1;
    @FXML private Label playerName1, playerMoney1, labelOnBetMoney1;
    @FXML private Label labelEquity1;
    @FXML private HBox hboxImageCards1, hboxHandBet1;
    @FXML private ImageView imgLeftCard1, imgRightCard1, imgAvatarProfile1;
    @FXML private ImageView ImgDealer1;

    @FXML private StackPane pokerPlayer2;
    @FXML private Label playerName2, playerMoney2, labelOnBetMoney2;
    @FXML private Label labelEquity2;
    @FXML private HBox hboxImageCards2, hboxHandBet2;
    @FXML private ImageView imgLeftCard2, imgRightCard2, imgAvatarProfile2;
    @FXML private ImageView ImgDealer2;

    @FXML private StackPane pokerPlayer3;
    @FXML private Label playerName3, playerMoney3, labelOnBetMoney3;
    @FXML private Label labelEquity3;
    @FXML private HBox hboxImageCards3, hboxHandBet3;
    @FXML private ImageView imgLeftCard3, imgRightCard3, imgAvatarProfile3;
    @FXML private ImageView ImgDealer3;

    @FXML private StackPane pokerPlayer4;
    @FXML private Label playerName4, playerMoney4, labelOnBetMoney4;
    @FXML private Label labelEquity4;
    @FXML private HBox hboxImageCards4, hboxHandBet4;
    @FXML private ImageView imgLeftCard4, imgRightCard4, imgAvatarProfile4;
    @FXML private ImageView ImgDealer4;

    @FXML private StackPane pokerPlayer5;
    @FXML private Label playerName5, playerMoney5, labelOnBetMoney5;
    @FXML private Label labelEquity5;
    @FXML private HBox hboxImageCards5, hboxHandBet5;
    @FXML private ImageView imgLeftCard5, imgRightCard5, imgAvatarProfile5;
    @FXML private ImageView ImgDealer5;

    @FXML private StackPane pokerPlayer6;
    @FXML private Label playerName6, playerMoney6, labelOnBetMoney6;
    @FXML private Label labelEquity6;
    @FXML private HBox hboxImageCards6, hboxHandBet6;
    @FXML private ImageView imgLeftCard6, imgRightCard6, imgAvatarProfile6;
    @FXML private ImageView ImgDealer6;

    @FXML private StackPane pokerPlayer7;
    @FXML private Label playerName7, playerMoney7, labelOnBetMoney7;
    @FXML private Label labelEquity7;
    @FXML private HBox hboxImageCards7, hboxHandBet7;
    @FXML private ImageView imgLeftCard7, imgRightCard7, imgAvatarProfile7;
    @FXML private ImageView ImgDealer7;

    @FXML private StackPane pokerPlayer8;
    @FXML private Label playerName8, playerMoney8, labelOnBetMoney8;
    @FXML private Label labelEquity8;
    @FXML private HBox hboxImageCards8, hboxHandBet8;
    @FXML private ImageView imgLeftCard8, imgRightCard8, imgAvatarProfile8;
    @FXML private ImageView ImgDealer8;
    
    /* Member variables to group all players variables */
    private List<StackPane> _listPlayerStackPanes;  // Player stackpanes
    private List<Label> _listNameLabels, _listMoneyLabels; // Player names and money OFF bet
    private List<HBox> _listHandBet;    // Player money ON bet + chips image
    private List<Label> _listOnBetMoney;    // Player money ON bet number
    private List<Label> _listEquity;    // Player equities
    private List<HBox> _listImageCards;     // Player cards
    private List<ImageView> _listPaintCards, _listAvatarProfiles; // Player cards images and avatar
    private List<ImageView> _listDealer;    // Player dealer chip
    

    /* Variables to add logic */
    private BlockingQueue<String> _commandQueue = new LinkedBlockingQueue<>();
    private Map<Integer, Integer> _playerSeatMap; // Player id = i -> list[i] = m -> m stackpane label
    private boolean _swapCallToCheck = false;
    private boolean _userCloses = false;
    private Thread _gameThread;

    
    @Override
    protected void onViewShown() {

        initialize();
        GUI_initializePlayersInfo();
        GUI_initializeCardStyle();
        GUI_initializeDealerButton();
        GUI_initializeMoneySlider();
        GUI_showWaitingPlayers(_clientInfo.playerPositions);

        usernamePlaceHolder.setText( _clientInfo.name );
        imgAvatarProfile.setImage( _clientInfo.getAvatar(_clientInfo.name,64) );
        imgTableInGame.setImage(new Image(getClass().getResource(_clientInfo.gameConfig._selectedTable).toExternalForm()));
        
        _stage.setOnCloseRequest(event -> {

            try {

                _userCloses = true;
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

            boolean ok = pokerGame(_clientInfo.name, _clientInfo.socket);
            if(ok) {
                System.out.printf("All OK! Game finished!\n");
                /*
                Platform.runLater(() -> {

                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game finished");
                    alert.setHeaderText("The game has finished successfully!");
                    alert.setContentText("You will return to the main menu");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/original/css/style.css").toExternalForm());
                    alert.getDialogPane().getStyleClass().add("custom-alert");
                    alert.showAndWait();

                    next();
                    
                });
                */
            }
            else {
                /*
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Game was cancelled");
                    alert.setHeaderText("All players disconnected");
                    alert.setContentText("You will return to the main menu");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/original/css/style.css").toExternalForm());
                    alert.getDialogPane().getStyleClass().add("custom-alert");
                    alert.showAndWait();

                    next();
                });
                */
            }
            
        });
        _gameThread.start();
    }

    private void initialize() {
        
        _listPlayerStackPanes = List.of(
            pokerPlayer0,
            pokerPlayer1,
            pokerPlayer2,
            pokerPlayer3,
            pokerPlayer4,
            pokerPlayer5,
            pokerPlayer6,
            pokerPlayer7,
            pokerPlayer8
        );

        _listNameLabels = List.of(
            playerName0,
            playerName1,
            playerName2,
            playerName3,
            playerName4,
            playerName5,
            playerName6,
            playerName7,
            playerName8
        );

        _listMoneyLabels = List.of(
            playerMoney0,
            playerMoney1,
            playerMoney2,
            playerMoney3,
            playerMoney4,
            playerMoney5,
            playerMoney6,
            playerMoney7,
            playerMoney8
        );

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

        _listAvatarProfiles = List.of(
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

        _listDealer = List.of(
            ImgDealer0,
            ImgDealer1,
            ImgDealer2,
            ImgDealer3,
            ImgDealer4,
            ImgDealer5,
            ImgDealer6,
            ImgDealer7,
            ImgDealer8
        );

        _listHandBet = List.of(
            hboxHandBet0,
            hboxHandBet1,
            hboxHandBet2,
            hboxHandBet3,
            hboxHandBet4,
            hboxHandBet5,
            hboxHandBet6,
            hboxHandBet7,
            hboxHandBet8
        );

        _listOnBetMoney = List.of(
            labelOnBetMoney0,
            labelOnBetMoney1,
            labelOnBetMoney2,
            labelOnBetMoney3,
            labelOnBetMoney4,
            labelOnBetMoney5,
            labelOnBetMoney6,
            labelOnBetMoney7,
            labelOnBetMoney8
        );

        _listEquity = List.of(
            labelEquity0,
            labelEquity1,
            labelEquity2,
            labelEquity3,
            labelEquity4,
            labelEquity5,
            labelEquity6,
            labelEquity7,
            labelEquity8
        );
    } 

    private void GUI_initializePlayersInfo() {
        _listNameLabels.forEach(label -> label.setText("") );
        _listMoneyLabels.forEach(label -> label.setText("") );
        _listPlayerStackPanes.forEach(stack -> stack.setVisible(false));

        _listImageCards.forEach(hbox -> hbox.setVisible(false));
        _listHandBet.forEach(hbox -> hbox.setVisible(false));
         _listEquity.forEach(lb -> lb.setVisible(false));
    }

    private void GUI_initializeCardStyle() { 
        Image cardImage = new Image(getClass().getResource(_clientInfo.gameConfig._selectedCard).toExternalForm());
        _listPaintCards.subList(2, _listPaintCards.size()).forEach(iv -> iv.setImage(cardImage));
    }

    private void GUI_initializeDealerButton() {
        
        Image DealerImage = new Image(getClass().getResource("/images/fichaDealer.png").toExternalForm());
        _listDealer.forEach(iv -> {
            iv.setImage(DealerImage);
            iv.setVisible(false);
        });
    }

    private void GUI_initializeMoneySlider() {   
        sliderMoney.setMin(0);
        sliderMoney.setValue(0);

        sliderMoney.valueProperty().addListener((obs, oldVal, newVal) -> {
            
            labelMoney.setText( String.valueOf(newVal.intValue()) );

            if( newVal.intValue() == sliderMoney.getMin() )
                btnRaise.setDisable(true);
            else
                btnRaise.setDisable(false);
        });
    }

    private void GUI_showWaitingPlayers(final List<PlayerInfo> players) {

        int myIndex = IntStream.range(0, players.size())
                        .filter(i -> players.get(i).id == _clientInfo.id)
                        .findFirst()
                        .orElse(-1);

        if(myIndex == -1){
            System.out.printf("We are not in the list! Something is wrong...\n");
            return;
        }


        _playerSeatMap = new HashMap<>(players.size());
        _playerSeatMap.put(myIndex, 0);

        GUI_getAvatarPosition(0, _clientInfo.name);
        playerName0.setText( _clientInfo.name );
        playerMoney0.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
        pokerPlayer0.setVisible(true);
        _listImageCards.get(0).setVisible(true);
        _listHandBet.get(0).setVisible(true);

        // Show players behind me(in the list) : position 1, 2, 3, ...
        int beforePosition = 1;
        for(int i = myIndex - 1; 0 <= i; i--) {

            PlayerInfo p = players.get(i);
            _playerSeatMap.put(p.id, beforePosition);

            Label nameLabel = _listNameLabels.get(beforePosition);
            Label moneyLabel = _listMoneyLabels.get(beforePosition);
            StackPane playerStackPane = _listPlayerStackPanes.get(beforePosition);
            HBox cardsHBox = _listImageCards.get(beforePosition);
            HBox onBetHBox = _listHandBet.get(beforePosition);
            
            nameLabel.setText(p.name);
            moneyLabel.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
            playerStackPane.setVisible(true);
            cardsHBox.setVisible(true);
            onBetHBox.setVisible(true);
            GUI_getAvatarPosition(beforePosition, p.name);

            ++beforePosition;
        }

        // Show players ahead of me(in the list) : position 8, 7, 6, ...
        int nextPosition = 8;
        for(int i = myIndex + 1; i < players.size(); i++) {

            PlayerInfo p = players.get(i);
            _playerSeatMap.put(p.id, nextPosition);

            Label nameLabel = _listNameLabels.get(nextPosition);
            Label moneyLabel = _listMoneyLabels.get(nextPosition);
            StackPane playerStackPane = _listPlayerStackPanes.get(nextPosition);
            HBox cardsHBox = _listImageCards.get(nextPosition);
            HBox onBetHBox = _listHandBet.get(nextPosition);
            
            nameLabel.setText(p.name);
            moneyLabel.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
            playerStackPane.setVisible(true);
            cardsHBox.setVisible(true);
            onBetHBox.setVisible(true);
            GUI_getAvatarPosition(nextPosition, p.name);

            --nextPosition;
        }
    }

    
    @FXML
    private void foldAction() {

        if(!_commandQueue.isEmpty())
            return;

        System.out.printf("FOLD BUTTON\n");
        _commandQueue.offer(GameType.FOLD_ACTION_FULL);
    }

    @FXML
    private void callAction() {

        if(!_commandQueue.isEmpty())
            return;

        if(_swapCallToCheck) {
            System.out.printf("CALL BUTTON\n");
            _commandQueue.offer(GameType.CHECK_ACTION_FULL);
        }
        else {
            System.out.printf("CHECK BUTTON\n");
            _commandQueue.offer(GameType.CALL_ACTION_FULL);
        }
    }

    @FXML
    private void raiseAction() {

        if(!_commandQueue.isEmpty())
            return;

        System.out.printf("RAISE BUTTON\n");
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

        int playerRankingsCode;
		int gameStatusCode;
        PlayerRole role;
		Card[] playerCards = new Card[2];
		Card[] tableCardValues = new Card[5];
        try {

            InputStream input = socket.getInputStream();
            boolean endOfGame = false;
            while(!endOfGame) {

                try {

                    //clearTableCards();

                    // Player role and cards
                    role = PokerGame.receivePlayerRole(input);
                    playerCards[0] = PokerGame.receiveCard(input);
                    playerCards[1] = PokerGame.receiveCard(input);
                    System.out.printf("Assigned role: %s\n", role.toString());
                    //GUI_showCards(imgLeftCard0, playerCards[0]);
                    //GUI_showCards(imgRightCard0, playerCards[1]);
                    
                    
                    // Preflop
                    System.out.printf("-- Preflop --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("PREFLOP");
                    });
                    playRound(playerCards[0], playerCards[1], role, socket);
                    tableCardValues[0] = PokerGame.receiveCard(input);  // First table card
                    tableCardValues[1] = PokerGame.receiveCard(input);  // Second table card
                    tableCardValues[2] = PokerGame.receiveCard(input);  // Third table card
                    //GUI_showCards(tableCard0, tableCardValues[0]);
                    //GUI_showCards(tableCard1, tableCardValues[1]);
                    //GUI_showCards(tableCard2, tableCardValues[2]);


                    // Flop
                    System.out.printf("-- Flop --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("FLOP");
                    });
                    playRound(playerCards[0], playerCards[1], role, socket);
                    tableCardValues[3] = PokerGame.receiveCard(input);  // Fourth table card
                    //GUI_showCards(tableCard3, tableCardValues[3]);


                    // Turn
                    System.out.printf("-- Turn --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("TURN");
                    });
                    playRound(playerCards[0], playerCards[1], role, socket);
                    tableCardValues[4] = PokerGame.receiveCard(input);  // fifth table card
                    //GUI_showCards(tableCard4, tableCardValues[4]);


                    // River
                    System.out.printf("-- River --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("RIVER");
                    });
                    playRound(playerCards[0], playerCards[1], role, socket);


                    // Showdown
                    System.out.printf("-- Showdown --\n");
                    Platform.runLater(() -> {
                        btnRound.setText("SHOWDOWN");
                    });
                    playerRankingsCode = SocketUtils.receiveInt(input);
                    if(playerRankingsCode == GameType.PLAYER_RANKINGS) {
                     
                        int numWinners = SocketUtils.receiveInt(input);
                        for(int i = 0; i < numWinners; i++) {
                            int winnerID = SocketUtils.receiveInt(input);
                            String winnerName = SocketUtils.receiveString(input);
                            int winnerCurrentMoney = SocketUtils.receiveInt(input);

                            System.out.printf(
                                "Player %s[%d] won this hand! Current money is %d\n",
                                winnerName, winnerID, winnerCurrentMoney
                            );
                        }
                    }
                    else {
                        System.out.printf("Error: expecting PLAYER_RANKINGS code and server responded %d\n", playerRankingsCode);
                        return false;
                    }
                    

                    int sleep_seconds = 3;
                    System.out.printf("%d seconds pause to see the winner...\n", sleep_seconds);
                    Thread.sleep(sleep_seconds * 1000);

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
                        //receivePlayersUpdatedInfo(socket);

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

            if(_userCloses) {
                return true;
            }
            else {
                System.out.printf("Error on client socket: %s\n", e.getMessage());
                return false;
            }
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

			}
			else if(serverCode == GameType.TURN_FORCED_BB) {

				final int amountBB = SocketUtils.receiveInt(socket.getInputStream());
                final int onBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                final int offBetMoney = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the big blind with %d chips\n", amountBB);

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
                final String otherPlayerLastCommand = SocketUtils.receiveString( socket.getInputStream() );
                final int otherPlayerOffBetMoney = SocketUtils.receiveInt( socket.getInputStream() );
                final int otherPlayerOnBetMoney = SocketUtils.receiveInt( socket.getInputStream() );

                System.out.printf(
                    "Player %s action - PlayerID: %d, Action: %s, OffBetMoney: %d, OnBetMoney: %d\n",
                    otherPlayerName,
                    otherPlayerID,
                    otherPlayerLastCommand,
                    otherPlayerOffBetMoney, otherPlayerOnBetMoney
                );

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
    

    private void GUI_updateDealer(int seatID) {
        _listDealer.forEach(iv -> iv.setVisible(false));
        _listDealer.get(seatID).setVisible(true);
    }

    private void GUI_receivePlayersUpdatedInfo(Socket socket) throws IOException {

        InputStream input = socket.getInputStream();

        int isLast = 0;
        do {
            int playerID = SocketUtils.receiveInt(input);
            PlayerRole r = PokerGame.receivePlayerRole(input);
            boolean isFolded = SocketUtils.receiveInt(input) == GameType.TRUE;
            boolean isWinner = SocketUtils.receiveInt(input) == GameType.TRUE;
            String lastCommand = SocketUtils.receiveString(input);
            int offBetMoney = SocketUtils.receiveInt(input);
            int onBetMoney = SocketUtils.receiveInt(input);
            String equity = SocketUtils.receiveString(input);

            /*
            System.out.printf(
                "UPDATE - Player ID: %d, Role: %s, Off-Bet Money: %d, On-Bet Money: %d, Folded: %b, Winner: %b, Last Command: %s\n", 
                playerID, r.name(), offBetMoney, onBetMoney, isFolded, isWinner, lastCommand
            );
            */

            int seatID = _playerSeatLabel.get(playerID).intValue();
            GUI_updatePlayerInfo(seatID, r, onBetMoney, offBetMoney, isFolded, isWinner, equity);
            _listEquity.get(seatID).setText(equity);

            isLast = SocketUtils.receiveInt(input);
        }
        while(isLast != GameType.TRUE);
    }

    private void GUI_updatePlayerInfo(int seatID, PlayerRole role, int onBetMoney, int offBetMoney, boolean isFolded, boolean isWinner, String equity) {

        Label nameLabel = null;
        Label moneyLabel = null;
        Label betLabel = _listOnBetMoney.get(seatID);
        Label equityLabel = _listEquity.get(seatID);

        if(role == PlayerRole.DEALER) {
            GUI_updateDealer(seatID);
        }

        PauseTransition transition = new PauseTransition(Duration.seconds(3));
        if(isWinner) {
            betLabel.setText( String.valueOf(onBetMoney) );
            nameLabel.setText( nameLabel.getText() + " (Winner!)" );
            transition.setOnFinished(event -> {
                nameLabel.setText( nameLabel.getText().replace(" (Winner!)", "") );
            });
            transition.play();

            System.out.printf("Player %s is the winner of the hand!\n", nameLabel.getText());
        }

        moneyLabel.setText( String.valueOf(offBetMoney) );
    }
    
    private void GUI_getAvatarPosition(final int position, String name) {
        ImageView avatarImage = _listAvatarProfiles.get(position);
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

    private void GUI_showCards(ImageView imageView, Card card) {

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
        imageView.setImage( cardImage );
    }

    
    private void GUI_clearTableCards() {
        tableCard0.setImage(null);
        tableCard1.setImage(null);
        tableCard2.setImage(null);
        tableCard3.setImage(null);
        tableCard4.setImage(null);
    }

    private void GUI_clearPlayerBets() {
        _listOnBetMoney.forEach(label -> label.setText("0"));
    }

}