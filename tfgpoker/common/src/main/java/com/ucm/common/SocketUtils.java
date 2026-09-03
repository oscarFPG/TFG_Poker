package com.ucm.common;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * Utility class that provides helper methods for sending and receiving
 * primitive data types through network streams.
 * <p>
 * The methods defined in this class implement a simple communication protocol
 * where strings are transmitted with their length prefix and integers are
 * transmitted using their 4-byte binary representation.
 * </p>
 *
 * <p>
 * This is a utility class and cannot be instantiated.
 * </p>
 */
public class SocketUtils {

    private SocketUtils() {}
    /**
     * Sends a string through the specified output stream.
     * <p>
     * The string is encoded using UTF-8 and transmitted as:
     * </p>
     * <ol>
     *   <li>The length of the encoded string as an integer.</li>
     *   <li>The string bytes.</li>
     * </ol>
     *
     * @param out output stream used to send the data
     * @param msg string to send
     * @throws IOException if an I/O error occurs while writing to the stream
     */
    public static void sendString(OutputStream out, String msg) throws IOException {

        byte buffer[] = msg.getBytes(StandardCharsets.UTF_8);
        sendInteger(out, buffer.length);
        out.write( buffer );
        out.flush();
    }
    /**
     * Receives a UTF-8 encoded string from the specified input stream.
     * <p>
     * The method first reads the string length and then reads the exact number
     * of bytes required to reconstruct the original string.
     * </p>
     *
     * @param in input stream used to receive the data
     * @return the received string
     * @throws IOException if an I/O error occurs or the connection is closed
     *                     before all expected bytes are received
     */
    public static String receiveString(InputStream in) throws IOException {
        
        int stringSize = receiveInt(in);
        byte[] buffer = new byte[stringSize];
        
        int totalRead = 0;
        while (totalRead < stringSize) {
            int bytesRead = in.read(buffer, totalRead, stringSize - totalRead);
            if (bytesRead == -1) 
                throw new IOException("Client closed connection correctly");
            totalRead += bytesRead;
        }

        return new String(buffer, StandardCharsets.UTF_8);
    }
    /**
     * Sends an integer through the specified output stream.
     * <p>
     * The integer is serialized using its standard 4-byte binary
     * representation.
     * </p>
     *
     * @param out output stream used to send the data
     * @param data integer value to send
     * @throws IOException if an I/O error occurs while writing to the stream
     */
    public static void sendInteger(OutputStream out, int data) throws IOException {

        byte[] buffer = ByteBuffer.allocate(Integer.BYTES).putInt(data).array();
        out.write(buffer);
        out.flush();
    }
    /**
     * Receives an integer from the specified input stream.
     * <p>
     * The method blocks until the 4 bytes required to reconstruct the integer
     * have been received.
     * </p>
     *
     * @param in input stream used to receive the data
     * @return the received integer value
     * @throws IOException if an I/O error occurs or the connection is closed
     * before all expected bytes are received
     */
    public static int receiveInt(InputStream in) throws IOException {
       
        byte[] buffer = new byte[Integer.BYTES];

        int totalRead = 0;
        while (totalRead < Integer.BYTES) {
            int bytesRead = in.read(buffer, totalRead, Integer.BYTES - totalRead);
            if (bytesRead == -1) 
                throw new IOException("Client closed connection correctly");
            totalRead += bytesRead;
        }

        return ByteBuffer.wrap(buffer).getInt();
    }
    
}
