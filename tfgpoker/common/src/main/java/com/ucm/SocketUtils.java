package com.ucm;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.io.IOException;


public class SocketUtils {

    private SocketUtils(){}

    public static void sendString(OutputStream out, String data) throws IOException {

        byte buffer[] = data.getBytes(StandardCharsets.UTF_8);
        sendInteger(out, buffer.length);
        out.write( buffer );
        out.flush();
    }

    public static String receiveString(InputStream in) throws IOException {
        
        int stringSize = receiveInt(in);
        byte[] buffer = new byte[stringSize];
        
        int totalRead = 0;
        while (totalRead < stringSize) {
            int bytesRead = in.read(buffer, totalRead, stringSize - totalRead);
            if (bytesRead == -1) 
                throw new IOException("DataStream closed");
            totalRead += bytesRead;
        }

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static void sendInteger(OutputStream out, int data) throws IOException {

        byte[] buffer = ByteBuffer.allocate(Integer.BYTES).putInt(data).array();
        out.write(buffer);
        out.flush();
    }

    public static int receiveInt(InputStream in) throws IOException {
       
        byte[] buffer = new byte[Integer.BYTES];

        int totalRead = 0;
        while (totalRead < Integer.BYTES) {
            int bytesRead = in.read(buffer, totalRead, Integer.BYTES - totalRead);
            if (bytesRead == -1) 
                throw new IOException("DataStream closed");
            totalRead += bytesRead;
        }

        return ByteBuffer.wrap(buffer).getInt();
    }
    
}