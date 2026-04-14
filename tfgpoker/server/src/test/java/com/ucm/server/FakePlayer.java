package com.ucm.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerPlayer;


public class FakePlayer extends Player {


    public List<String> commands;


    public FakePlayer(final int ID, int offBet, int onBet) {
        super(ID, String.format("FakePlayer%d", ID), offBet);

        _onBetMoney = onBet;
        commands = new ArrayList<>();
    }


    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) { 
        String command = commands.removeFirst();
        return command; 
    }


    @Override
    public void notifySmallBlindBet(final int amount) {}

    @Override
    public void notifyBigBlindBet(final int amount) {}

    @Override
    public void notifyTurnWait() {}

    @Override
    public void notifyTurnPlay() {}

    @Override
    public void notifyRoundEnded() {}

    @Override
    public void notifyHandEnded() {}

    @Override
    public void notifyGameEnded() {}

    @Override
    public void notifyGameKeeps() {}

    @Override
    public void notifyGameWinner() {}

    @Override
    public void notifyGameLoser() {}

    @Override
    public void notifyHandEndsByFolds() {}

    @Override
    public void notifyPlayerRole(PlayerRole role) {}

    @Override
    public void notifyPlayerCard(Card c) {}

    @Override
    public void notifyTableCard(Card c) {}

    @Override
    public void notifyMoneyAmount(int amount) {}

    @Override
    public void notifyOtherPlayerAction(IPokerPlayer p) {}


    @Override
    public void notifyPlayerState(IPokerPlayer player, boolean last) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerState'");
    }


    @Override
    public void notifyTotalPot(int total) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTotalPot'");
    }
    
    public void notifyEquity(double equity) {
      
    }
    
}