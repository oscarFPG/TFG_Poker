package com.ucm.client.views.original.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Suit;
import com.ucm.common.SocketUtils;

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


    private Thread _gameThread;


    @FXML
    public void onFoldAction() {
        System.out.printf("Fold button pressed!\n");
    }

    @FXML
    public void onCallAction() {
        System.out.printf("Call button pressed!\n");
    }

    @FXML
    public void onRaiseAction() {
        System.out.printf("Raise button pressed!\n");
    }


    @Override
    protected void onViewShown() {

        usernamePlaceHolder.setText( _clientInfo.name );

        _gameThread = new Thread(() -> {
            pokerGame(_clientInfo.name, _clientInfo.socket);
        });
        _gameThread.start();
    }

    private void pokerGame(String name, Socket socket) {

        int roleCode;
		int currentMoney;
		int rankingCode;
		int gameStatusCode;
		Card[] playerCards = new Card[2];
		Card[] tableCardValues = new Card[5];
        try {

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            int role = SocketUtils.receiveInt(input);
            System.out.printf("Role received: %d\n", role);

            boolean endOfGame = false;
            while(!endOfGame) {

                try {

                    // Player role and cards
                    roleCode = SocketUtils.receiveInt(input);
                    playerCards[0] = receiveCard(input);
                    playerCards[1] = receiveCard(input);

                    // Preflop
                    System.out.printf("-- Preflop --\n");
                    playRound(playerCards[0], playerCards[1], socket);
                    tableCardValues[0] = receiveCard(input);
                    tableCardValues[1] = receiveCard(input);
                    tableCardValues[2] = receiveCard(input);
                    
                    // Flop
                    System.out.printf("-- Flop --\n");
                    playRound(playerCards[0], playerCards[1], socket);
                    tableCardValues[3] = receiveCard(input);

                    // Turn
                    System.out.printf("-- Turn --\n");
                    playRound(playerCards[0], playerCards[1], socket);
                    tableCardValues[4] = receiveCard(input);

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
            }

        }
        catch(IOException e) {
            System.out.printf("Error on game: %s\n", e.getMessage());
        }
    }

    private void playRound(Card card1, Card card2, Socket socket) throws OnlyOnePlayerLeftException, IOException {

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

				boolean valid = false;
				while(!valid) {

					String command = "";    // Read input from buttons
					String baseCommand = command.split(" ")[0];

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

    private Card receiveCard(InputStream in) throws IOException {
        
		int valueCode = SocketUtils.receiveInt(in);
		int suitCode = SocketUtils.receiveInt(in);
		return new Card(valueCode, Suit.getSuitFromCode(suitCode));
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
