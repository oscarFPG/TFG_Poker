package com.ucm.client;

import java.security.MessageDigest;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AvatarGenerator {
    
    public static Image generate(String seed, int size) {

        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        byte[] hash = hash(seed);
        Random rnd = new Random(bytesToLong(hash));

        Color bg = Color.color(
            rnd.nextDouble(),
            rnd.nextDouble(),
            rnd.nextDouble()
        );

        gc.setFill(bg);
        gc.fillRect(0, 0, size, size);

        // Ojos
        gc.setFill(Color.BLACK);
        gc.fillOval(size * 0.25, size * 0.35, size * 0.08, size * 0.08);
        gc.fillOval(size * 0.65, size * 0.35, size * 0.08, size * 0.08);

        // Boca
        gc.strokeArc(
            size * 0.3,
            size * 0.55,
            size * 0.4,
            size * 0.2,
            180,
            180,
            javafx.scene.shape.ArcType.OPEN
        );

        WritableImage image = new WritableImage(size, size);
        canvas.snapshot(null, image);
        return image;
    }

    private static byte[] hash(String seed) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(seed.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long bytesToLong(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < Math.min(8, bytes.length); i++) {
            value = (value << 8) | (bytes[i] & 0xff);
        }
        return value;
    }

}
