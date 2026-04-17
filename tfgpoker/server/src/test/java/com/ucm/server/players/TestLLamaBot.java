package com.ucm.server.players;


import org.junit.jupiter.api.Test;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;


public class TestLLamaBot {

    private static final int INIT_MONEY = 1000;

    @Test
    private void testPromptBasic_2Players() {

        LlamaPokerLLM llama = new LlamaPokerLLM(0, INIT_MONEY);

    }

    //@Test
    private void testFullPrompt() {

        LlamaPokerLLM bot = new LlamaPokerLLM(1, 100);

        // =========================
        // ESCENARIO 1 (RAISE)
        // =========================
        
        bot.notifyPlayerRole(PlayerRole.HIJACK); // HJ

        bot.notifyPlayerCard(new Card(11, Suit.CLUBS)); // Jc
        bot.notifyPlayerCard(new Card(10, Suit.CLUBS)); // Tc

        bot.notifyTableCard(new Card(11, Suit.SPADES)); // Js
        bot.notifyTableCard(new Card(13, Suit.SPADES)); // Ks
        bot.notifyTableCard(new Card(5, Suit.DIAMONDS)); // 5d
        bot.notifyTableCard(new Card(11, Suit.DIAMONDS)); // Jd

        bot.notifySmallBlindBet(1);
        bot.notifyBigBlindBet(2);

        //bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN, "raise", 2.0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "call", 2.0);

        //bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN_1, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "bet", 4);
        //bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN_2, "raise", 13);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "call", 13);

        //bot.notifyPlayerAction(PlayerRole.LOJACK, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "bet", 23);

        System.out.println("SCENARIO 1: " + bot.notifyMakePlay(1, 1, 100));
        bot.notifyHandEnded();


        
        // =========================
        // ESCENARIO 2 (FOLD)
        // =========================
        bot.notifyPlayerRole(PlayerRole.DEALER); // BTN

        bot.notifyPlayerCard(new Card(1, Suit.CLUBS));  // Ac
        bot.notifyPlayerCard(new Card(12, Suit.SPADES)); // Qs

        bot.notifyTableCard(new Card(13, Suit.SPADES)); // Ks
        bot.notifyTableCard(new Card(7, Suit.HEARTS));  // 7h
        bot.notifyTableCard(new Card(2, Suit.DIAMONDS)); // 2d
        bot.notifyTableCard(new Card(6, Suit.HEARTS));  // 6h
        bot.notifyTableCard(new Card(10, Suit.HEARTS)); // Th

        //bot.notifyPlayerAction(PlayerRole.DEALER, "raise", 2.5);
        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "call", 2.5);

        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "check", 0);

        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "bet", 6);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "call", 6);

        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "bet", 13);
        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "raise", 41);

        System.out.println("SCENARIO 2: " + bot.notifyMakePlay(1, 1, 100));
        bot.notifyHandEnded();


        // =========================
        // ESCENARIO 3 (CALL)
        // =========================
        bot.notifyPlayerRole(PlayerRole.SMALL_BLIND); // SB

        bot.notifyPlayerCard(new Card(1, Suit.HEARTS)); // Ah
        bot.notifyPlayerCard(new Card(13, Suit.HEARTS)); // Kh

        //bot.notifyPlayerAction(PlayerRole.DEALER, "raise", 2.5);
        //bot.notifyPlayerAction(PlayerRole.SMALL_BLIND, "raise", 12);
        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "all-in", 100);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "fold", 0);

        System.out.println("SCENARIO 3: " + bot.notifyMakePlay(1, 1, 100));
        bot.notifyHandEnded();


        // =========================
        // ESCENARIO 4 (RAISE)
        // =========================
        bot.notifyPlayerRole(PlayerRole.SMALL_BLIND); // SB

        bot.notifyPlayerCard(new Card(11, Suit.SPADES)); // Js
        bot.notifyPlayerCard(new Card(10, Suit.SPADES)); // Ts

        //bot.notifyPlayerAction(PlayerRole.DEALER, "raise", 2.5);

        System.out.println("SCENARIO 4: " + bot.notifyMakePlay(1, 1, 100));
        bot.notifyHandEnded();
        
    }

}