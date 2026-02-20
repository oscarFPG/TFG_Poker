package com.ucm;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;


// GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ClientMain extends Application {

    private static String hostname = "localhost";
    private static String _name;
	private static boolean hostStartsGame;
	private static boolean gameStarts;

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
    public static void main(String[] args) {

		_scanner = new Scanner(System.in);
		preGame();
		_scanner.close();

		/*
		// TODO: Asociar toda esta logica siguiente con el metodo preGame()
		// Pregame
		SocketChannel socket = null;
		ByteBuffer buffer = null;
		Scanner scanner = new Scanner(System.in);
       
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(false);
            socket.connect( new InetSocketAddress(hostname, GameType.PORT) );

			hostStartsGame = false;
			gameStarts = false;
            while(!socket.finishConnect()){}    // Wait until connection is finished
			System.out.printf("Conectado correctamente!\n");

			System.out.printf("Introduzca un nombre: ");
			name = scanner.next();
			System.out.printf("Bienvenido %s\n", name);

			System.out.printf("Que desea hacer?\n");
			System.out.printf("1- Crear partida\n");
			System.out.printf("2- Unirse a partida\n");
			System.out.printf("> ");
			int opcion = scanner.nextInt();

			if(opcion == 1){

				// Enviar peticion de crear partida
				buffer = ByteBuffer.allocate(1 + Integer.BYTES);
				buffer.clear();
				buffer.put(GameType.PETITION_TYPE);			// Tipo de peticion
				buffer.putInt(GameType.CREATE_PETITION);	// Codigo peticion
				buffer.flip();

				while(buffer.hasRemaining()){
					socket.write(buffer);
				}
				System.out.printf("Peticion CREATE mandada!\n");

				// Enviar nombre de usuario al servidor
				buffer = ByteBuffer.allocate(1 + 4 + name.length());
				buffer.clear();
				buffer.put(GameType.NAME_TYPE);		// Tipo de dato
				buffer.putInt(name.length());		// Tamaño en bytes
				buffer.put(name.getBytes());		// Dato
				buffer.flip();

				while(buffer.hasRemaining()){
					socket.write(buffer);
				}
				System.out.printf("Nombre enviado correctamente!\n");
				System.out.printf("Esperando a más jugadores...\n");

				// Esperar al host para solicitar el comienzo de partida
				while(!hostStartsGame){

					System.out.printf("Escribe \'start\' para comenzar la partida...\n");
					String comando = scanner.next();
					hostStartsGame = (comando.equalsIgnoreCase("start")) ? true : false;
				}
				System.out.printf("Host comienza la partida!\n");

				// Enviar peticion comenzar partida
				buffer = ByteBuffer.allocate(1 + Integer.BYTES);
				buffer.clear();
				buffer.put(GameType.PETITION_TYPE);			// Tipo de peticion
				buffer.putInt(GameType.HOST_START_GAME);	// Codigo peticion
				buffer.flip();

				while(buffer.hasRemaining()){
					socket.write(buffer);
				}
				System.out.printf("Peticion HOST START GAME mandada!\n");

				// Esperar a recibir el aviso de comienzo de partida 
				int bytesRead;
				buffer = ByteBuffer.allocate(Integer.BYTES);
				while(!gameStarts){

					bytesRead = socket.read(buffer);
					if(bytesRead == -1){
						System.out.printf("Error esperando datos del servidor");
						throw new IOException("Waiting on server");
					}

					if(bytesRead != 0){

						buffer.flip();
						while (buffer.remaining() >= 4) {
							int code = buffer.getInt();
                    		System.out.printf("Peticion recibida %d\n", code);
							gameStarts = (code == GameType.GAME_STARTS) ? true : false;
						}
						buffer.compact();
					}
				}
				System.out.printf("La partida comienza!\n");

				// Comenzar partida
				// ...
			}
			else if(opcion == 2){

				buffer = ByteBuffer.allocate(1 + Integer.BYTES);
				buffer.clear();
				buffer.put(GameType.PETITION_TYPE);		// Tipo de peticion
				buffer.putInt(GameType.JOIN_PETITION);	// Codigo peticion
				buffer.flip();

				while(buffer.hasRemaining()){
					socket.write(buffer);
				}
				System.out.printf("Peticion JOIN mandada!\n");

				// Enviar nombre de usuario al servidor
				buffer = ByteBuffer.allocate(1 + 4 + name.length());
				buffer.clear();
				buffer.put(GameType.NAME_TYPE);		// Tipo de dato
				buffer.putInt(name.length());		// Tamaño en bytes
				buffer.put(name.getBytes());		// Dato
				buffer.flip();

				while(buffer.hasRemaining()){
					socket.write(buffer);
				}
				System.out.printf("Nombre enviado correctamente\n");

				// Esperar a recibir el aviso de comienzo de partida 
				int bytesRead;
				buffer = ByteBuffer.allocate(Integer.BYTES);
				while(!gameStarts){

					bytesRead = socket.read(buffer);
					if(bytesRead == -1){
						System.out.printf("Error esperando datos del servidor");
						throw new IOException("Waiting on server");
					}

					if(bytesRead != 0){

						buffer.flip();
						while (buffer.remaining() >= 4) {
							int code = buffer.getInt();
                    		System.out.printf("Peticion recibida %d\n", code);
							gameStarts = (code == GameType.GAME_STARTS) ? true : false;
						}
						buffer.compact();
					}
				}
				System.out.printf("La partida comienza!\n");

			}
			else{
				System.out.printf("Opcion no reconocida\n");
			}

        }
        catch(IOException e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }
        finally {
			scanner.close();
			try {
				socket.close();
			}
			catch (IOException e) {}
        }

		*/

		// TODO: Asociar todo la logica siguiente al metodo game()
		// Game
		// ...

    }

	private static void preGame(){

		try{
			SocketChannel socket = SocketChannel.open();
			socket.configureBlocking(false);
			socket.connect( new InetSocketAddress(hostname, GameType.PORT) );

			Selector selector = Selector.open();
			socket.register(selector, SelectionKey.OP_CONNECT);

			while(true){
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
		}
		catch(IOException e) {
			System.out.printf("Error: %s\n", e.getMessage());
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
			// TODO: HACER

		}
		catch (IOException e) {
			System.out.printf("Error connecting to server: %s\n", e.getMessage());
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