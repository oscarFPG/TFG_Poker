package com.ucm.server.players;


import org.junit.jupiter.api.Test;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;
import com.ucm.server.gameobjects.Player;


public class TestLLamaBot {

    private static final int INIT_MONEY = 1000;

    @Test
    private void testPromptBasic_2Players() {

        LlamaPokerLLM llama = new LlamaPokerLLM();
        Player player = new Player(0, "LlamaBot", INIT_MONEY, llama);

        // Roles

        // Small blind bet by llama

        // Big blind bet by llama

        // Llama's turn


    }

    //@Test
    private void testFullPrompt() {

        LlamaPokerLLM llama = new LlamaPokerLLM();
        Player player = new Player(0, "LlamaBot", INIT_MONEY, llama);

        // =========================
        // ESCENARIO 1 (RAISE)
        // =========================
        
        player.receiveRole(PlayerRole.HIJACK); // HJ

        player.receiveCard(new Card(11, Suit.CLUBS)); // Jc
        player.receiveCard(new Card(10, Suit.CLUBS)); // Tc

        player.receiveTableCard(new Card(11, Suit.SPADES)); // Js
        player.receiveTableCard(new Card(13, Suit.SPADES)); // Ks
        player.receiveTableCard(new Card(5, Suit.DIAMONDS)); // 5d
        player.receiveTableCard(new Card(11, Suit.DIAMONDS)); // Jd

        player.putSmallBlindBet(1);
        player.putBigBlindBet(2);

        //bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN, "raise", 2.0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "call", 2.0);

        //bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN_1, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "bet", 4);
        //bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN_2, "raise", 13);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "call", 13);

        //bot.notifyPlayerAction(PlayerRole.LOJACK, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "bet", 23);

        System.out.println("SCENARIO 1: " + player.makePlay(1, 1, 100));
        player.notifyRoundEnded();


        
        // =========================
        // ESCENARIO 2 (FOLD)
        // =========================
        player.receiveRole(PlayerRole.DEALER); // BTN

        player.receiveCard(new Card(1, Suit.CLUBS));  // Ac
        player.receiveCard(new Card(12, Suit.SPADES)); // Qs

        player.receiveTableCard(new Card(13, Suit.SPADES)); // Ks
        player.receiveTableCard(new Card(7, Suit.HEARTS));  // 7h
        player.receiveTableCard(new Card(2, Suit.DIAMONDS)); // 2d
        player.receiveTableCard(new Card(6, Suit.HEARTS));  // 6h
        player.receiveTableCard(new Card(10, Suit.HEARTS)); // Th

        //bot.notifyPlayerAction(PlayerRole.DEALER, "raise", 2.5);
        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "call", 2.5);

        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "check", 0);

        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "bet", 6);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "call", 6);

        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "check", 0);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "bet", 13);
        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "raise", 41);

        System.out.println("SCENARIO 2: " + player.makePlay(1, 1, 100));
        player.notifyRoundEnded();


        // =========================
        // ESCENARIO 3 (CALL)
        // =========================
        player.receiveRole(PlayerRole.SMALL_BLIND); // SB

        player.receiveCard(new Card(1, Suit.HEARTS)); // Ah
        player.receiveCard(new Card(13, Suit.HEARTS)); // Kh

        //bot.notifyPlayerAction(PlayerRole.DEALER, "raise", 2.5);
        //bot.notifyPlayerAction(PlayerRole.SMALL_BLIND, "raise", 12);
        //bot.notifyPlayerAction(PlayerRole.BIG_BLIND, "all-in", 100);
        //bot.notifyPlayerAction(PlayerRole.DEALER, "fold", 0);

        System.out.println("SCENARIO 3: " + player.makePlay(1, 1, 100));
        player.notifyRoundEnded();


        // =========================
        // ESCENARIO 4 (RAISE)
        // =========================
        player.receiveRole(PlayerRole.SMALL_BLIND); // SB

        player.receiveCard(new Card(11, Suit.SPADES)); // Js
        player.receiveCard(new Card(10, Suit.SPADES)); // Ts

        //bot.notifyPlayerAction(PlayerRole.DEALER, "raise", 2.5);

        System.out.println("SCENARIO 4: " + player.makePlay(1, 1, 100));
        player.notifyRoundEnded();
        
    }

}