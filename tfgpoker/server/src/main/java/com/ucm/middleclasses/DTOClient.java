package com.ucm.middleclasses;

import java.net.Socket;


public record DTOClient(int playerID, int matchID, String playerName, Socket socket) {}