package com.ucm.client;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ucm.client.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ClientMainCopy extends Application {

	private static class Card {
		public int numberCode;
		public int suitCode;

		public Card(){
			this.numberCode = -1;
			this.suitCode = -1;
		}

		public Card(int number, int suit) {
			this.numberCode = number;
			this.suitCode = suit;
		}
	}

	public static boolean AUTOMATED_MODE = false;
    private static String _hostname = "localhost";

    private static String _name;
	private static Scanner _scanner;

	
    /*
     * Desde la ruta TFGPOKER/tfgpoker
     * 		.\mvnw.cmd clean install
     * Run:
     * 		.\mvnw.cmd -pl client -Prun exec:java
	 * Run auto(Siendo 'match' el nombre del archivo de partida a jugar y 'player' el del jugador a imitar):
	 * 		.\mvnw.cmd -pl client -Prun-auto exec:java -Dmatch="" -Dplayer=""
     * Debug:
     * 		.\mvnwDebug.cmd -pl client -Pdebug exec:java
     * Run the Tests
     * 		.\mvnw.cmd test
     */
    public static void main(String[] args) throws IOException {

		if(args.length == 2) {	// Ejecucion automatizada con archivos de texto para simular el input de multiples jugadores
			String playerFilePath = "client/tests/" + args[0] + "/" + args[1] + ".txt";
			System.out.printf("Running in auto mode with player file: %s\n", playerFilePath);
			_scanner = new Scanner( new File(playerFilePath) );
			AUTOMATED_MODE = true;
		}
		else {
			_scanner = new Scanner(System.in);
		}

		/*System.out.printf("Specify the server IP (default: localhost): ");
	    String serverIP = _scanner.nextLine();
		if (!serverIP.trim().isEmpty()) {
			_hostname = serverIP.trim();
		}

		try {
			Socket socket = preGame(_hostname);
			game(socket);

			socket.close();
			_scanner.close();
		}
		catch(IOException e) {
			System.out.printf("%s\n", e.getMessage());
		}*/
    }


	public static void iniciar(final String serverIP, final String userName){

		System.out.printf("Specify the server IP (default: localhost): ");
		if (!serverIP.trim().isEmpty()) {
			_hostname = serverIP.trim();
		}

		try {
			Socket socket = preGame(_hostname, userName);
			game(socket);

			socket.close();
			_scanner.close();
		}
		catch(IOException e) {
			System.out.printf("%s\n", e.getMessage());
		}

	}

	// Pregame section
	private static Socket preGame(final String serverIP, final String userName) throws IOException {

		SocketChannel socket = SocketChannel.open();
		socket.configureBlocking(false);
		socket.connect( new InetSocketAddress(serverIP, GameType.PORT) );
		
		while(!socket.finishConnect()) {}
		System.out.printf("Connected to the server succesfully!\n");

		System.out.printf("Write your username: ");
		_name = userName;
		sendString(_name, socket);

		int opcion = getUserPetition();
		if(opcion == 1) {
			sendPetition(GameType.CREATE_PETITION, socket);
			System.out.printf("Petition CREATE sent!\n");
			configureGame(socket);
		}
		else if(opcion == 2) {
			sendPetition(GameType.JOIN_PETITION, socket);
			System.out.printf("Petition JOIN sent!\n");
		}
		else {
			throw new IOException("User petition invalid! Must be 1 or 2");
		}

		boolean gameStarts = false;
		while(!gameStarts){ 
			gameStarts = receiveGameStartCode(socket);
		}
		System.out.printf("Game starts!\n");

		socket.configureBlocking(true);	// Important!!
		return (socket != null) ? socket.socket() : null;
	}

	private static void configureGame(SocketChannel socket) throws IOException {

		boolean hostWantsToStart = false;
		String command = null;
		while(!hostWantsToStart) {
			
			// Give time to the rest of the automated players to join
			if(AUTOMATED_MODE) {
				waitSeconds(6);
			}

			System.out.printf("Select an action: \n");
			System.out.printf("Start game: \'start\'\n");
			System.out.printf("Add bots: \'bot\'\n");
			System.out.printf(" > ");

			command = _scanner.next();
			if(command.equalsIgnoreCase("start")) {
				sendPetition(GameType.HOST_START_GAME_PETITION, socket);
				hostWantsToStart = true;
			}
			else if(command.equalsIgnoreCase("bot")) {	// TODO
				List<String> botList = receiveBotList(socket);
				configureBots(socket, botList);
			}
			else {
				System.out.printf("Command \'%s\' invalid!\n", command);
				hostWantsToStart = false;
			}
		}
	}

	// TODO
	private static List<String> receiveBotList(SocketChannel socket) {
		
		List<String> botList = new ArrayList<>();
		
		return botList;
	}

	// TODO
	private static void configureBots(SocketChannel socket, final List<String> botList) {

		System.out.printf("Host wants to add a bot");
		boolean exit = false;
		String command = null;
		while(!exit) {
			
			// Give time to the rest of the automated players to join
			if(AUTOMATED_MODE) {
				waitSeconds(6);
			}

			System.out.printf("Select a bot: \n");
			for(String botInfo : botList) {
				System.out.printf("%s\n", botInfo);
			}
			System.out.printf(" > ");

			command = _scanner.next();
			
		}
	}

	private static boolean receiveGameStartCode(SocketChannel socket) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocate(128);
		int bytesRead;
		int code;

		bytesRead = socket.read(buffer);
		if (bytesRead == -1) {
			socket.close();
			return false;
		}
		else if(bytesRead == 0) {
			return false;
		}

		buffer.flip();
		code = buffer.getInt();
		switch (code) {
		case GameType.GAME_STARTS:
			System.out.printf("GAME_STARTS code received!\n");
			return true;
	
		default:
			System.out.printf("Code %d unknown %d\n", code);
			return false;
		}
	}

	// Auxiliar methods - Pregame
	private static void sendString(String msg, SocketChannel socket) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocate(1 + Integer.BYTES + msg.length());
		buffer.put(GameType.DATA_TYPE_NAME);
		buffer.putInt(msg.length());
		buffer.put(msg.getBytes());
		buffer.flip();

		while(buffer.hasRemaining())
			socket.write(buffer);
	}

	private static void sendPetition(int petitionCode, SocketChannel socket) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocate(1 + Integer.BYTES);
		buffer.clear();
		buffer.put(GameType.DATA_TYPE_PETITION);	// Tipo de peticion
		buffer.putInt(petitionCode);				// Codigo peticion
		buffer.flip();

		while(buffer.hasRemaining()){
			socket.write(buffer);
		}
	}

	private static int getUserPetition() {

		// Wait for clients petition
		int option = -1;
		while(option == -1){
			System.out.printf("What do you want do?\n");
			System.out.printf("1- Creat game\n");
			System.out.printf("2- Join game\n");
			System.out.printf("> ");

			option = _scanner.nextInt();
			if(option < 1 || 2 < option){
				option = -1;
				System.out.printf("%d is not a valid option\n", option);
			}
		}

		return option;
	}

	private static void waitSeconds(final int seconds) {

		try {

			for(int i = 0; i < seconds; i++) {
				System.out.printf("Waiting %d seconds for the other players...\n", seconds - i);
				Thread.sleep(1000); // Esperar a que se unan los demas jugadores
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}


	// Game section
	private static String translateCardCode(final int value, final int suit) {
		
		String valueString = (value == GameType.NUMBER_ACE) ? "A" :
								(value == GameType.NUMBER_TWO) ? "2" :
								(value == GameType.NUMBER_THREE) ? "3" :
								(value == GameType.NUMBER_FOUR) ? "4" :
								(value == GameType.NUMBER_FIVE) ? "5" :
								(value == GameType.NUMBER_SIX) ? "6" :
								(value == GameType.NUMBER_SEVEN) ? "7" :
								(value == GameType.NUMBER_EIGHT) ? "8" :
								(value == GameType.NUMBER_NINE) ? "9" :
								(value == GameType.NUMBER_TEN) ? "T" :
								(value == GameType.NUMBER_J) ? "J" :
								(value == GameType.NUMBER_Q) ? "Q" :
								(value == GameType.NUMBER_K) ? "K" : "?";

		char suitChar = (suit == GameType.HEARTS) ? '\u2665' :
							(suit == GameType.DIAMONDS) ? '\u2666' :
							(suit == GameType.CLUBS) ? '\u2663' :
							(suit == GameType.SPADES) ? '\u2660' : 'x';

		return String.format("[%s%c]", valueString, suitChar);
	}

	private static String translatePlayerRoleCode(final int code) {
		
		switch (code) {
			case GameType.PLAYER_ROLE_DEALER:
				return "DEALER";
			case GameType.PLAYER_ROLE_SMALL_BLIND:
				return "SMALL_BLIND";
			case GameType.PLAYER_ROLE_BIG_BLIND:
				return "BIG_BLIND";
			case GameType.PLAYER_ROLE_UNDER_THE_GUN:
				return "UNDER_THE_GUN";
			case GameType.PLAYER_ROLE_MIDDLE_POSITION:
				return "MIDDLE_POSITION";
			case GameType.PLAYER_ROLE_CUT_OFF:
				return "CUT_OFF";
			default:
				return "NO_ROLE";
		}

	}

	private static String getUserCommand() {

		final int MAX_SIZE = 32;
		System.out.printf("Write your action: \n");
		System.out.printf("1- fold(f)\n");
		System.out.printf("2- check(k)\n");
		System.out.printf("3- call(c)\n");
		System.out.printf("4- allin(a)\n");
		System.out.printf("5- raise(r) <amount>\n");
		System.out.printf("> ");

		String command = null;
		while (command == null) { 
			
			command = _scanner.nextLine();
			if(command.isBlank())
				command = null;
			else
				command = (command.length() < MAX_SIZE) ? command : null;
		}

		return command;
	}

	private static void showTableCards(final Card tableCards[]) {

		for(Card c : tableCards){
			if(c != null)
				System.out.printf( translateCardCode(c.numberCode, c.suitCode) );
			else
				System.out.printf("[xx]");
		}
		System.out.printf("\n");

	}

	// Auxiliar methods - Game
	private static Card receiveCard(InputStream in) throws IOException {

		Card c = new Card();
		c.numberCode = SocketUtils.receiveInt(in);
		c.suitCode = SocketUtils.receiveInt(in);
		return c;
	}

	private static void playRound(final Card card1, final Card card2, Socket socket) throws IOException, OnlyOnePlayerLeftException {
		
		System.out.printf("Round has started!\n");

		boolean handEndsByFold = false;
		boolean blindsOnPlay = true;
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

				if(blindsOnPlay) {
					System.out.printf("-- Small blind bet: %d\n", sb);
					System.out.printf("-- Big blind bet: %d\n", bb);
					blindsOnPlay = false;
				}
				
				System.out.printf("-- Max bet: %d\n", maxBet);
				System.out.printf("-- Your money: %d\n", offBetMoney);
				System.out.printf("-- Your last bet: %d\n\n", onBetMoney);
				

				boolean valid = false;
				while(!valid) {

					String command = getUserCommand();
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

	private static void game(Socket socket) throws IOException {

		System.out.printf("Match starts!\n");
		int roleCode;
		int currentMoney;
		int rankingCode;
		int gameStatusCode;
		Card[] playerCards = new Card[2];
		Card[] tableCardValues = new Card[5];
		InputStream in = socket.getInputStream();

		boolean endOfGame = false;
		while(!endOfGame) {

			try {

				roleCode = SocketUtils.receiveInt(in);
				String playerRole = translatePlayerRoleCode(roleCode);
				System.out.printf("Player with rol %s\n", playerRole);

				playerCards[0] = receiveCard(in);
				playerCards[1] = receiveCard(in);
				System.out.printf("Cards: %s - %s\n\n", 
					translateCardCode(playerCards[0].numberCode, playerCards[0].suitCode), 
					translateCardCode(playerCards[1].numberCode, playerCards[1].suitCode)
				);

				// Preflop
				System.out.printf("-- Preflop --\n");
				playRound(playerCards[0], playerCards[1], socket);
				tableCardValues[0] = receiveCard(in);
				tableCardValues[1] = receiveCard(in);
				tableCardValues[2] = receiveCard(in);
				showTableCards(tableCardValues);
				
				// Flop
				System.out.printf("-- Flop --\n");
				playRound(playerCards[0], playerCards[1], socket);
				tableCardValues[3] = receiveCard(in);
				showTableCards(tableCardValues);

				// Turn
				System.out.printf("-- Turn --\n");
				playRound(playerCards[0], playerCards[1], socket);
				tableCardValues[4] = receiveCard(in);
				showTableCards(tableCardValues);

				// River
				System.out.printf("-- River --\n");
				playRound(playerCards[0], playerCards[1], socket);
				showTableCards(tableCardValues);

				// Showdown
				System.out.printf("-- Showdown --\n");
				rankingCode = SocketUtils.receiveInt(in);
				currentMoney = SocketUtils.receiveInt(in);
				if(rankingCode == GameType.PLAYER_WINS_HAND) {
					System.out.printf("You have won!\nCurrent money is %d\n", currentMoney);
				}
				else if(rankingCode == GameType.PLAYER_LOSES_HAND) {
					System.out.printf("You have lost!\nCurrent money is %d\n", currentMoney);
				}

				// Game ends or keeps
				gameStatusCode = SocketUtils.receiveInt(in);
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
					rankingCode = SocketUtils.receiveInt(in);
					currentMoney = SocketUtils.receiveInt(in);
					if(rankingCode == GameType.PLAYER_WINS_HAND) {
						System.out.printf("You have won!\nCurrent money is %d\n", currentMoney);
					}
					else if(rankingCode == GameType.PLAYER_LOSES_HAND) {
						System.out.printf("You have lost!\nCurrent money is %d\n", currentMoney);
					}

					// Game ends or keeps
					gameStatusCode = SocketUtils.receiveInt(in);
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

	


    @Override
    public void start(Stage stage) throws Exception {

        // Llamar a este metodo con: launch(args);
        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }

}