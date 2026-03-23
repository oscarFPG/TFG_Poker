package com.ucm.server.middleclasses;

import java.net.Socket;

public record ClientStructGame(String name, Socket socket) {}