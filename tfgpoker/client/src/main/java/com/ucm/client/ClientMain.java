package com.ucm.client;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ClientMain extends Application {

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
    private static String hostname = "localhost";
    private static String _name;
	private static int _money;
	private static boolean _gameStarts;
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

		if(args.length == 2){	// Ejecucion automatizada con archivos de texto para simular el input de multiples jugadores
			String playerFilePath = "client/tests/" + args[0] + "/" + args[1] + ".txt";
			System.out.printf("Running in auto mode with player file: %s\n", playerFilePath);
			_scanner = new Scanner( new File(playerFilePath) );
			AUTOMATED_MODE = true;
		}
		else{
			_scanner = new Scanner(System.in);
		}

		System.out.printf("Specify the server IP (default: localhost): ");
		String serverIP = _scanner.nextLine();
		if (!serverIP.trim().isEmpty()) {
			hostname = serverIP.trim();
		}
		else {
			serverIP = hostname;
		}

		// Poker application
		Socket socket = preGame(serverIP);
		game(socket);

		// Cleanup
		try {
			socket.close();
		}
		catch(IOException e) {
			System.out.printf("Error trying to end the game succesfully: %s\n", e.getMessage());
		}

		_scanner.close();
    }


	// Pre-game methods
	private static void handleConnect(SelectionKey key) {

		SocketChannel client = (SocketChannel) key.channel();
		try {
			if (client.finishConnect()) {
				System.out.printf("Conectado al servidor!\n");
				key.interestOps(SelectionKey.OP_WRITE);	// Prepare to send the clients name
			}
		}
		catch (IOException e) {
			System.out.printf("Error connecting to server: %s\n", e.getMessage());
		}
	}

	private static void handleReceive(SelectionKey key) {

		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(128);
		int bytesRead;

		try {
			bytesRead = socket.read(buffer);
			if (bytesRead == -1) {
				socket.close();
				key.cancel();
				return;
			}

			buffer.flip();
			int code = buffer.getInt();
			switch (code) {
			case GameType.GAME_STARTS:
				System.out.printf("Aviso GAME_STARTS recibido!\n");
				_gameStarts = true;
				break;
		
			default:
				System.out.printf("Aviso %d desconocido %d\n", code);
				break;
			}

		}
		catch (IOException e) {
			System.out.printf("Error connecting to server: %s\n", e.getMessage());
			try{
				socket.close();
				key.cancel();
			}
			catch(IOException exc){
				System.out.printf("Error closing the socket: %s\n", exc.getMessage());
			}
		}
	}

	private static void handleSend(SelectionKey key) {
		
		SocketChannel socket = (SocketChannel) key.channel();
		try{

			// Get users name
			System.out.printf("Escribe tu nombre: ");
			_name = _scanner.next();

			// We should validate name before storing it -> minimum and maximum number of caracters, etc...
			sendString(_name, socket);

			// Stop sending data and waiting for reading data from socket
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

			// Get user petition and send it to the server
			int opcion = getUserPetition();
			if(opcion == 1){
				sendPetition(GameType.CREATE_PETITION, socket);
				System.out.printf("Petition CREATE sent!\n");
				sendStartGameByHost(socket);
			}
			else if(opcion == 2){
				sendPetition(GameType.JOIN_PETITION, socket);
				System.out.printf("Petition JOIN sent!\n");
			}

			// Prepare to receive server codes
			key.interestOps(SelectionKey.OP_READ);
		}
		catch(IOException e){
			System.out.printf("Error enviando el nombre del usuario: %s\n", e.getMessage());
			try{
				socket.close();
				key.cancel();
			}
			catch(IOException exc){
				System.out.printf("Error closing the socket: %s\n", exc.getMessage());
			}
		}
	}


	// Pregame and in-game methods
	private static Socket preGame(final String serverIP) {

		SocketChannel socket = null;
		try{
			socket = SocketChannel.open();
			socket.configureBlocking(false);
			socket.connect( new InetSocketAddress(serverIP, GameType.PORT) );

			Selector selector = Selector.open();
			socket.register(selector, SelectionKey.OP_CONNECT);

			_gameStarts = false;
			while(!_gameStarts){
				selector.select();
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();

					if (!key.isValid())
						continue;

					if (key.isConnectable()) {
						handleConnect(key);
					}
					if (key.isReadable()) {
						handleReceive(key);
					}
					if (key.isWritable()) {
						handleSend(key);
					}
				}
			}

			selector.close();
			socket.configureBlocking(true);
		}
		catch(IOException e) {
			System.out.printf("Error: %s\n", e.getMessage());
		}

		return (socket != null) ? socket.socket() : null;
	}

	private static void game(Socket socket) {

		System.out.printf("Match starts!\n");
		int roleCode;
		boolean status;
		boolean endOfGame = false;
		Card[] playerCards = new Card[2];
		Card[] tableCardValues = new Card[5];

		try {

			InputStream in = socket.getInputStream();
			while(!endOfGame) {

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
				status = playRound(playerCards[0], playerCards[1], socket);
				System.out.printf("Preflop has ended!\n\n");
				tableCardValues[0] = receiveCard(in);
				tableCardValues[1] = receiveCard(in);
				tableCardValues[2] = receiveCard(in);
				showTableCards(tableCardValues);
				
				// Flop
				status = playRound(playerCards[0], playerCards[1], socket);
				tableCardValues[3] = receiveCard(in);
				showTableCards(tableCardValues);

				// Turn
				status = playRound(playerCards[0], playerCards[1], socket);
				tableCardValues[4] = receiveCard(in);
				showTableCards(tableCardValues);

				// River
				status = playRound(playerCards[0], playerCards[1], socket);
				showTableCards(tableCardValues);

				// Showdown
				int rankingCode = SocketUtils.receiveInt(in);
				_money = SocketUtils.receiveInt(in);
				if(rankingCode == GameType.PLAYER_WINS_HAND) {
					System.out.printf("You have won!\nCurrent money is %d\n", _money);
				}
				else if(rankingCode == GameType.PLAYER_LOSES_HAND) {
					System.out.printf("You have lost!\nCurrent money is %d\n", _money);
				}

				// Game ends or keeps
				int gameStatusCode = SocketUtils.receiveInt(in);
				System.out.printf("Game status code received is %d\n", gameStatusCode);
				endOfGame = (gameStatusCode == GameType.GAME_ENDS);
			}

			System.out.printf("Game ends! Thanks for playing %s!\n", _name);
		}
		catch (IOException e) {
			System.out.printf("Error: %s\n", e.getMessage());
			try {
				socket.close();
				System.out.printf("Socket closed successfully\n");
			}
			catch(IOException exc) {
				System.out.printf("Error closing the socket: %s\n", exc.getMessage());
			}
		}

	}


	// Auxiliar methods to send/receive data
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

	private static void sendStartGameByHost(SocketChannel socket) throws IOException {

		// Wait for host to start the game
		String command = null;
		while(command == null){
			System.out.printf("Escriba \'start\' para comenzar la partida...\n > ");

			if(AUTOMATED_MODE){
				try {

					final int seconds = 6;
					for(int i = 0; i < seconds; i++){
						System.out.printf("Waiting %d seconds for the other players...\n", seconds - i);
						Thread.sleep(1000); // Esperar a que se unan los demas jugadores
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}

			command = _scanner.next();
			if(!command.equalsIgnoreCase("start")){
				System.out.printf("Comando \'%s\' no valido!\n", command);
				command = null;
			}
			else{
				sendPetition(GameType.HOST_START_GAME_PETITION, socket);
			}
		}
	}

	// In-game logic
	private static Card receiveCard(InputStream in) throws IOException {

		Card c = new Card();
		c.numberCode = SocketUtils.receiveInt(in);
		c.suitCode = SocketUtils.receiveInt(in);
		return c;
	}

	private static boolean playRound(final Card card1, final Card card2, Socket socket) throws IOException {
		
		System.out.printf("Round has started!\n");

		boolean roundSuccess = true;
		boolean blindsOnPlay = true;
		int sb, bb, maxBet;

		int turn = SocketUtils.receiveInt(socket.getInputStream());
		while(turn != GameType.ROUND_ENDS) {

			if(turn == GameType.TURN_FORCED_SB){
				int cantidadSB = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the small blind with %d chips\n", cantidadSB);
			}
			else if(turn == GameType.TURN_FORCED_BB){
				int cantidadBB = SocketUtils.receiveInt(socket.getInputStream());
				System.out.printf("Forced play as the big blind with %d chips\n", cantidadBB);
			}
			else if(turn == GameType.TURN_WAIT){
				System.out.printf("Wait for the other players!\n");
			}
			else if(turn == GameType.TURN_PLAY){

				System.out.printf("\nIt's your turn to play!\n");

				// Receive round info
				sb = SocketUtils.receiveInt( socket.getInputStream() );
				bb = SocketUtils.receiveInt( socket.getInputStream() );
				maxBet = SocketUtils.receiveInt( socket.getInputStream() );
				_money = SocketUtils.receiveInt( socket.getInputStream() );

				if(blindsOnPlay){
					System.out.printf("-- Small blind bet: %d\n", sb);
					System.out.printf("-- Big blind bet: %d\n", bb);
					blindsOnPlay = false;
				}
				
				System.out.printf("-- Max bet: %d\n", maxBet);
				System.out.printf("-- Your money: %d\n\n", _money);

				boolean valid = false;
				while(!valid){

					String command = getUserCommand();
					String baseCommand = command.split(" ")[0];

					System.out.printf("Player command is %s\n", command);
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
						System.out.printf("Command %s not valid!\n", command);
						valid = false;
					}
				}
				
			}
			else{
				System.out.printf("Unknown turn code %d\n", turn);
			}

			turn = SocketUtils.receiveInt(socket.getInputStream());
		}
		System.out.printf("Round has ended!\n\n");
		
		return roundSuccess;
	}

	// Auxiliar methods
	private static int getUserPetition() {

		// Wait for clients petition
		int opcion = -1;
		while(opcion == -1){
			System.out.printf("Que desea hacer?\n");
			System.out.printf("1- Crear partida\n");
			System.out.printf("2- Unirse a partida\n");
			System.out.printf("> ");
			opcion = _scanner.nextInt();

			if(opcion != 1 && opcion != 2){
				opcion = -1;
				System.out.printf("Code petition unknown %d\n", opcion);
			}
		}

		return opcion;
	}

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

    @Override
    public void start(Stage stage) throws Exception {

        // Llamar a este metodo con: launch(args);
        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }

}