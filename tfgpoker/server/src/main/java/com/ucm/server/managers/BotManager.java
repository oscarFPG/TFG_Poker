package com.ucm.server.managers;

import java.util.Arrays;
import java.util.List;

import com.ucm.server.gameobjects.Bot;
import com.ucm.server.players.GeminiLLM;


public class BotManager {
    
    private static final List<Bot>AVAILABLE_BOTS  = Arrays.asList(
        new GeminiLLM()
    );


    public static Bot createBot(int ID) {

        for(Bot bot : AVAILABLE_BOTS) {
            if(bot.getIdBot() == ID) {
                return bot;
            }
        }

        return null;
    }

}
