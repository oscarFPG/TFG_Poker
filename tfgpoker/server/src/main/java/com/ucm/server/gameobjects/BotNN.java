package com.ucm.server.gameobjects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPlayerInfo;
import com.ucm.server.interfaces.IPlayerNotificator;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;


public class BotNN implements IPlayerNotificator {

    private static final String MODEL_RESOURCE = "models_nn/dqn_holdem_model.onnx";

    private final OrtEnvironment _env;
    private final OrtSession _session;


    public BotNN() {
        try {
            _env = OrtEnvironment.getEnvironment();
            _session = createSessionFromResources(_env, MODEL_RESOURCE);
        } 
        catch (IOException | OrtException e) {
            throw new IllegalStateException("Cannot load ONNX model from resources: " + MODEL_RESOURCE, e);
        }
    }

    
    private static OrtSession createSessionFromResources(OrtEnvironment env, String resourcePath) throws IOException, OrtException {

        ClassLoader classLoader = BotNN.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            byte[] modelBytes = inputStream.readAllBytes();

            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);

            return env.createSession(modelBytes, options);
        }
    }

    public int predict(float[] state) throws OrtException {

        long[] shape = new long[]{1, state.length};
        OnnxTensor inputTensor = OnnxTensor.createTensor(_env, FloatBuffer.wrap(state), shape);
        String inputName = _session.getInputNames().iterator().next();

        Map<String, OnnxTensor> inputs = Map.of(inputName, inputTensor);
        try (OrtSession.Result results = _session.run(inputs)) {

            float[][] output = (float[][]) results.get(0).getValue();

            int bestAction = 0;
            float maxQ = output[0][0];

            for (int i = 1; i < output[0].length; i++) {
                if (output[0][i] > maxQ) {
                    maxQ = output[0][i];
                    bestAction = i;
                }
            }

            return bestAction;
        }
    }


    @Override
    public String notifyMakePlay(int sb, int bb, int maxBet, IPlayerInfo player) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyMakePlay'");
    }


    @Override
    public void notifySmallBlindBet(int amount, IPlayerInfo player) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifySmallBlindBet'");
    }


    @Override
    public void notifyBigBlindBet(int amount, IPlayerInfo player) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyBigBlindBet'");
    }


    @Override
    public void notifyPlayerRole(PlayerRole role) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerRole'");
    }


    @Override
    public void notifyPlayerCard(Card c) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerCard'");
    }


    @Override
    public void notifyTableCard(Card c) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTableCard'");
    }


    @Override
    public void notifyTotalPot(int total) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTotalPot'");
    }


    @Override
    public void notifyOtherPlayerAction(IPlayerInfo other) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyOtherPlayerAction'");
    }


    @Override
    public void notifyOwnState(IPlayerInfo player) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyOwnState'");
    }


    @Override
    public void notifyOtherPlayerState(IPlayerInfo other) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyOtherPlayerState'");
    }


    @Override
    public void notifyEndPlayerState() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyEndPlayerState'");
    }


    @Override
    public void notifyCurrentTurnPlayer(IPlayerInfo player) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyCurrentTurnPlayer'");
    }


    @Override
    public void notifyTurnWait() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTurnWait'");
    }


    @Override
    public void notifyTurnPlay() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTurnPlay'");
    }


    @Override
    public void notifyRoundEnded() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyRoundEnded'");
    }


    @Override
    public void notifyHandEndsByFolds() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandEndsByFolds'");
    }


    @Override
    public void notifyGameEnded() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameEnded'");
    }


    @Override
    public void notifyGameKeeps() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameKeeps'");
    }


    @Override
    public void notifyGameWinner() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameWinner'");
    }


    @Override
    public void notifyGameLoser() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameLoser'");
    }


    @Override
    public void notifyEquity(double equity) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyEquity'");
    }

}