package com.ucm.client;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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


public class PokerGame {

	private static class Card {
		public int numberCode;
		public int suitCode;

		public Card() {
			this.numberCode = -1;
			this.suitCode = -1;
		}

		public Card(int number, int suit) {
			this.numberCode = number;
			this.suitCode = suit;
		}
	}


	public static final String LOCAL_HOST = "localhost"; 
	

	private PokerGame() {}


	public static Socket connect(final String serverIP) throws IOException {

		Socket socket = new Socket(serverIP, GameType.PORT);
		return socket;
	}

	public static boolean checkIpValid(final String IP){
		return !IP.trim().isEmpty();
	}



}