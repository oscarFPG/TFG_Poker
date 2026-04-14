package com.ucm.common;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameInfo {

    public GameConfig gameConfig;
    public List<ClientStruct> players;
    public List<BotStruct> bots;

    public GameInfo(GameConfig config) {
        gameConfig = config;
        players = new ArrayList<>();
        bots = new ArrayList<>();
    }

    public void addPlayer(final String name, final Socket socket) {
        players.add( new ClientStruct(name, socket) );
    }

    public void addBots (final int id) {
        bots.add(new BotStruct(id));
    }

}