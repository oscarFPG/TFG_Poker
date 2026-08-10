package com.ucm.server.gameobjects;

import java.io.IOException;
import java.io.InputStream;

import com.ucm.server.interfaces.IPlayerInfo;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;


/**
 * Abstract class that represents a poker bot powered by a Neural Network (NN).
 */
public abstract class BotNN extends Bot {

    /**
     * Path to the directory containing the neural network models. 
     * This path is used to locate the model files when initializing the bot.
     */
    private static final String MODEL_PATH = "models_nn/";

    /**
     * The ONNX Runtime environment used for running the neural network models.
     */
    protected OrtEnvironment _env;

    /**
     * The ONNX Runtime session that holds the loaded neural network model and is used for making predictions.
     */
    protected OrtSession _session;


    /**
     * Constructs a new BotNN instance with the specified bot identifier and model filename.
     * @param ID unique identifier for the bot
     * @param filename the filename of the neural network model to be loaded for this bot
     */
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


    /**
     * {@inheritDoc}
     * 
     * This method calls the neural network to make a decision based on the current game state. 
     * This is made calling the methods {@link #encondeState(int, IPlayerInfo)}, {@link #predict(float[])} and {@link #translate(int, int, int, IPlayerInfo, float[])}.
     */
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

    /**
     * {@inheritDoc}
     * @return "BOT_NN"
     */
    @Override
    public String getPlayerType() {
        return "BOT_NN";
    }

}