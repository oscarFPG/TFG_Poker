package com.ucm.server.gameobjects;

import java.io.IOException;
import java.io.InputStream;

import com.ucm.server.interfaces.IPlayerInfo;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;


public abstract class BotNN extends Bot {

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

    @Override
    public String getPlayerType() {
        return "BOT_NN";
    }

}