package com.ucm.server;

import java.util.ArrayList;
import java.util.List;

import com.ucm.server.gameobjects.Player;


public class FakePlayer extends Player {

    public List<String> commands;

    
    public FakePlayer(final int ID, int offBet, int onBet) {
        super(ID, String.format("FakePlayer%d", ID), offBet, null);

        increaseOnBetMoney(onBet);
        commands = new ArrayList<>();
    }

    public void addCommand(String c) {
        commands.add(c);
    }

    @Override
    public String makePlay(int sb, int bb, int maxBet) {
        return commands.removeFirst(); 
    }

}