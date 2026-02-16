package com.ucm;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

// GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ClientMain extends Application {

    private static String hostname = "localhost";
    private static String name;

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

		SocketChannel socket = null;
		ByteBuffer buffer = null;
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Introduzca un nombre: ");
        name = scanner.next();
        System.out.printf("Bienvenido %s\n", name);
       
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(false);
            socket.connect( new InetSocketAddress(hostname, GameType.PORT) );

            while(!socket.finishConnect()){}    // Wait until connection is finished

			System.out.printf("Que desea hacer?\n");
			System.out.printf("1- Crear partida\n");
			System.out.printf("2- Unirse a partida\n");
			System.out.printf("> ");
			int opcion = scanner.nextInt();

			if(opcion == 1){

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
				System.out.printf("Nombre enviado correctamente\n");
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
			}
			else{
				System.out.printf("Opcion no reconocida\n");
			}

			while(true){}
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

    }

    @Override
    public void start(Stage stage) throws Exception {

        // Llamar a este metodo con: launch(args);
        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }

}