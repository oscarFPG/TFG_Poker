package com.ucm.client;

import java.security.MessageDigest;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AvatarGenerator {

    public static Image generateHuman (String see, int size) {
        return generate(see, size);
    }

    public static Image generateBot(AvatarType type, int size) {
        switch (type) {
            case GEMINI_BOT:
                return generateBotAvatar("GEMINI_BOT", size, Color.web("#7B61FF"));
            case LLAMA_BOT:
                return generateBotAvatar("LLAMA_BOT", size, Color.web("#1417dd"));
            default:
                throw new IllegalArgumentException("Unsupported avatar type");
        }
    }

    private static Image generateBotAvatar(String seed, int size, Color mainColor) {

        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(mainColor);
        gc.fillRect(0, 0, size, size);

        // Visera tipo robot
        gc.setFill(Color.rgb(0, 0, 0, 0.25));
        gc.fillRect(size * 0.1, size * 0.3, size * 0.8, size * 0.25);

        // Ojos LED
        gc.setFill(Color.CYAN);
        gc.fillOval(size * 0.3, size * 0.37, size * 0.1, size * 0.1);
        gc.fillOval(size * 0.6, size * 0.37, size * 0.1, size * 0.1);

        WritableImage image = new WritableImage(size, size);
        canvas.snapshot(null, image);
        return image;
    }
    
    private static Image generate(String seed, int size) {

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
