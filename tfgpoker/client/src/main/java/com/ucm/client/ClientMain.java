package com.ucm.client;


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

// GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ClientMain extends Application {

    private static String hostname = "localhost";
    private static String _name;
	private static boolean _gameStarts;

	private static Scanner _scanner;
    /*
     * Desde la ruta TFGPOKER/tfgpoker
     * 		.\mvnw.cmd clean install
     * Run:
     * 		.\mvnw.cmd -pl client -Prun exec:java
     * Debug:
     * 		.\mvnwDebug.cmd -pl client -Pdebug exec:java
     * Run the Tests
     * 		.\mvnw.cmd test
     */
    public static void main(String[] args) throws IOException {

		_scanner = new Scanner(System.in);

		// Entrar a aplicacion de poker
		Socket socket = preGame();
		game(socket);

		_scanner.close();
    }

	private static Socket preGame(){

		SocketChannel socket = null;
		try{
			socket = SocketChannel.open();
			socket.configureBlocking(false);
			socket.connect( new InetSocketAddress(hostname, GameType.PORT) );

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

			System.out.printf("Comienza la partida!\n");
			selector.close();
			socket.configureBlocking(true);
		}
		catch(IOException e) {
			System.out.printf("Error: %s\n", e.getMessage());
		}

		return (socket != null) ? socket.socket() : null;
	}

	private static void game(Socket socket){

		System.out.printf("La partida comienza!\n");
		int roleCode;
		// int cardValues[] = new int[2];
		// int cardSuits[] = new int[2];
		// int tableCardValues[] = new int[5];
		// int tableCardSuits[] = new int[5];

		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			// Recibir rol de jugador
			roleCode = SocketUtils.receiveInt(in);
			System.out.printf("Player with name \'%s\' and rol code %d\n", _name, roleCode);

			// Recibir cartas
			int cartas = SocketUtils.receiveInt(in);

			// Jugar turno/esperar a turno
			int turno1 = SocketUtils.receiveInt(in);

			// Recibir carta de tablero (1)
			// Recibir carta de tablero (2)
			// Recibir carta de tablero (3)
			int cartaMesa1 = SocketUtils.receiveInt(in);
			int cartaMesa2 = SocketUtils.receiveInt(in);
			int cartaMesa3 = SocketUtils.receiveInt(in);

			// Jugar turno/esperar a turno
			int turno2 = SocketUtils.receiveInt(in);
			// Recibir carta de tablero (4)
			int cartaMesa4 = SocketUtils.receiveInt(in);

			// Jugar turno/esperar a turno
			int turno3 = SocketUtils.receiveInt(in);
			// Recibir carta de tablero (5)
			int cartaMesa5 = SocketUtils.receiveInt(in);

			// Showdown -> Comprobar ganadores y repartir premios
			int showdown = SocketUtils.receiveInt(in);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void sendString(String msg, SocketChannel socket) throws IOException{

		ByteBuffer buffer = ByteBuffer.allocate(1 + Integer.BYTES + msg.length());
		buffer.put(GameType.DATA_TYPE_NAME);
		buffer.putInt(msg.length());
		buffer.put(msg.getBytes());
		buffer.flip();

		while(buffer.hasRemaining())
			socket.write(buffer);
	}

	private static void sendPetition(int petitionCode, SocketChannel socket) throws IOException{

		ByteBuffer buffer = ByteBuffer.allocate(1 + Integer.BYTES);
		buffer.clear();
		buffer.put(GameType.DATA_TYPE_PETITION);	// Tipo de peticion
		buffer.putInt(petitionCode);				// Codigo peticion
		buffer.flip();

		while(buffer.hasRemaining()){
			socket.write(buffer);
		}
	}

	private static void handleConnect(SelectionKey key){

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

	private static void handleReceive(SelectionKey key){

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

	private static void handleSend(SelectionKey key){
		
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

	private static int getUserPetition(){

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

	private static void sendStartGameByHost(SocketChannel socket) throws IOException{

		// Wait for host to start the game
		String command = null;
		while(command == null){
			System.out.printf("Escriba \'start\' para comenzar la partida...\n > ");
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

    @Override
    public void start(Stage stage) throws Exception {

        // Llamar a este metodo con: launch(args);
        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }

}