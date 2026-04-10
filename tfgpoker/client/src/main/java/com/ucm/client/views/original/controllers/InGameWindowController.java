package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.GameType;
import com.ucm.common.PokerGame;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;
import com.ucm.common.SocketUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class InGameWindowController extends GenericController {


    @FXML
    private Label usernamePlaceHolder;

    @FXML
    private Button btnFold;

    @FXML
    private Button btnCall;

    @FXML
    private Button btnRaise;


    private Thread _gameThread = null;
    private BlockingQueue<String> _commandQueue = new LinkedBlockingQueue<>();

    @FXML
    private void foldAction() {
        
        _commandQueue.offer("fold");
    }

    @FXML
    private void callAction() {
        _commandQueue.offer("call");
    }

    @FXML
    private void raiseAction() {

        int ejemplo = 5;
        _commandQueue.offer( String.format("raise %d", ejemplo) );
    }


    @Override
    protected void onViewShown() {

        System.out.printf("Start!\n");

        usernamePlaceHolder.setText( _clientInfo.name );

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
            pokerGame(_clientInfo.name, _clientInfo.socket);
        });
        _gameThread.start();
    }

    @Override
    public void onNextEvent() {}

    @Override
    public void onBackEvent() {}


    private void pokerGame(String name, Socket socket) {

		int currentMoney;
		int rankingCode;
		int gameStatusCode;
        PlayerRole role;
		Card[] playerCards = new Card[2];
		Card[] tableCardValues = new Card[5];
        try {

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            boolean endOfGame = false;
            while(!endOfGame) {

                try {

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
                    playRound(playerCards[0], playerCards[1], socket);
                    tableCardValues[0] = PokerGame.receiveCard(input);
                    tableCardValues[1] = PokerGame.receiveCard(input);
                    tableCardValues[2] = PokerGame.receiveCard(input);
                    System.out.printf(
                        "table cards: %s %s %s %s %s\n",
                        tableCardValues[0].toString(), 
                        tableCardValues[1].toString(),
                        tableCardValues[2].toString(),
                        Card.MissingCardToString(),
                        Card.MissingCardToString()
                    );

                    // Flop
                    System.out.printf("-- Flop --\n");
                    playRound(playerCards[0], playerCards[1], socket);
                    tableCardValues[3] = PokerGame.receiveCard(input);
                    System.out.printf(
                        "table cards: %s %s %s %s %s\n",
                        tableCardValues[0].toString(), 
                        tableCardValues[1].toString(),
                        tableCardValues[2].toString(),
                        tableCardValues[3].toString(),
                        Card.MissingCardToString()
                    );

                    // Turn
                    System.out.printf("-- Turn --\n");
                    playRound(playerCards[0], playerCards[1], socket);
                    tableCardValues[4] = PokerGame.receiveCard(input);
                    System.out.printf(
                        "table cards: %s %s %s %s %s\n",
                        tableCardValues[0].toString(), 
                        tableCardValues[1].toString(),
                        tableCardValues[2].toString(),
                        tableCardValues[3].toString(),
                        tableCardValues[4].toString()
                    );

                    // River
                    System.out.printf("-- River --\n");
                    playRound(playerCards[0], playerCards[1], socket);

                    // Showdown
                    System.out.printf("-- Showdown --\n");
                    rankingCode = SocketUtils.receiveInt(input);
                    currentMoney = SocketUtils.receiveInt(input);
                    if(rankingCode == GameType.PLAYER_WINS_HAND) {
                        System.out.printf("You have won!\nCurrent money is %d\n", currentMoney);
                    }
                    else if(rankingCode == GameType.PLAYER_LOSES_HAND) {
                        System.out.printf("You have lost!\nCurrent money is %d\n", currentMoney);
                    }

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
                        rankingCode = SocketUtils.receiveInt(input);
                        currentMoney = SocketUtils.receiveInt(input);
                        if(rankingCode == GameType.PLAYER_WINS_HAND) {
                            System.out.printf("You have won!\nCurrent money is %d\n", currentMoney);
                        }
                        else if(rankingCode == GameType.PLAYER_LOSES_HAND) {
                            System.out.printf("You have lost!\nCurrent money is %d\n", currentMoney);
                        }

                        // Game ends or keeps
                        gameStatusCode = SocketUtils.receiveInt(input);
                        endOfGame = (gameStatusCode == GameType.GAME_ENDS);
                        if(gameStatusCode == GameType.GAME_ENDS)
                            System.out.printf("Match ended!\n\n");
                        else if(gameStatusCode == GameType.GAME_KEEPS)
                            System.out.printf("Match keeps!\n\n");

                    }
                    catch(IOException ex) {
                        System.out.printf("Error receiving the rank after a fold exception: %s", ex.getMessage());
                    }
                }
                catch(InterruptedException e) {
                    System.out.printf("Game thread interrupted: %s\n", e.getMessage());
                }

            }
        }
        catch(IOException e) {
            System.out.printf("Error on game: %s\n", e.getMessage());
        }
    }

    private void playRound(Card card1, Card card2, Socket socket) throws OnlyOnePlayerLeftException, IOException, InterruptedException {

        boolean handEndsByFold = false;
		int sb, bb, maxBet;
		int offBetMoney, onBetMoney;

		int serverCode = SocketUtils.receiveInt(socket.getInputStream());
		while(serverCode != GameType.ROUND_ENDS && !handEndsByFold) {

			if(serverCode == GameType.TURN_FORCED_SB){
				int cantidadSB = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the small blind with %d chips\n", cantidadSB);
			}
			else if(serverCode == GameType.TURN_FORCED_BB) {
				int cantidadBB = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the big blind with %d chips\n", cantidadBB);
			}
			else if(serverCode == GameType.TURN_WAIT) {
				System.out.printf("Wait for the other players!\n");
			}
			else if(serverCode == GameType.TURN_PLAY) {

				System.out.printf("It's your turn to play!\n");

				// Receive round info
				sb = SocketUtils.receiveInt( socket.getInputStream() );
				bb = SocketUtils.receiveInt( socket.getInputStream() );
				maxBet = SocketUtils.receiveInt( socket.getInputStream() );
				offBetMoney = SocketUtils.receiveInt( socket.getInputStream() );
				onBetMoney = SocketUtils.receiveInt( socket.getInputStream() );

                String command = selectCommand(socket);

			}
			else if(serverCode == GameType.HAND_ENDS_BY_FOLD) {
				handEndsByFold = true;
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

    private String selectCommand(Socket socket) throws InterruptedException {

        System.out.printf("Waiting to client to make a move...\n");
        String command = null;
        try {

            boolean valid = false;
            while(!valid) {

                command = _commandQueue.take();
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
        }
        catch(IOException e) {
            System.out.printf("Error sending the command: %s\n", e.getMessage());
        }

        return command;
    }


    
    
}
