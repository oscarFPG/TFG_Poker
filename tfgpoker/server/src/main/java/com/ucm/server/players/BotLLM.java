package com.ucm.server.players;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.gameobjects.PlayerRole;

public class BotLLM extends Player {

    
    public BotLLM(int id, String name, int money) {
        super(id, name, money);
    }


    @Override
    public void actionSmallBlindBet(int sb) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionSmallBlindBet'");
    }

    @Override
    public void actionBigBlindBet(int bb) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionBigBlindBet'");
    }

    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionMakePlay'");
    }

    @Override
    public void receiveRole(PlayerRole r) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveRole'");
    }

    @Override
    public void receiveCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveCard'");
    }

    @Override
    public void receiveTableCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveTableCard'");
    }

    @Override
    public void receiveNewMoney(int money) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveNewMoney'");
    }

    @Override
    public void receivePriceMoney(int money) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receivePriceMoney'");
    }

    @Override
    public void notifySmallBlindBet(final int amount) {

    }

    @Override
    public void notifyBigBlindBet(final int amount) {
        
    }

    @Override
    public void notifyTurnWait() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTurnWait'");
    }

    @Override
    public void notifyTurnPlay() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTurnPlay'");
    }

    @Override
    public void notifyRoundEnded() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyRoundEnded'");
    }

    @Override
    public void notifyHandEnded() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandEnded'");
    }

    @Override
    public void notifyGameEnded() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameEnded'");
    }

    @Override
    public void notifyGameKeeps() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameKeeps'");
    }

    @Override
    public void notifyHandWinner() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandWinner'");
    }

    @Override
    public void notifyHandLoser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandLoser'");
    }

    @Override
    public void notifyGameWinner() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameWinner'");
    }

    @Override
    public void notifyGameLoser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameLoser'");
    }

    @Override
    public void notifyHandEndsByFolds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandEndsByFolds'");
    }
    
}
