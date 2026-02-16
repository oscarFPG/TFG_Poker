package com.ucm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.lang.Thread;

// GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClientMain extends Application {

    private static String hostname = "localhost";
    private static String name;
    private static Scanner scanner;

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     * .\mvnw.cmd clean install
     * Run:
     * .\mvnw.cmd -pl client -Prun exec:java
     * Debug:
     * .\mvnwDebug.cmd -pl client -Pdebug exec:java
     * Run the Tests
     * .\mvnw.cmd test
     */
    public static void main(String[] args) {

        SocketChannel socket = null;
        SocketChannel hostSocket = null;
        ByteBuffer readBuffer, writeBuffer;
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(false);

            socket.connect(new InetSocketAddress(hostname, GameType.PORT));
            System.out.printf("Client connected!\n");

            // Check if SocketChannel is already connected and avoid busy wait
            while (!socket.finishConnect()) {
                Thread.sleep(500);
            }

            // Get client name
            scanner = new Scanner(System.in);
            System.out.printf("Write your username: ");
            name = scanner.next();

            // Get clients petition
            System.out.printf("1-Create(C)\n2-Join(J)\nWrite option name: ");
            String input = scanner.next();
            scanner.close();

            // Send client petition
            if (input.equalsIgnoreCase("create") || input.equalsIgnoreCase("c")) {

                // 1 byte de tipo + 4 bytes de integer(tamaño string) + integer
                writeBuffer = ByteBuffer.allocate(1 + Integer.BYTES);
                writeBuffer.put(GameType.INTEGER_TYPE);
                writeBuffer.putInt(GameType.CREATE_PETITION);

                writeBuffer.flip();
                socket.write(writeBuffer);

                // Recibir nuevo puerto de partida
                readBuffer = ByteBuffer.allocate(Integer.BYTES);
                readBuffer.clear();

                int bytesRead = 0;
                while (readBuffer.hasRemaining()) {
                    bytesRead = socket.read(readBuffer);
                }
                readBuffer.flip();

                int puerto = readBuffer.getInt();
                System.out.printf("Nuevo puerto es %d\n", puerto);

                hostSocket = SocketChannel.open();
                hostSocket.configureBlocking(false);
                hostSocket.connect(new InetSocketAddress(hostname, puerto));
                System.out.printf("Client connected to host socket!\n");

                readBuffer = ByteBuffer.allocate(Integer.BYTES);
                readBuffer.clear();
                boolean salaAbierta = false;
                boolean empezarPartida = false;
                while (!empezarPartida) {

                    scanner = new Scanner(System.in);
                    System.out.printf("Que desea hacer?\n");
                    System.out.printf("C - Cerrar/abrir privacidad de la sala\n");
                    System.out.printf("S - Comenzar partida con los jugadores actuales\n");
                    System.out.printf("> Opcion: ");
                    String opcion = scanner.next();

                    if (opcion.equalsIgnoreCase("c")) { // Abrir/cerrar sala

                        salaAbierta = !salaAbierta;
                        writeBuffer = ByteBuffer.allocate(Integer.BYTES);
                        int code = (salaAbierta) ? GameType.MATCH_OPEN : GameType.MATCH_CLOSED;

                        writeBuffer.putInt(code);
                        writeBuffer.flip();
                        hostSocket.write(writeBuffer);
                    } else if (opcion.equalsIgnoreCase("s")) { // Comenzar partida

                        writeBuffer = ByteBuffer.allocate(Integer.BYTES);
                        writeBuffer.putInt(GameType.GAME_STARTS);
                        writeBuffer.flip();
                        socket.write(writeBuffer);

                        empezarPartida = true;
                    }
                }

                // Recibir señal de start del servidor
                while (bytesRead != Integer.BYTES) {

                    int bytes = socket.read(readBuffer);
                    if (bytes == -1) {
                        System.out.printf("Conexión host cerrada de forma inesperada\n");
                        hostSocket.close();
                    }

                    bytesRead += bytes;
                }
                System.out.printf("Match starts\n");

            } else if (input.equalsIgnoreCase("join") || input.equalsIgnoreCase("j")) {

                // 1 byte de tipo + 4 bytes de integer(tamaño string) + integer
                writeBuffer = ByteBuffer.allocate(1 + Integer.BYTES);
                writeBuffer.put(GameType.INTEGER_TYPE);
                writeBuffer.putInt(GameType.JOIN_PETITION);

                writeBuffer.flip();
                socket.write(writeBuffer);
            } else {
                System.out.printf("Petition not found\n");
            }

        } catch (NoSuchElementException e) { // El programa termina mientras se tiene el Scanner abierto
            scanner.close();
        } catch (IOException | InterruptedException e) {
            System.out.printf("ERROR: %s\n", e.getMessage());
        } finally {

            try {
                socket.close();
            } catch (IOException e) {
                System.out.printf("Error closing socket: %s\n", e.getMessage());
            }
        }

        // launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setScene(new Scene(new Label("Aplicacion Poker"), 300, 200));
        stage.setTitle("Poker TFG");
        stage.show();
    }

}