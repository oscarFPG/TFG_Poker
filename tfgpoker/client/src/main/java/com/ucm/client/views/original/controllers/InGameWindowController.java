package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.ucm.client.utils.Messages;
import com.ucm.client.utils.NotificationManager;
import com.ucm.common.GameType;
import com.ucm.common.PlayerInfo;
import com.ucm.common.PokerGame;
import com.ucm.common.SocketUtils;
import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class InGameWindowController extends GenericController {

    /* Constants */
    private static final float DEFAULT_OPACITY = 1.0f;
    private static final float FOLDED_OPACITY = 0.6f;
    private static final float ELIMINATED_OPACITY = 0.4f;
    

    /* Player info */
    @FXML private Label usernamePlaceHolder;
    @FXML private ImageView imgAvatarProfile;
    @FXML private HBox hboxMenuItems;

    /* Holder + Buttons */
    @FXML private VBox buttonsHolder;
    @FXML private Button btnFold, btnCall, btnRaise;
    @FXML private Button btnRound, btnMinBet, btnHalfBet, btnMaxBet;
    @FXML private Button btnDecreaseMoney, btnIncreaseMoney;
    @FXML private Button btnMenu;
    @FXML private ToggleButton btnEquity;
    @FXML private ImageView imgSeeEquity;

    /* Money buttons, labels and slider */
    @FXML private Label labelMoney;
    @FXML private Slider sliderMoney;

    /* Table info */
    @FXML private Label labelTotalPot;
    @FXML private ImageView imgTableInGame;
    @FXML private ImageView tableCard0, tableCard1, tableCard2, tableCard3, tableCard4;
    
    /* All poker players seats */
    @FXML private StackPane pokerPlayer0;           // Always playing-client seat
    @FXML private Label playerName0, playerMoney0, labelOnBetMoney0;  // Always playing-client seat
    @FXML private Label labelEquity0;
    @FXML private HBox hboxImageCards0, hboxHandBet0, hboxTimer0;
    @FXML private ImageView imgLeftCard0, imgRightCard0, imgAvatarProfile0;
    @FXML private ImageView ImgDealer0;
    @FXML private Rectangle rectFirstTimer0, rectSecondTimer0, rectThirdTimer0, rectFourthTimer0, rectFifthTimer0, rectSixthTimer0;

    @FXML private StackPane pokerPlayer1;
    @FXML private Label playerName1, playerMoney1, labelOnBetMoney1;
    @FXML private Label labelEquity1;
    @FXML private HBox hboxImageCards1, hboxHandBet1, hboxTimer1;
    @FXML private ImageView imgLeftCard1, imgRightCard1, imgAvatarProfile1;
    @FXML private ImageView ImgDealer1;
    @FXML private Rectangle rectFirstTimer1, rectSecondTimer1, rectThirdTimer1, rectFourthTimer1, rectFifthTimer1, rectSixthTimer1;

    @FXML private StackPane pokerPlayer2;
    @FXML private Label playerName2, playerMoney2, labelOnBetMoney2;
    @FXML private Label labelEquity2;
    @FXML private HBox hboxImageCards2, hboxHandBet2, hboxTimer2;
    @FXML private ImageView imgLeftCard2, imgRightCard2, imgAvatarProfile2;
    @FXML private ImageView ImgDealer2;
    @FXML private Rectangle rectFirstTimer2, rectSecondTimer2, rectThirdTimer2, rectFourthTimer2, rectFifthTimer2, rectSixthTimer2;

    @FXML private StackPane pokerPlayer3;
    @FXML private Label playerName3, playerMoney3, labelOnBetMoney3;
    @FXML private Label labelEquity3;
    @FXML private HBox hboxImageCards3, hboxHandBet3, hboxTimer3;
    @FXML private ImageView imgLeftCard3, imgRightCard3, imgAvatarProfile3;
    @FXML private ImageView ImgDealer3;
    @FXML private Rectangle rectFirstTimer3, rectSecondTimer3, rectThirdTimer3, rectFourthTimer3, rectFifthTimer3, rectSixthTimer3;

    @FXML private StackPane pokerPlayer4;
    @FXML private Label playerName4, playerMoney4, labelOnBetMoney4;
    @FXML private Label labelEquity4;
    @FXML private HBox hboxImageCards4, hboxHandBet4, hboxTimer4;
    @FXML private ImageView imgLeftCard4, imgRightCard4, imgAvatarProfile4;
    @FXML private ImageView ImgDealer4;
    @FXML private Rectangle rectFirstTimer4, rectSecondTimer4, rectThirdTimer4, rectFourthTimer4, rectFifthTimer4, rectSixthTimer4;

    @FXML private StackPane pokerPlayer5;
    @FXML private Label playerName5, playerMoney5, labelOnBetMoney5;
    @FXML private Label labelEquity5;
    @FXML private HBox hboxImageCards5, hboxHandBet5, hboxTimer5;
    @FXML private ImageView imgLeftCard5, imgRightCard5, imgAvatarProfile5;
    @FXML private ImageView ImgDealer5;
    @FXML private Rectangle rectFirstTimer5, rectSecondTimer5, rectThirdTimer5, rectFourthTimer5, rectFifthTimer5, rectSixthTimer5;

    @FXML private StackPane pokerPlayer6;
    @FXML private Label playerName6, playerMoney6, labelOnBetMoney6;
    @FXML private Label labelEquity6;
    @FXML private HBox hboxImageCards6, hboxHandBet6, hboxTimer6;
    @FXML private ImageView imgLeftCard6, imgRightCard6, imgAvatarProfile6;
    @FXML private ImageView ImgDealer6;
    @FXML private Rectangle rectFirstTimer6, rectSecondTimer6, rectThirdTimer6, rectFourthTimer6, rectFifthTimer6, rectSixthTimer6;

    @FXML private StackPane pokerPlayer7;
    @FXML private Label playerName7, playerMoney7, labelOnBetMoney7;
    @FXML private Label labelEquity7;
    @FXML private HBox hboxImageCards7, hboxHandBet7, hboxTimer7;
    @FXML private ImageView imgLeftCard7, imgRightCard7, imgAvatarProfile7;
    @FXML private ImageView ImgDealer7;
    @FXML private Rectangle rectFirstTimer7, rectSecondTimer7, rectThirdTimer7, rectFourthTimer7, rectFifthTimer7, rectSixthTimer7;

    @FXML private StackPane pokerPlayer8;
    @FXML private Label playerName8, playerMoney8, labelOnBetMoney8;
    @FXML private Label labelEquity8;
    @FXML private HBox hboxImageCards8, hboxHandBet8, hboxTimer8;
    @FXML private ImageView imgLeftCard8, imgRightCard8, imgAvatarProfile8;
    @FXML private ImageView ImgDealer8;
    @FXML private Rectangle rectFirstTimer8, rectSecondTimer8, rectThirdTimer8, rectFourthTimer8, rectFifthTimer8, rectSixthTimer8;
    
    /* Member variables to group all players variables */
    private List<StackPane> _listPlayerStackPanes;  // Player stackpanes
    private List<Label> _listNameLabels, _listMoneyLabels; // Player names and money OFF bet
    private List<HBox> _listHandBet;    // Player money ON bet + chips image
    private List<Label> _listOnBetMoney;    // Player money ON bet number
    private List<Label> _listEquity;    // Player equities
    private List<HBox> _listImageCards;     // Player cards
    private List<ImageView> _listPaintCards, _listAvatarProfiles; // Player cards images and avatar
    private List<ImageView> _listDealer;    // Player dealer chip
    private Map<Integer, List<Rectangle>> _listTimer; // Players timer
    

    /* Variables to add logic */
    // Command queue to save user actions via buttons - Avoid multiple sends cause by multiple clicks
    private BlockingQueue<String> _commandQueue = new LinkedBlockingQueue<>();
    private Thread _gameThread; // Receiving game logic thread
    private Map<Integer, Integer> _playerSeatMap; // Player id = i -> list[i] = m -> m stackpane label
    private boolean _swapCallToCheck = false;   // Flag to change call button for check button
    private boolean _userCloses = false;    // Flag for user closes window
    private boolean _menuOpen = true;       // Flag to open/close top menu
    private boolean _equityVisible = false; // Flag to show/hide player equity
    private String _myEquity = "0%";

    // Images to show if equity is being displayed
    private final Image equityOn = new Image(getClass().getResource("/images/seeStatistic.png").toExternalForm());
    private final Image equityOff = new Image(getClass().getResource("/images/notSeeStatistic.png").toExternalForm());
    
    private int _turnTimerTotal;
    private int _blockTime;
    private static final int TOTAL_BLOCKS = 6;
    private ScheduledFuture<?> _task;
    private final ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor();

    private Integer _timerPlayerId = null;
    private int _secondsLeft;
    
    @Override
    protected void onViewShown() {

        initialize();
        GUI_initializePlayersInfo();
        GUI_flipDownAllPlayerCards();
        GUI_initializeDealerButton();
        GUI_initializeMoneySlider();
        GUI_initializeTimers();
        GUI_showWaitingPlayers(_clientInfo.playerPositions);
        GUI_clearTableCards();
        GUI_clearPlayerBets();
        GUI_clearDealer();
        GUI_clearTurnPlayer();
        GUI_initializeTurnTimer();

        usernamePlaceHolder.setText( _clientInfo.name );
        imgAvatarProfile.setImage( _clientInfo.getAvatar(_clientInfo.name,64) );
        imgTableInGame.setImage(new Image(getClass().getResource(_clientInfo.gameConfig._selectedTable).toExternalForm()));
        
        _stage.setOnCloseRequest(event -> {

            try {
                _userCloses = true;

                GUI_shutdownVisualTimer();

                if(_clientInfo.socket != null && !_clientInfo.socket.isClosed())
                    _clientInfo.socket.close();

                if(_gameThread != null && _gameThread.isAlive())
                    _gameThread.interrupt();
            }
            catch (IOException e) {
                System.out.printf("Error closing socket: %s\n", e.getMessage());
                NotificationManager.showError(Messages.Notifications.ERROR_CLOSING_SOCKET + e.getMessage());
            }
        });

        _gameThread = new Thread(() -> {

            if(_clientInfo.isHost && _clientInfo.gameConfig._joinedAsSpectator) {
                spectateGame(_clientInfo.socket);
            }
            else {

                boolean ok = pokerGame(_clientInfo.name, _clientInfo.socket);
                if(ok) {
                    System.out.printf("All OK! Game finished!\n");
                    NotificationManager.showSuccess(Messages.Notifications.CONFIRMATION_GAME_FINISHED);
                }
                else {
                    NotificationManager.showError(Messages.Notifications.ERROR_GAME_CANCELED_BY_SERVER);
                }
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
        _listHandBet.get(0).setOpacity(1);
        _listEquity.forEach(label -> { label.setVisible(false); label.setText("0%");});
    }

    private void GUI_flipDownAllPlayerCards() { 
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
        sliderMoney.setMax(1_000_000);

        sliderMoney.valueProperty().addListener((obs, oldVal, newVal) -> {
            
            labelMoney.setText( String.valueOf(newVal.intValue()) );

            if( newVal.intValue() == sliderMoney.getMin() )
                btnRaise.setDisable(true);
            else
                btnRaise.setDisable(false);
        });
    }

    private void GUI_initializeTimers() {

        _listTimer = new HashMap<>();

        _listTimer.put(0, List.of(rectFirstTimer0, rectSecondTimer0, rectThirdTimer0, rectFourthTimer0, rectFifthTimer0, rectSixthTimer0));
        _listTimer.put(1, List.of(rectFirstTimer1, rectSecondTimer1, rectThirdTimer1, rectFourthTimer1, rectFifthTimer1, rectSixthTimer1));
        _listTimer.put(2, List.of(rectFirstTimer2, rectSecondTimer2, rectThirdTimer2, rectFourthTimer2, rectFifthTimer2, rectSixthTimer2));
        _listTimer.put(3, List.of(rectFirstTimer3, rectSecondTimer3, rectThirdTimer3, rectFourthTimer3, rectFifthTimer3, rectSixthTimer3));
        _listTimer.put(4, List.of(rectFirstTimer4, rectSecondTimer4, rectThirdTimer4, rectFourthTimer4, rectFifthTimer4, rectSixthTimer4));
        _listTimer.put(5, List.of(rectFirstTimer5, rectSecondTimer5, rectThirdTimer5, rectFourthTimer5, rectFifthTimer5, rectSixthTimer5));
        _listTimer.put(6, List.of(rectFirstTimer6, rectSecondTimer6, rectThirdTimer6, rectFourthTimer6, rectFifthTimer6, rectSixthTimer6));
        _listTimer.put(7, List.of(rectFirstTimer7, rectSecondTimer7, rectThirdTimer7, rectFourthTimer7, rectFifthTimer7, rectSixthTimer7));
        _listTimer.put(8, List.of(rectFirstTimer8, rectSecondTimer8, rectThirdTimer8, rectFourthTimer8, rectFifthTimer8, rectSixthTimer8));

        for(List<Rectangle> rectangles : _listTimer.values()) {
            for(Rectangle r : rectangles) {
                r.setVisible(false);
            }
        }
    }

    private void GUI_initializeTurnTimer() {
        _turnTimerTotal = Integer.parseInt(_clientInfo.gameConfig._turnTimerPlayer);
        _blockTime = _turnTimerTotal / TOTAL_BLOCKS;
    }

    private void GUI_showWaitingPlayers(final List<PlayerInfo> players) {

        int myIndex = IntStream.range(0, players.size())
                        .filter(i -> players.get(i).id == _clientInfo.id)
                        .findFirst()
                        .orElse(-1);

        _playerSeatMap = new HashMap<>(players.size());
        if(myIndex == -1) {
         
            int seatIndex = 0;
            for(int i = players.size() - 1; 0 <= i; i--) {

                PlayerInfo p = players.get(i);
                _playerSeatMap.put(p.id, seatIndex);

                Label nameLabel = _listNameLabels.get(seatIndex);
                Label moneyLabel = _listMoneyLabels.get(seatIndex);
                StackPane playerStackPane = _listPlayerStackPanes.get(seatIndex);
                HBox cardsHBox = _listImageCards.get(seatIndex);
                HBox onBetHBox = _listHandBet.get(seatIndex);

                nameLabel.setText(p.name);
                moneyLabel.setText( String.valueOf(_clientInfo.gameConfig._initialMoney) );
                playerStackPane.setVisible(true);
                cardsHBox.setVisible(true);
                onBetHBox.setVisible(true);
                GUI_getAvatarPosition(seatIndex, p.name);

                seatIndex++;
            }

            return;
        }

        
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


        _commandQueue.offer(GameType.FOLD_ACTION_FULL);
    }

    @FXML
    private void callAction() {

        if(!_commandQueue.isEmpty())
            return;

        
        if(_swapCallToCheck) {
            System.out.printf("CHECK BUTTON\n");
            _commandQueue.offer(GameType.CHECK_ACTION_FULL);
            //NotificationManager.showSuccess(Messages.Notifications.CHECK_ACTION_FULL);
        }
        else {
            System.out.printf("CALL BUTTON\n");
            _commandQueue.offer(GameType.CALL_ACTION_FULL);
             //NotificationManager.showSuccess(Messages.Notifications.CALL_ACTION_FULL);
        }
    }

    @FXML
    private void raiseAction() {

        if(!_commandQueue.isEmpty())
            return;


        int amount = Integer.parseInt( labelMoney.getText() );
        _commandQueue.offer( String.format("%s %d", GameType.RAISE_ACTION_FULL, amount) );
       // NotificationManager.showSuccess(Messages.Notifications.RAISE_ACTION_FULL + amount);
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

    @FXML
    private void openMenu() {
        _menuOpen = !_menuOpen;
        
        hboxMenuItems.setVisible(_menuOpen);
    }

    @FXML
    private void seeEquity() {
        _equityVisible = btnEquity.isSelected();
        imgSeeEquity.setImage(_equityVisible ? equityOn : equityOff);
        GUI_putEquityToPlayer();
    }


    private void spectateGame(Socket socket) {

        Card[] tableCards = new Card[5];
        int cardCounter = 0;
        int serverCode;
        try {
            
            do {

                serverCode = SocketUtils.receiveInt(socket.getInputStream());
                if(serverCode == GameType.OTHER_PLAYER_STATUS) {
                    System.out.printf("Waiting other player status!\n");

                    int playerID = SocketUtils.receiveInt(socket.getInputStream());
                    String playerName = SocketUtils.receiveString(socket.getInputStream());
                    PlayerRole role = PokerGame.receivePlayerRole( socket.getInputStream() );
                    boolean isFolded = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                    boolean isWinner = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                    boolean isEliminated = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                    int moneyOffBet = SocketUtils.receiveInt(socket.getInputStream());
                    int moneyOnBet = SocketUtils.receiveInt(socket.getInputStream());

                    Platform.runLater(() -> {
                        GUI_updatePlayerInfo(playerID, role, moneyOnBet, moneyOffBet, isFolded, isWinner, isEliminated, true);
                    });
                }
                else if(serverCode == GameType.TABLE_CARD) {
                    System.out.printf("Waiting a table card!\n");

                    Card card = PokerGame.receiveCard(socket.getInputStream());
                    final int counter = cardCounter;
                    tableCards[cardCounter++] = card;

                    Platform.runLater(() -> {

                        if(counter == 0) {
                            GUI_showCard(tableCard0, tableCards[0]);
                        }
                        else if(counter == 1) {
                            GUI_showCard(tableCard1, tableCards[1]);
                        }
                        else if(counter == 2) {
                            GUI_showCard(tableCard2, tableCards[2]);
                        }
                        else if(counter == 3) {
                            GUI_showCard(tableCard3, tableCards[3]);
                        }
                        else if(counter == 4) {
                            GUI_showCard(tableCard4, tableCards[4]);
                        }
                        else {
                            System.out.printf("Error with cards counter\n");
                        }
                    });

                    System.out.printf("Received table card: %s", card.toLetterString());
                }
                else if(serverCode == GameType.TOTAL_POT) {
                    System.out.printf("Waiting current total pot!\n");

                    final int totalPot = SocketUtils.receiveInt(socket.getInputStream());
                    Platform.runLater(() -> {
                        labelTotalPot.setText( String.valueOf(totalPot) );
                    });
                }
                else if(serverCode == GameType.TURN_BEFORE_PLAY) {
                    System.out.printf("Waiting to know which player is next!\n");

                    int playerID = SocketUtils.receiveInt(socket.getInputStream());
                    GUI_putTurnPlayer(playerID);

                    Platform.runLater(() -> {
                        buttonsHolder.setVisible(false);
                        GUI_startVisualTimer(playerID);
                    });
                }
                else if(serverCode == GameType.TURN_OTHER_PLAYER) {
                    System.out.printf("Waiting to receive other player current action!\n");

                    int otherPlayerID = SocketUtils.receiveInt( socket.getInputStream() );
                    String otherPlayerName = SocketUtils.receiveString( socket.getInputStream() );
                    PlayerRole otherPlayerRole = PokerGame.receivePlayerRole( socket.getInputStream() );
                    boolean otherPlayerIsFolded = SocketUtils.receiveInt( socket.getInputStream() ) == GameType.TRUE;
                    boolean otherPlayerIsWinner = SocketUtils.receiveInt( socket.getInputStream() ) == GameType.TRUE;
                    String otherPlayerLastCommand = SocketUtils.receiveString( socket.getInputStream() );
                    int otherPlayerOffBetMoney = SocketUtils.receiveInt( socket.getInputStream() );
                    int otherPlayerOnBetMoney = SocketUtils.receiveInt( socket.getInputStream() );

                    Platform.runLater(() -> {

                        GUI_stopVisualTimer();
                        GUI_putPlayerBet(otherPlayerID, otherPlayerOnBetMoney, otherPlayerOffBetMoney, otherPlayerIsFolded);
                        
                        int seatID = _playerSeatMap.get(otherPlayerID);
                        _listPlayerStackPanes.get(seatID).getStyleClass().remove("tourn-player-color");
                    });
                }
                else if(serverCode == GameType.HAND_ENDS_BY_FOLD) {

                    System.out.printf("Hand ended because all players except one folded\n");
                    Platform.runLater(() -> {
                        buttonsHolder.setVisible(false);
                        GUI_stopVisualTimer();
                    });
                }
                else if(serverCode == GameType.ROUND_ENDS) {
                    System.out.printf("Round ends!\n");

                    Platform.runLater(() -> {
                        buttonsHolder.setVisible(false);
                        GUI_stopVisualTimer();
                        GUI_clearPlayerBets();
                    });
                }
                else if(serverCode == GameType.GAME_ENDS) {
                    System.out.printf("Game ends!\n");

                    Platform.runLater(() -> {
                        buttonsHolder.setVisible(false);
                        GUI_stopVisualTimer();
                        GUI_clearPlayerBets();
                    });
                }
                else if(serverCode == GameType.GAME_KEEPS) {
                    System.out.printf("Game keeps!\n");

                    Platform.runLater(() -> {
                        buttonsHolder.setVisible(false);
                        GUI_stopVisualTimer();
                        GUI_clearPlayerBets();
                    });
                }
                else {
                    System.out.printf("Server response %d unknown\n", serverCode);
                }
            }
            while(serverCode != GameType.GAME_ENDS);

        }
        catch (IOException e) {
            System.out.printf("Error spectating game: %s\n", e.getMessage());
        }
    }

    private boolean pokerGame(String name, Socket socket) {

        PlayerRole role;
		Card[] playerCards = new Card[2];
		Card[] tableCards = new Card[5];
        try {

            InputStream input = socket.getInputStream();
            boolean endOfGame = false;
            while(!endOfGame) {

                try {

                    Platform.runLater(() -> {
                        GUI_clearTableCards();
                        GUI_flipDownAllPlayerCards();
                        GUI_clearPlayerBets();
                        GUI_clearDealer();
                        GUI_clearTurnPlayer();
                    });

                    // Player roles
                    System.out.printf("-- New hand --\n");
                    role = PokerGame.receivePlayerRole(input);
                    playerStartInfo(socket);
                    NotificationManager.showSuccess(Messages.Notifications.WAITING_GAME_START);
                    

                    // Player cards
                    playerCards[0] = PokerGame.receiveCard(input);
                    playerCards[1] = PokerGame.receiveCard(input);
                    handleEquity(socket);
                    System.out.printf("My cards: %s %s\n", 
                        playerCards[0].toString(), playerCards[1].toString()
                    );
                    Platform.runLater(() -> {
                        GUI_putMyCards(_clientInfo.id, playerCards[0], playerCards[1]);
                        GUI_putTurnPlayer(_clientInfo.id);
                    });

                    
                    // Preflop
                    System.out.printf("-- Preflop --\n");
                    Platform.runLater(() -> {
                        GUI_putRoundName("PREFLOP");
                    });
                    playRound(socket);
                    tableCards[0] = PokerGame.receiveCard(input);  // First table card
                    tableCards[1] = PokerGame.receiveCard(input);  // Second table card
                    tableCards[2] = PokerGame.receiveCard(input);  // Third table card
                    handleEquity(socket);
                    Platform.runLater(() -> {
                        GUI_showCard(tableCard0, tableCards[0]);
                        GUI_showCard(tableCard1, tableCards[1]);
                        GUI_showCard(tableCard2, tableCards[2]);
                    });

                    
                    // Flop
                    System.out.printf("-- Flop --\n");
                    Platform.runLater(() -> {
                        GUI_putRoundName("FLOP");
                        GUI_clearPlayerBets();
                    });
                    playRound(socket);
                    tableCards[3] = PokerGame.receiveCard(input);  // Fourth table card
                    handleEquity(socket);
                    Platform.runLater(() -> {
                        GUI_showCard(tableCard3, tableCards[3]);
                    });


                    // Turn
                    System.out.printf("-- Turn --\n");
                    Platform.runLater(() -> {
                        GUI_putRoundName("TURN");
                        GUI_clearPlayerBets();
                    });
                    playRound(socket);
                    tableCards[4] = PokerGame.receiveCard(input);  // fifth table card
                    handleEquity(socket);
                    Platform.runLater(() -> {
                        GUI_showCard(tableCard4, tableCards[4]);
                    });


                    // River
                    System.out.printf("-- River --\n");
                    Platform.runLater(() -> {
                        GUI_putRoundName("RIVER");
                        GUI_clearPlayerBets();
                    });
                    playRound(socket);


                    // Showdown
                    System.out.printf("-- Showdown --\n");
                    Platform.runLater(() -> {
                        GUI_putRoundName("SHOWDOWN");
                    });
                    endOfGame = showdown(socket);
                }
                catch (OnlyOnePlayerLeftException e) {

                    System.out.printf("There is only one player left!\n");
                    NotificationManager.showError(Messages.Notifications.ERROR_ONLY_ONE_PLAYER_LEFT);
                    
                    try {
                        endOfGame = showdown(socket);
                    }
                    catch(IOException ex) {
                        System.out.printf("Error receiving the rank after a fold exception: %s", ex.getMessage());
                        NotificationManager.showError(Messages.Notifications.ERROR_RECEIVING_RANK + ex.getMessage());
                    }
                    catch(InterruptedException ex) {
                        System.out.printf("Game thread interrupted: %s\n", ex.getMessage());
                        NotificationManager.showError(Messages.Notifications.ERROR_THREAD_INTERRUPTED + ex.getMessage());
                    }

                }
                catch(InterruptedException e) {
                    System.out.printf("Game thread interrupted: %s\n", e.getMessage());
                    NotificationManager.showError(Messages.Notifications.ERROR_THREAD_INTERRUPTED + e.getMessage());
                }

            }
        }
        catch(IOException e) {

            if(_userCloses) {
                return true;
            }
            else {
                System.out.printf("Error on client socket: %s\n", e.getMessage());
                NotificationManager.showError(Messages.Notifications.ERROR_CLIENT_SOCKET + e.getMessage());
                return false;
            }
        }
        catch(CancelGameException e) {

            System.out.printf("Game cancelled by server: %s\n", e.getMessage());
            return false;
        }

        return true;
    }

    private void playRound(Socket socket) 
    throws OnlyOnePlayerLeftException, CancelGameException, IOException, InterruptedException {

        boolean handEndsByFold = false;
		int serverCode;
        do {

            serverCode = SocketUtils.receiveInt(socket.getInputStream());
            if(serverCode == GameType.TURN_FORCED_SB) {

				final int amountSB = SocketUtils.receiveInt(socket.getInputStream());
                final int onBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                final int offBetMoney = SocketUtils.receiveInt(socket.getInputStream());

                Platform.runLater(() -> {

                    GUI_putPlayerBet(_clientInfo.id, onBetMoney, offBetMoney, false);
                    GUI_putTurnPlayer(_clientInfo.id);
                });

			}
			else if(serverCode == GameType.TURN_FORCED_BB) {

				final int amountBB = SocketUtils.receiveInt(socket.getInputStream());
                final int onBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                final int offBetMoney = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the big blind with %d chips\n", amountBB);

                Platform.runLater(() -> {

                    GUI_putPlayerBet(_clientInfo.id, onBetMoney, offBetMoney, false);
                    GUI_putTurnPlayer(_clientInfo.id);
                });
			}
			else if(serverCode == GameType.TURN_WAIT) {

                int seatID = _playerSeatMap.get(_clientInfo.id);
				System.out.printf("Wait for the other players to play...\n");
                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                    _listPlayerStackPanes.get(seatID).getStyleClass().remove("tourn-player-color");
                    GUI_stopVisualTimer();
                });

			}
			else if(serverCode == GameType.TURN_PLAY) {

				System.out.printf("It's your turn to play!\n");

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

                _commandQueue.clear();

                // Do not allow to bet less than the current max bet
                Platform.runLater(() -> {

                    buttonsHolder.setVisible(true);
                    GUI_putTurnPlayer(_clientInfo.id);
                    GUI_startVisualTimer(_clientInfo.id);

                    int sliderStep = Math.clamp(offBetMoney / 100, 1, offBetMoney);
                    sliderMoney.setMajorTickUnit( sliderStep );
                    sliderMoney.setMin( (double)maxBet );
                    sliderMoney.setMax( (double)(offBetMoney + onBetMoney) );
                    sliderMoney.setValue( sliderMoney.getMin() );
                });

                selectCommand(socket, sb, bb, maxBet, offBetMoney, onBetMoney);

                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                    GUI_stopVisualTimer();
                });
			}
			else if(serverCode == GameType.HAND_ENDS_BY_FOLD) {

                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                    GUI_stopVisualTimer();
                });

				handEndsByFold = true;
			}
            else if(serverCode == GameType.MY_PLAYER_STATUS) {

                String myName = SocketUtils.receiveString(socket.getInputStream());
                final int myOffBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                final int myOnBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                final PlayerRole myRole = PokerGame.receivePlayerRole( socket.getInputStream() );
                final boolean iAmFolded = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                final boolean iAmWinner = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                final boolean iAmEliminated = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;

                Platform.runLater(() -> {

                    int seatID = _playerSeatMap.get(_clientInfo.id);
                    if(iAmFolded) {
                        _listHandBet.get( seatID ).setOpacity(FOLDED_OPACITY);
                    }
                    else {
                        GUI_putPlayerBet(_clientInfo.id, myOnBetMoney, myOffBetMoney, iAmFolded);
                    }

                    buttonsHolder.setVisible(false);
                });
            }
            else if(serverCode == GameType.TURN_BEFORE_PLAY) {

                int currentTurnPlayerId = SocketUtils.receiveInt( socket.getInputStream() );
                GUI_putTurnPlayer(currentTurnPlayerId);

                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                    GUI_startVisualTimer(currentTurnPlayerId);
                });
            }
            else if(serverCode == GameType.TURN_OTHER_PLAYER) {

                int otherPlayerID = SocketUtils.receiveInt( socket.getInputStream() );
                String otherPlayerName = SocketUtils.receiveString( socket.getInputStream() );
                PlayerRole otherPlayerRole = PokerGame.receivePlayerRole( socket.getInputStream() );
                boolean otherPlayerIsFolded = SocketUtils.receiveInt( socket.getInputStream() ) == GameType.TRUE;
                boolean otherPlayerIsWinner = SocketUtils.receiveInt( socket.getInputStream() ) == GameType.TRUE;
                String otherPlayerLastCommand = SocketUtils.receiveString( socket.getInputStream() );
                int otherPlayerOffBetMoney = SocketUtils.receiveInt( socket.getInputStream() );
                int otherPlayerOnBetMoney = SocketUtils.receiveInt( socket.getInputStream() );

                System.out.printf(
                    "Player %s action - PlayerID: %d, Action: %s, OffBetMoney: %d, OnBetMoney: %d\n",
                    otherPlayerName,
                    otherPlayerID,
                    otherPlayerLastCommand,
                    otherPlayerOffBetMoney, otherPlayerOnBetMoney
                );

                Platform.runLater(() -> {
                    
                    GUI_stopVisualTimer();
                    GUI_putPlayerBet(otherPlayerID, otherPlayerOnBetMoney, otherPlayerOffBetMoney, otherPlayerIsFolded);

                    int seatID = _playerSeatMap.get(otherPlayerID);
                    _listPlayerStackPanes.get(seatID).getStyleClass().remove("tourn-player-color");
                });

            }
            else if(serverCode == GameType.TOTAL_POT) {

                final int totalPot = SocketUtils.receiveInt( socket.getInputStream() );
                System.out.printf("Total pot now is %d$\n", totalPot);
                Platform.runLater(() -> {
                    labelTotalPot.setText( String.valueOf(totalPot) );
                });
            }
            else if(serverCode == GameType.ERROR_GAME_CANCELS) {

                System.out.printf("Game has been cancelled by the server!\n");
                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                    GUI_stopVisualTimer();
                });

                throw new CancelGameException();
            }
            else if(serverCode == GameType.ROUND_ENDS) {

                System.out.printf("ROUND_ENDS received!\n");

                Platform.runLater(() -> {
                    buttonsHolder.setVisible(false);
                    GUI_stopVisualTimer();
                });
            }
            else {

				System.out.printf("Unknown turn code %d\n", serverCode);
                NotificationManager.showError(Messages.Notifications.ERROR_UNKNOWN_TURN_CODE + serverCode) ;
			}

        }
        while(serverCode != GameType.ROUND_ENDS && !handEndsByFold);
		System.out.printf("Round has ended!\n\n");    

		if(handEndsByFold) {
            NotificationManager.showError(Messages.Notifications.ERROR_ONLY_ONE_PLAYER_LEFT);
            Platform.runLater(() -> {
                buttonsHolder.setVisible(false);
            });

			throw new OnlyOnePlayerLeftException();
        }
    }

    private boolean showdown(Socket socket) throws IOException, InterruptedException, CancelGameException {

        boolean gameEnds = false;
        int code;
        do {

            code = SocketUtils.receiveInt(socket.getInputStream());
            if(code == GameType.MY_PLAYER_STATUS) {

                String myName = SocketUtils.receiveString(socket.getInputStream());
                int myOffBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                int myOnBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                PlayerRole myRole = PokerGame.receivePlayerRole(socket.getInputStream());
                boolean iAmFolded = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean iAmWinner = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean iAmEliminated = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;

                Platform.runLater(() -> {
                    GUI_updatePlayerInfo(_clientInfo.id, myRole, myOnBetMoney, myOffBetMoney, iAmFolded, iAmWinner, iAmEliminated, true);
                });
            }
            else if(code == GameType.OTHER_PLAYER_STATUS) {

                int playerID = SocketUtils.receiveInt(socket.getInputStream());
                String playerName = SocketUtils.receiveString(socket.getInputStream());
                PlayerRole role = PokerGame.receivePlayerRole(socket.getInputStream());
                boolean isFolded = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean isWinner = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean isEliminated = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                int moneyOffBet = SocketUtils.receiveInt(socket.getInputStream());
                int moneyOnBet = SocketUtils.receiveInt(socket.getInputStream());

                Platform.runLater(() -> {
                    GUI_updatePlayerInfo(playerID, role, moneyOnBet, moneyOffBet, isFolded, isWinner, isEliminated, true);
                });
            }
            else if (code == GameType.PLAYER_CARDS) {
                
                final int otherPlayerID = SocketUtils.receiveInt(socket.getInputStream());
                final Card c1 = PokerGame.receiveCard(socket.getInputStream());
                final Card c2 = PokerGame.receiveCard(socket.getInputStream());
            
                Platform.runLater(() -> {
                    
                    final int seatID = _playerSeatMap.get(otherPlayerID);
                    ImageView leftCard = (ImageView) _listPaintCards.get(seatID * 2);
                    ImageView rightCard = (ImageView) _listPaintCards.get(seatID * 2 + 1);

                    GUI_showCard(leftCard, c1);
                    GUI_showCard(rightCard, c2);
                });

                System.out.printf("Other player[%d] has cards: %s %s\n", 
                    otherPlayerID,
                    c1.toString(), 
                    c2.toString()
                );
            }
            else if(code == GameType.PLAYER_STATUS_END) {
                System.out.printf("Player status end received!\n");
            }
            else if(code == GameType.GAME_ENDS) {
                System.out.printf("Game ends received!\n");
                gameEnds = true;
            }
            else if(code == GameType.GAME_KEEPS) {
                System.out.printf("Game keeps received!\n");
                gameEnds = false;
            }
            else {
                System.out.printf("Unknown code %d in showdown!\n", code);
                NotificationManager.showError(String.format(Messages.Notifications.ERROR_UNKNOWN_CODE_SHOWDOWN, code));
            }
            
        } 
        while(code != GameType.GAME_ENDS && code != GameType.GAME_KEEPS);
        
        // Wait to display player cards for the user
        System.out.printf("%d seconds pause to see the winner...\n", GameType.SHOWDOWN_WAIT_TIME_SEC);
        Thread.sleep(GameType.SHOWDOWN_WAIT_TIME_SEC * 1000);

        return gameEnds;
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

                _commandQueue.clear();
                String command = _commandQueue.poll(_turnTimerTotal, TimeUnit.SECONDS);

                if(command == null) {
                    System.out.printf("No local command received before timeout.");
                    Platform.runLater(() -> {
                        buttonsHolder.setVisible(false);
                        GUI_stopVisualTimer();
                    });

                    return;
                }

                String baseCommand = command.split(" ")[0];
                System.out.printf("Full command received: %s\n", command);

                valid = true;
                if (baseCommand.equalsIgnoreCase("raise") || baseCommand.equalsIgnoreCase("r")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);

                    Platform.runLater(() -> {
                        int targetBet = Integer.parseInt(command.split(" ")[1]);
                        GUI_putPlayerBet(_clientInfo.id, targetBet, offBetMoney, false);
                    });
                }
                else if (baseCommand.equalsIgnoreCase("fold") || baseCommand.equalsIgnoreCase("f")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);

                    Platform.runLater(() -> {
                        GUI_putPlayerBet(_clientInfo.id, onBetMoney, offBetMoney, true);
                    });
                }
                else if (baseCommand.equalsIgnoreCase("check") || baseCommand.equalsIgnoreCase("k")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);

                    Platform.runLater(() -> {
                        GUI_putPlayerBet(_clientInfo.id, 0, offBetMoney, false);
                    });
                }
                else if (baseCommand.equalsIgnoreCase("call") || baseCommand.equalsIgnoreCase("c")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);

                    Platform.runLater(() -> {
                        GUI_putPlayerBet(_clientInfo.id, maxBet, offBetMoney, false);
                    });
                }
                else if (baseCommand.equalsIgnoreCase("all in") || baseCommand.equalsIgnoreCase("a")) {
                    SocketUtils.sendString(socket.getOutputStream(), command);
                }
                else {
                    System.out.printf("Command %s not valid! Try again\n", command);
                    NotificationManager.showError(String.format(Messages.Notifications.ERROR_COMMAND_NOT_VALID, command));
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
            NotificationManager.showError(Messages.Notifications.ERROR_SENDING_COMMAND + e.getMessage());
                    
        }
    }

    private void playerStartInfo(Socket socket) throws IOException, CancelGameException {

        int code;
        do {

            code = SocketUtils.receiveInt(socket.getInputStream());
            if(code == GameType.MY_PLAYER_STATUS) {

                String myName = SocketUtils.receiveString(socket.getInputStream());
                int myOffBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                int myOnBetMoney = SocketUtils.receiveInt(socket.getInputStream());
                PlayerRole role = PokerGame.receivePlayerRole(socket.getInputStream());
                boolean iAmFolded = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean iAmWinner = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean iAmEliminated = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;

                System.out.printf(
                    "My info - Name: %s, Role: %s, OffBetMoney: %d, OnBetMoney: %d\n",
                    myName,
                    role.name(),
                    myOffBetMoney, myOnBetMoney
                );

                Platform.runLater(() -> {
                    GUI_updatePlayerInfo(_clientInfo.id, role, myOnBetMoney, myOffBetMoney, iAmFolded, iAmWinner, iAmEliminated, false);
                });
            }
            else if(code == GameType.OTHER_PLAYER_STATUS) {

                int playerID = SocketUtils.receiveInt(socket.getInputStream());
                String player = SocketUtils.receiveString(socket.getInputStream());
                PlayerRole playerRole = PokerGame.receivePlayerRole(socket.getInputStream());
                boolean isFolded = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean isWinner = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                boolean isEliminated = SocketUtils.receiveInt(socket.getInputStream()) == GameType.TRUE;
                int moneyOffBet = SocketUtils.receiveInt(socket.getInputStream());
                int moneyOnBet = SocketUtils.receiveInt(socket.getInputStream());

                System.out.printf(
                    "Player %s info - PlayerID: %d, Role: %s, OffBetMoney: %d, OnBetMoney: %d\n",
                    player,
                    playerID,
                    playerRole.name(),
                    moneyOffBet, moneyOnBet
                );

                Platform.runLater(() -> {
                    GUI_updatePlayerInfo(playerID, playerRole, moneyOnBet, moneyOffBet, isFolded, isWinner, isEliminated, false);
                });
            }
            else if(code == GameType.PLAYER_STATUS_END) {
                System.out.printf("Exiting player status update loop!\n");
            }
            else if(code == GameType.ERROR_GAME_CANCELS) {
                System.out.printf("All the players left! Cancelling game...\n");
                throw new CancelGameException();
            }
            else {
                System.out.printf("ERROR! Code received: %d!\n", code);
                NotificationManager.showError(Messages.Notifications.ERROR_UNKNOWN_CODE + code);
             
            }
            
        } 
        while(code != GameType.PLAYER_STATUS_END);
    }


    private void GUI_updatePlayerInfo(
        int playerID, 
        PlayerRole role, 
        int onBetMoney, 
        int offBetMoney, 
        boolean isFolded, 
        boolean isWinner, 
        boolean isEliminated, 
        boolean isShowdown
    ) {

        final int seatID = _playerSeatMap.get(playerID);
        StackPane playerStackPane = _listPlayerStackPanes.get(seatID);
        Label nameLabel = _listNameLabels.get(seatID);
        Label moneyLabel = _listMoneyLabels.get(seatID);
        Label betLabel = _listOnBetMoney.get(seatID);

        // Draw dealers button if necessary
        if(isShowdown && role == PlayerRole.DEALER) {
            GUI_putDealerButton(playerID);
        }

        // Draw player as folded if necessary
        if(isFolded) {
            playerStackPane.setOpacity(FOLDED_OPACITY);
            _listHandBet.get(seatID).setOpacity(FOLDED_OPACITY);
            _listImageCards.get(seatID).setOpacity(FOLDED_OPACITY);
        }
        else {
            playerStackPane.setOpacity(DEFAULT_OPACITY);
            _listHandBet.get(seatID).setOpacity(DEFAULT_OPACITY);
            _listImageCards.get(seatID).setOpacity(DEFAULT_OPACITY);
        }

        if(isShowdown && isWinner) {
            System.out.printf("Player %s is the winner of the hand!\n", nameLabel.getText());
            NotificationManager.showSuccess(String.format(Messages.Notifications.CONFIRMATION_WINNER_GAME, nameLabel.getText()));
             
            // Change winners name during 3 seconds
            PauseTransition transition = new PauseTransition( Duration.seconds(3) );
            nameLabel.setText( nameLabel.getText() + " (Winner)" );
            transition.setOnFinished(event -> {
                nameLabel.setText( nameLabel.getText().replace(" (Winner)", "") );
            });
            transition.play();
        }

        if(isEliminated) {
            System.out.printf("Player %s has been eliminated from the game!\n", nameLabel.getText());
            NotificationManager.showSuccess(String.format(Messages.Notifications.CONFIRMATION_PLAYER_ELIMINATED, nameLabel.getText()));
            
            // Set text as <name> (Eliminated) if necessary
            if(!nameLabel.getText().contains("(Eliminated)"))
                nameLabel.setText( nameLabel.getText() + " (Eliminated)" );
            _listPlayerStackPanes.get(seatID).setOpacity(ELIMINATED_OPACITY);
        }

        betLabel.setText( String.valueOf(onBetMoney) );
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

    private void GUI_showCard(ImageView imageView, Card card) {

        String path = "images/cards/";
        String cardValue = (card.getNumber() == 2) ? "2"
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
                        : (card.getNumber() == 14) ? "ace"
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

    private void handleEquity(Socket socket) throws IOException {

        int code = SocketUtils.receiveInt(socket.getInputStream());
        _myEquity = SocketUtils.receiveString(socket.getInputStream());
        Platform.runLater(this::GUI_putEquityToPlayer);
    }

    private void GUI_putMyCards(int playerID, Card card1, Card card2) {
        int seatID = _playerSeatMap.get(playerID);
        ImageView leftCard = (ImageView) _listPaintCards.get(seatID * 2);
        ImageView rightCard = (ImageView) _listPaintCards.get(seatID * 2 + 1);

        GUI_showCard(leftCard, card1);
        GUI_showCard(rightCard, card2);
    }

    private void GUI_putRoundName(String round) {
        btnRound.setText(round);
    }

    private void GUI_putDealerButton(int playerID) {

        Integer seatID = _playerSeatMap.get(playerID);
        if(seatID != null)
            _listDealer.get(seatID).setVisible(true);
    }

    private void GUI_putPlayerBet(int playerID, int amountOnBet, int amountOffBet, boolean isFolded) {

        int seatID = _playerSeatMap.get(playerID);
        if(!isFolded) {
            Label betLabel = _listOnBetMoney.get(seatID);
            Label moneyLabel = _listMoneyLabels.get(seatID);

            _listHandBet.get(seatID).setVisible(true);
            _listHandBet.get(seatID).setOpacity(1);

            betLabel.setText( String.valueOf(amountOnBet) );
            betLabel.setVisible(true);

            moneyLabel.setText( String.valueOf(amountOffBet) );
        }
        else {
            StackPane playerStackPane = _listPlayerStackPanes.get(seatID);
            HBox cards = _listImageCards.get(seatID);

            playerStackPane.setOpacity(FOLDED_OPACITY);
            _listHandBet.get(seatID).setOpacity(FOLDED_OPACITY);
            cards.setOpacity(FOLDED_OPACITY);
        }
    }

    private void GUI_putTurnPlayer(int playerID) {
        GUI_clearTurnPlayer();
        Integer seatID = _playerSeatMap.get(playerID);
        if(seatID != null) {
            _listPlayerStackPanes.get(seatID).getStyleClass().add("tourn-player-color");
        }
    }

    private void GUI_putEquityToPlayer() {
        Label myEquityLabel = _listEquity.get(0);
        
        if(_equityVisible && _myEquity != null) {
            myEquityLabel.setText(_myEquity);
            myEquityLabel.setVisible(true);

            double equityValue = Double.parseDouble(_myEquity.replace("%","").replace(",", "."))/100;
            GUI_putColorStyleToEquity(myEquityLabel, equityValue);
        }
        else {
            myEquityLabel.setVisible(false);
        }
    }

    private void GUI_putColorStyleToEquity(Label equityLabel, double equity) {

        equityLabel.getStyleClass().removeAll(
          "equity-green",
          "equity-yellow",
          "equity-red"  
        );

        if(equity >= 0.65) {
            equityLabel.getStyleClass().add("equity-green");
        }
        else if(equity >= 0.35) {
            equityLabel.getStyleClass().add("equity-yellow");
        }
        else{
            equityLabel.getStyleClass().add("equity-red");
        }

    }

    private void GUI_putPlayerTimer(int seatID) {
        
        List<Rectangle> rectangles = _listTimer.get(seatID);
        if(rectangles == null)
            return;
        

        final int blocksRemaining = Math.max(0, Math.min(TOTAL_BLOCKS, (int) Math.ceil(_secondsLeft / (double) _blockTime)));
        Platform.runLater(()-> {
            for (int i = 0; i < rectangles.size(); i++) {
                rectangles.get(i).setVisible(i < blocksRemaining);
            }
        });
    }

    private void GUI_resetPlayerTimer(int seatID) {
        List<Rectangle> rectangles = _listTimer.get(seatID);
        if(rectangles == null) return;

        Platform.runLater(()->{
            for (Rectangle r : rectangles) {
                r.setVisible(true);
            }
        });
    }

    private void GUI_clearTableCards() {
        tableCard0.setImage(null);
        tableCard1.setImage(null);
        tableCard2.setImage(null);
        tableCard3.setImage(null);
        tableCard4.setImage(null);
    }

    private void GUI_clearPlayerBets() {

        _listOnBetMoney.forEach(label -> label.setVisible(false));
        _listHandBet.forEach(bet -> bet.setVisible(false));
        _listHandBet.forEach(bet -> {
            bet.setVisible(false);  
            bet.setOpacity(1.0);
        });
    }

    private void GUI_clearDealer() {
        _listDealer.forEach(iv -> iv.setVisible(false));
    }

    private void GUI_clearTurnPlayer() {
        _listPlayerStackPanes.forEach(pane -> pane.getStyleClass().remove("tourn-player-color"));
    }

    private void GUI_clearTimer(int seatID) {
        List<Rectangle> rectangles = _listTimer.get(seatID);
        if(rectangles == null) return;

        Platform.runLater(()->{
            for (Rectangle r : rectangles) {
                r.setVisible(false);
            }
        });
    }



    private synchronized void GUI_startVisualTimer(int playerID) {

        GUI_stopVisualTimer();

        Integer seatID = _playerSeatMap.get(playerID);
        final long turnEndTime = System.currentTimeMillis() + _turnTimerTotal * 1000L;
        _timerPlayerId = playerID;

        // Reset timer
        System.out.printf("Timer start for playerId=%d (%s client)\n", playerID, playerID == _clientInfo.id ? "local turn" : "remote turn");
        GUI_resetPlayerTimer(seatID);

        _task = _scheduler.scheduleAtFixedRate(()-> {

            long now = System.currentTimeMillis();
            long millisLeft = turnEndTime - now;
            _secondsLeft = (int) Math.ceil(Math.max(millisLeft, 0) / 1000.0);

            GUI_putPlayerTimer(seatID);

            if(millisLeft < 0) {
                GUI_stopVisualTimer();
            }

        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private synchronized void GUI_stopVisualTimer() {
        
        if(_task != null && !_task.isCancelled()) {
            _task.cancel(true);
            _task = null;
        }

        if(_timerPlayerId != null) {
            Integer seatID = _playerSeatMap.get(_timerPlayerId);
            System.out.printf("Timer stop for playerId=%d\n", _timerPlayerId);
            if(seatID != null) {
                GUI_clearTimer(seatID);
            }
        }
        _timerPlayerId = null;
    }

    private synchronized void GUI_shutdownVisualTimer() {
        _scheduler.shutdownNow();
    }

    
    
}