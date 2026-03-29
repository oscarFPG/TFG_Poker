package com.ucm.server.middleclasses;

import java.net.Socket;

public record ClientStruct(String name, Socket socket) {}