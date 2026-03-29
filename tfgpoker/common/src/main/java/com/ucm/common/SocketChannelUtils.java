package com.ucm.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SocketChannelUtils {
 
    public static final int MAX_STRING_LENGTH = 256;

    private SocketChannelUtils() {}


    public static void sendString(SocketChannel socket, final String msg) throws IOException {

        int size = Integer.BYTES;
        int msgSize = Math.min(msg.length(), MAX_STRING_LENGTH);
        ByteBuffer buffer = ByteBuffer.allocate(msgSize + size);
		buffer.putInt(msgSize);
		buffer.put(msg.getBytes(StandardCharsets.UTF_8), 0, msgSize);
		buffer.flip();

		while(buffer.hasRemaining())
			socket.write(buffer);
    }

    public static String receiveString(SocketChannel socket) throws IOException {

        int msgSize = receiveInteger(socket);
        ByteBuffer buffer = ByteBuffer.allocate(msgSize);

        while (buffer.hasRemaining()) {

            int bytesRead = socket.read(buffer);
            if (bytesRead == -1) {
                throw new IOException("Connection closed while reading string");
            }
        }

        buffer.flip();
        return new String(buffer.array(), 0, msgSize, StandardCharsets.UTF_8);
    }

    public static void sendInteger(SocketChannel socket, final int data) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate( Integer.BYTES );
		buffer.putInt(data);
        buffer.flip();

		while(buffer.hasRemaining())
			socket.write(buffer);
    }

    public static int receiveInteger(SocketChannel socket) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        while (buffer.hasRemaining()) {

            int bytesRead = socket.read(buffer);
            if (bytesRead == -1) {
                throw new IOException("Connection closed before reading integer");
            }
        }

        buffer.flip();
        return buffer.getInt();
    }

}