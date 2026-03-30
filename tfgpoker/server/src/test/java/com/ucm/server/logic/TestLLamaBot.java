package com.ucm.server.logic;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.gameobjects.Suit;
import com.ucm.server.players.LlamaPokerLLM;

public class TestLLamaBot {

    public static void main(String[] args) {

        LlamaPokerLLM bot = new LlamaPokerLLM(1, 100);

        
        bot.notifyPlayerRole(PlayerRole.BIG_BLIND);
        bot.notifyPlayerCard(new Card(14, Suit.HEARTS));
        bot.notifyPlayerCard(new Card(13, Suit.HEARTS));

        bot.notifyTableCard(new Card(12, Suit.DIAMONDS));
        bot.notifyTableCard(new Card(11, Suit.HEARTS));
        bot.notifyTableCard(new Card(2, Suit.CLUBS));

        bot.notifySmallBlindBet(1);
        bot.notifyBigBlindBet(2);

       
        bot.notifyPlayerAction(PlayerRole.UNDER_THE_GUN, "raise", 3);
        bot.notifyPlayerAction(PlayerRole.SMALL_BLIND, "call", 3);

       
        String action = bot.actionMakePlay(1, 2, 3);

        System.out.println("BOT ACTION: " + action);
    }
}