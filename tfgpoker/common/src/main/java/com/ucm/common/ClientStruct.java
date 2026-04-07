package com.ucm.common;

import java.net.Socket;


public record ClientStruct(String name, Socket socket) {}