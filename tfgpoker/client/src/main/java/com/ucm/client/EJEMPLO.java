package com.ucm.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.ucm.common.GameType;
import com.ucm.common.SocketUtils;


public class EJEMPLO {
    
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
    

    private static Socket preGame(final String serverIP) throws IOException {

        Socket socket = new Socket(serverIP, GameType.PORT);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        

        System.out.printf("Write your username: ");
        _name = _scanner.next();
        SocketUtils.sendInteger(out, GameType.PETITION_PLAYER_NAME);
        SocketUtils.sendString(out, _name);

        int response = SocketUtils.receiveInt(in);
        if(response == GameType.CONFIRMATION_NAME_VALID) {
            System.out.printf("Name valid!\n");
        }
        else if(response == GameType.ERROR_NAME_TOO_SHORT) {
            System.out.printf("Name too short!\n");
        }
        else if(response == GameType.ERROR_NAME_TOO_LONG) {
            System.out.printf("Name too long!\n");
        }

        boolean test = true;
        while(test) {

        }

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