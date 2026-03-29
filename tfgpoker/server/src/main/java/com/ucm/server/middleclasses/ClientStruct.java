package com.ucm.server.middleclasses;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public record ClientStruct<T>(String name, Socket socket, BlockingQueue<T> queue) {}