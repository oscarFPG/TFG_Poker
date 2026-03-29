package com.ucm.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;


public class ClientMain {
    
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
	private static Scanner _scanner = new Scanner(System.in);

    /*
     * Desde la ruta TFGPOKER/tfgpoker
     *      .\mvnw.cmd clean install
     * Run client:
     *      .\mvnw.cmd -pl client -Prun exec:java
     * Run client in local mode (no client, only for testing):
     *      .\mvnw.cmd -pl client -Prun-local exec:java -Dn=<int>
     * Debug:
     *      .\mvnwDebug.cmd -pl client -Pdebug exec:java
     */
    public static void main(String[] args) {

        System.out.printf("Specify the server IP (default: localhost): ");
		String serverIP = _scanner.nextLine();
		if (!serverIP.trim().isEmpty()) {
			_hostname = serverIP.trim();
		}

		try {

			Socket socket = preGame(_hostname);

			socket.close();
			_scanner.close();
		}
		catch(IOException e) {
			System.out.printf("%s\n", e.getMessage());
		}

    }


    private static Socket preGame(final String serverIP) throws IOException {

        Socket socket = new Socket(serverIP, GameType.PORT);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        

        System.out.printf("Write your username: ");
        _name = _scanner.next();
        SocketUtils.sendString(out, _name);

        int option = getUserPetition();
        if(option == 1) {

            SocketUtils.sendInteger(out, GameType.PETITION_CREATE_GAME);
            System.out.printf("CREATE_GAME sent\n");

            System.out.printf("Do you want to add bots?\n");
            System.out.printf("[Y]es/[N]o : ");
            String input = _scanner.next();
            if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("yes")) {
                SocketUtils.sendInteger(out, GameType.PETITION_ADD_BOTS);
            }
            else {
                SocketUtils.sendInteger(out, GameType.PETITION_NOT_ADD_BOTS);
            }

            boolean gameStarts = false;
            while(!gameStarts) {

                System.out.printf("Waiting...\n");
                int code = SocketUtils.receiveInt(in);
                System.out.printf("");

                if(code == GameType.GAME_STARTS) {
                    gameStarts = true;
                }
                else if(code == GameType.EVENT_PLAYER_JOINED) {

                    System.out.printf("Player's info\n\n");

                    int numPlayers = SocketUtils.receiveInt(in);
                    System.out.printf("%d players in the game\n", numPlayers);
                    for(int i = 0; i < numPlayers; i++) {
                        String name = SocketUtils.receiveString(in);
                        System.out.printf("Player %s in the game\n", name);
                    }
                    System.out.printf("List completed!\n");

                }

            }

        }
        else if(option == 2) {

            SocketUtils.sendInteger(out, GameType.PETITION_JOIN_GAME);
            System.out.printf("JOIN_GAME sent\n");

            boolean gameStarts = false;
            while(!gameStarts) {

                System.out.printf("Waiting...\n");
                int code = SocketUtils.receiveInt(in);
                if(code == GameType.GAME_STARTS) {
                    gameStarts = true;
                }
                else if(code == GameType.EVENT_PLAYER_JOINED) {
                    
                    System.out.printf("Player's info\n\n");

                    int numPlayers = SocketUtils.receiveInt(in);
                    System.out.printf("%d players in the game\n", numPlayers);
                    for(int i = 0; i < numPlayers; i++) {
                        String name = SocketUtils.receiveString(in);
                        System.out.printf("Player %s in the game\n", name);
                    }
                    System.out.printf("List completed!\n");

                }
            }
            System.out.printf("Game starts!\n");

        }
        else {
            throw new IOException( String.format("Option not found: %d", option) );
        }


        System.out.printf("Pregame ends!\n");
        return socket;
    }


    private static int getUserPetition() {

		// Wait for clients petition
		int option = -1;
		while(option == -1){
			System.out.printf("What do you want do?\n");
			System.out.printf("1- Create game\n");
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


}