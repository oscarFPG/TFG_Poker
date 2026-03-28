package com.ucm.server.control;

import java.util.ArrayList;
import java.util.List;

import com.ucm.server.gameobjects.Bot;
import com.ucm.server.players.ChatgptLLM;


public class BotServiceProvider {
    
    
    private static final List<Bot> BOT_LIST = List.of(
        new ChatgptLLM()
    );


    private BotServiceProvider() {}


    public static List<String> getBotListInformation() {

        List<String> list = new ArrayList<>();
        for(Bot b : BOT_LIST)
            list.add( b.getFullDescription() );

        return list;
    }


    public static Bot createChatgptBot() {
        return new ChatgptLLM();
    }

}