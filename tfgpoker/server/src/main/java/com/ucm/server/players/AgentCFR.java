package com.ucm.server.players;


import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotNN;
import com.ucm.server.interfaces.IPlayerInfo;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession.Result;

/**
 * Deep CFR trained for 1v1 poker games (Heads-up)
 * 
 * AgentCFR
 */
public class AgentCFR extends BotNN {

    private static final boolean DEBUG_CFR = false;

    private static final int CFR_ID = GameType.BOT_NN_MODEL_1;
    private static final String CFR_FILENAME = "poker_cfr.onnx";
    public static final String CFR_NAME = "DeepCFR";

    // Action indexes
    private static final int FOLD_IDX = 0;
    private static final int CALL_IDX = 1;
    private static final int RAISE_IDX = 3;
    private static final int ALL_IN_IDX = 4;

    public AgentCFR() {
        super(CFR_ID, CFR_FILENAME);
    }

  
    @Override
    public float[] encondeState(final int maxBet, IPlayerInfo player) {

        float[] state = new float[54];
        Card[] cards = player.getPlayerCards();

        // Encode cards
        int card1_index = cardToIndex(cards[0]);
        int card2_index = cardToIndex(cards[1]);
        state[card1_index] = 1.0f;
        state[card2_index] = 1.0f;

        // Encode player chips
        state[52] = (float)player.getMoneyOnBet();

        // Encode maxbet chips
        state[53] = (float)maxBet;

       return state;
    }

    @Override
    public float[] predict(float[] state) {

        try {

            long[] shape = new long[]{1, 54};
            OnnxTensor inputTensor = OnnxTensor.createTensor(_env, FloatBuffer.wrap(state), shape);
            String inputName = _session.getInputNames().iterator().next();

            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put(inputName, inputTensor);

            Result result = _session.run(inputs);
            float[][] output = (float[][]) result.get(0).getValue();

            return output[0]; // probabilities
        }
        catch (OrtException e) {
            System.out.printf("ERROR PREDICTING ON %s : %s\n", getDescription(), e.getMessage());
        }

        return null; 
    }

    /**
     * Method to select the best action predicted by the neuronal network
     * 
     * @param sb Small-blind bet in this round
     * @param bb Big-blind bet in this round
     * @param maxBet Maximum bet in this round
     * @param player Player used by this neuronal network
     * @param prediction float array with 5 positions, in order:  
     * 
     * @return 
     */
    @Override
    public String translate(int sb, int bb, int maxBet, IPlayerInfo player, float[] prediction) {
        
        if(prediction == null)
            return GameType.FOLD_ACTION_FULL;


        // Getting probabilities for each action to make and order them by decreasing prob
        float[] probs = softmax(prediction);
        int[] actions = reorderActionsByProbability(probs);

        // Select the best action to perform if it is possible
        String selectedAction = GameType.FOLD_ACTION_FULL;  // Action performed if there no other option
        boolean found = false;
        int i = 0;
        while(!found && i < actions.length) {

            int action = actions[i];
            if(action == FOLD_IDX) {
                found = true;
                selectedAction = GameType.FOLD_ACTION_FULL;
            }
            else if(action == CALL_IDX) {   // Can be "CALL" or "CHECK"
                found = true;
                selectedAction = (maxBet == 0) ? GameType.CHECK_ACTION_FULL : GameType.CALL_ACTION_FULL;
            }
            else if(action == RAISE_IDX) {  // Always doubles the maximum bet if it is possible
                found = true;

                // If there is no bet on the table -> 10% of the total money
                int targetBet = 2 * maxBet;
                if(targetBet == 0) {    // Caused because maxBet is zero
                    selectedAction = GameType.CHECK_ACTION_FULL;
                }
                else if(targetBet <= player.getMoneyOffBet() + player.getMoneyOnBet()) {
                    selectedAction = GameType.RAISE_ACTION_FULL + " " + targetBet;
                }
            }
            else if(action == ALL_IN_IDX) {
                found = true;
                selectedAction = GameType.ALL_IN_ACTION_FULL;
            }

            ++i;
        }

        return selectedAction;
    }



    /**
     * Softmax function to get the probability based on the logits
     * @param logits
     * @return probabilities array
     */
    private float[] softmax(float[] logits) {

        // Getting the max value
        float max = Float.NEGATIVE_INFINITY;
        for (float v : logits)
            max = Math.max(max, v);


        float sum = 0f;
        float[] result = new float[logits.length];
        for (int i = 0; i < logits.length; i++) {
            result[i] = (float) Math.exp(logits[i] - max);
            sum += result[i];
        }

        for (int i = 0; i < logits.length; i++)
            result[i] /= sum;

        return result;
    }

    /**
     * Index of every action ordered by probability (max to min)
     * @param probs probabilities of each action -> probs[i] = x means action i has a x probability where 0 <= x <= 1
     * @return
     */
    private int[] reorderActionsByProbability(final float[] probs) {

        // Getting the indexes
        Integer[] indices = new Integer[probs.length];
        for (int i = 0; i < probs.length; i++)
            indices[i] = i;

        // Order from max to min
        Arrays.sort(indices, (a, b) -> Float.compare(probs[b], probs[a]));

        // Create result array
        int[] result = new int[probs.length];
        for (int i = 0; i < probs.length; i++)
            result[i] = indices[i];

        if(DEBUG_CFR)
            showProbDistribution(result, probs);

        return result;
    }

    private int cardToIndex(final Card c) {

        Suit suit = c.getSuit();
        int value = c.getCardValueNetworkCode();

        int row = (suit == Suit.SPADES) ? 0 :
                  (suit == Suit.HEARTS) ? 1 :
                  (suit == Suit.DIAMONDS) ? 2 :
                  3;

        int column = (value == GameType.NUMBER_ACE) ? 0 :
                     (value == GameType.NUMBER_TWO) ? 1 :
                     (value == GameType.NUMBER_THREE) ? 2 :
                     (value == GameType.NUMBER_FOUR) ? 3 :
                     (value == GameType.NUMBER_FIVE) ? 4 :
                     (value == GameType.NUMBER_SIX) ? 5 :
                     (value == GameType.NUMBER_SEVEN) ? 6 :
                     (value == GameType.NUMBER_EIGHT) ? 7 :
                     (value == GameType.NUMBER_NINE) ? 8 :
                     (value == GameType.NUMBER_TEN) ? 9 :
                     (value == GameType.NUMBER_J) ? 10 :
                     (value == GameType.NUMBER_Q) ? 11 :
                     12;

        return row * 13 + column;
    }

    private void showProbDistribution(int[] result, float[] probs) {

        // Show probability of each action
        System.out.printf("Predictions:\n");
        for(int idx : result) {
            float prob = probs[idx];
            System.out.printf("Action %d : %.2f%%\n", idx, prob * 100);
        }
        System.out.printf("\n");
    }

    @Override
    public String getFullDescription() {
        return "Neuronal network based on a CFR agent(Counterfactual Regret Minimization)";
    }

    @Override
    public String getDescription() {
        return "AgentCFR_1";
    }

    @Override
    public Bot create(BotStyle style) {
        return new AgentCFR();
    }

    @Override
    public String getPlayerModel() {
        return "AgentCFR_1";
    }


}