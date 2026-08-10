package com.ucm.client;

import java.security.MessageDigest;
import java.util.Random;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AvatarGenerator {

    public static Image generateHuman (String see, int size) {
        return generate(see, size);
    }

    public static Image generateBot(String name, int size) {

        String type = getTypeBot(name);
        BotStyle style = BotStyle.createByName(name);
        
        return generateBotAvatar(size, generateBotColor(type), style);
    }

    private static String getTypeBot(String name){
        if (name.startsWith("GeminiLLM")){
            return GameType.GEMINI_NAME;
        }
        else if (name.startsWith("LlamaPokerLLM")){
            return GameType.LLAMA_NAME;
        }
        else if (name.startsWith("DeepCFR")){
           return GameType.NN_MODEL_NAME;
        }
        else if (name.startsWith("FSM")){
           return GameType.FSM_NAME;
        }
        else{
            return GameType.DEFAULT_NAME;
        }
    }

    private static Color generateBotColor(String botType){
        
        int hash = Math.abs(botType.hashCode());

        double hue = hash % 360;

        return Color.hsb(hue, 0.7, 0.9);
    }

    private static Image generateBotAvatar(int size, Color mainColor, BotStyle style) {

        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(mainColor);
        gc.fillRect(0, 0, size, size);

        // Visera tipo robot
        gc.setFill(Color.rgb(0, 0, 0, 0.25));
        gc.fillRect(size * 0.1, size * 0.3, size * 0.8, size * 0.25);

        // Ojos LED
        generateEyesByStyle(gc, size, style);
        
        // Boca 
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(size * 0.03);
        generateMouthByStyle(gc, size, style);

        
        WritableImage image = new WritableImage(size, size);
        canvas.snapshot(null, image);
        return image;
    }

    private static void generateEyesByStyle(GraphicsContext gc, int size, BotStyle style){
        switch (style) {
            case DEFAULT -> {
                gc.setFill(Color.CYAN);
                gc.fillOval(size * 0.28, size * 0.33, size * 0.10, size * 0.10);

            }
            case TIGHT_PASSIVE -> {
                gc.setStroke(Color.CYAN);
                gc.strokeLine(size * 0.28, size * 0.38, size * 0.38, size * 0.38);
            }
            case TIGHT_AGGRESSIVE -> {
                gc.setStroke(Color.ORANGE);
                gc.strokeLine(size * 0.28, size * 0.42, size * 0.38, size * 0.35);
            }
            case LOOSE_PASSIVE -> {
                gc.setFill(Color.LIMEGREEN);
                gc.fillOval(size * 0.28, size * 0.32, size * 0.12, size * 0.12);
            }
            case LOOSE_AGGRESSIVE -> {
                gc.setFill(Color.YELLOW);
                gc.fillOval(size * 0.26, size * 0.30, size * 0.14, size * 0.14);
            }
            case MANIAC -> {
                gc.setFill(Color.RED);
                gc.fillOval(size * 0.24, size * 0.28, size * 0.16, size * 0.16);
            }
        }
    }

    private static void  generateMouthByStyle(GraphicsContext gc, int size, BotStyle style){
        switch (style) {
            case DEFAULT -> {
                gc.strokeLine(size * 0.35, size * 0.65, size * 0.65, size * 0.65);
            }
            case TIGHT_PASSIVE -> {
                gc.strokeLine(size * 0.38, size * 0.67, size * 0.62, size * 0.67);
            }
            case TIGHT_AGGRESSIVE -> {
                gc.strokeLine(size * 0.35, size * 0.68, size * 0.65, size * 0.62);
            }
            case LOOSE_PASSIVE -> {
                gc.strokeArc(size * 0.35, size * 0.58, size * 0.30, size * 0.15, 180, 180, javafx.scene.shape.ArcType.OPEN);
            }
            case LOOSE_AGGRESSIVE -> {
                gc.strokeArc(size * 0.30, size * 0.55, size * 0.40, size * 0.20, 180, 180, javafx.scene.shape.ArcType.OPEN);
            }
            case MANIAC -> {
                gc.setStroke(Color.RED);
                gc.strokeArc(size * 0.28, size * 0.54, size * 0.44, size * 0.22, 180, 180, javafx.scene.shape.ArcType.OPEN);
                for(int i = 0; i < 5; i++) {
                    double x = size * 0.35 + i * size * 0.07;
                    gc.strokeLine(x, size * 0.64, x, size * 0.69);
                }
            }
        }
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
