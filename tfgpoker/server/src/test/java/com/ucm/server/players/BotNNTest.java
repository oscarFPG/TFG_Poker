package com.ucm.server.players;


import org.junit.jupiter.api.Test;

import com.ucm.server.gameobjects.BotNN;
import com.ucm.server.gameobjects.Player;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;


public class BotNNTest {

    @Test
    public void load() {
        OrtEnvironment env = OrtEnvironment.getEnvironment();
        System.out.println("OK");
    }

    @Test
    public void loadTest() throws OrtException {
    
        BotNN botNN = new BotNN();

        float[] state = new float[] {
            // 0–1: cartas jugador
            0.72f, 0.10f,

            // 2–11: board (5 cartas * 2 features)
            0.33f, 0.80f,   // carta 1
            0.55f, 0.20f,   // carta 2
            0.91f, 0.10f,   // carta 3
            0.00f, 0.00f,   // carta 4 (no existe)
            0.00f, 0.00f,   // carta 5 (no existe)

            // 12: número de cartas en mesa (normalizado)
            0.60f,

            // 13–15: economía del juego
            0.45f,   // pot
            0.70f,   // stack hero
            0.65f,   // stack rival

            // 16–17: contexto
            1.0f,    // in position
            1.0f,    // to act

            // 18–23: legal actions (fold, check, call, bet, raise, all-in)
            1f, 1f, 1f, 0f, 1f, 0f,

            // 24–28: historial (últimas acciones)
            0f, 2f, 3f, 1f, 0f,

            // 29: street (flop)
            1f,

            // 30–35: padding / bias / extras
            1f, 0f, 0f, 0f, 0f, 1f
        };

        int action = botNN.predict(state);
        System.out.printf("Neuronal network action: %d\n", action);

        //Player player = new Player(0, "Bot-NN", 1000, botNN);
    }

}