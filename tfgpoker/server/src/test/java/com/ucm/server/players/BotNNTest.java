package com.ucm.server.players;


import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.gameobjects.BotNN;
import com.ucm.server.gameobjects.Player;

import ai.onnxruntime.OrtException;


public class BotNNTest {

    @Test
    public void testEnviroment() {
        BotNN.testEnviroment();
    }

    @Test
    public void testResponse() throws OrtException {
    
        AgentCFR cfr = new AgentCFR();
        Player player = new Player(0, "DeepCFR bot", 100, cfr);
        int sb = 6;
        int bb = 2 * sb;


        try {

            Card card1 = new Card(2, Suit.CLUBS);
            Card card2 = new Card(7, Suit.DIAMONDS);

            player.receiveCard(card1);
            player.receiveCard(card2);
            player.putSmallBlindBet(sb);

            String action = cfr.notifyMakePlay(sb, bb, bb, player);

            System.out.printf("Action: %s\n", action);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}