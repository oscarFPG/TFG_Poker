package com.ucm.server.gameobjects;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.ucm.server.interfaces.IPlayerInfo;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class BotNN extends Bot {

    private static final Logger log = LogManager.getLogger(BotNN.class);

    private static final String MODEL_PATH = "models_nn/";

    protected OrtEnvironment _env;
    protected OrtSession _session;


    public BotNN(final int ID, final String filename) {
        super(ID);
        
        String full_path = MODEL_PATH + filename;
        try {

            InputStream is = getClass().getClassLoader()
                            .getResourceAsStream(full_path);
            if (is == null) {
                throw new IllegalStateException("No se encontró el modelo.");
            }

            byte[] modelBytes = is.readAllBytes();

            _env = OrtEnvironment.getEnvironment();
            _session = _env.createSession(modelBytes, new OrtSession.SessionOptions());
        }
        catch(IOException | OrtException e) {
            System.out.printf("ERROR LOADING MODEL with file %s : %s\n", full_path, e.getMessage());   
        }

    }

    public static void testEnviroment() {
        OrtEnvironment env = OrtEnvironment.getEnvironment();
        System.out.println(env);
    }

    @Override
    public String play(int sb, int bb, int maxBet, IPlayerInfo player) throws IOException {
        
        float[] state = encondeState(maxBet, player);
        float[] prediction = predict(state);
        String response = translate(sb, bb, maxBet, player, prediction);
        
        //  Wait for a random number of seconds to send a response
        Random rand = new Random();
        int min_sec = 5;
        int max_sec = 20;
        try {
            int randomSeconds = rand.nextInt((max_sec - min_sec) + 1) + min_sec;
            log.info("Neural Network waiting for {} seconds...", randomSeconds);
            TimeUnit.SECONDS.sleep(randomSeconds);
        }
        catch (InterruptedException e) {}   // Nothing

        return response;
    }

    /**
     * Encode all the available game context to feed the neuronal network
     * @return encoded game state
     */
    public abstract float[] encondeState(final int maxBet, IPlayerInfo player);

    /**
     * ACtion performed by the neuronal network based on the game state
     * @param state encoded as the neuronal network requires
     * @return probability of each action predicted by the neuronal network
     */
    public abstract float[] predict(float[] state);

    /**
     * Translate the prediction made by the neuronal network to perform an action in the game
     * @param prediction made by the nn
     * @return final response
     */
    public abstract String translate(int sb, int bb, int maxBet, IPlayerInfo player, final float[] prediction);

}