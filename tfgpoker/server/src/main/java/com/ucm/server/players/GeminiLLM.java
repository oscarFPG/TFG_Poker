package com.ucm.server.players;

import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

public class GeminiLLM extends BotLLM {


    public GeminiLLM(int id, int money, String apiKey) {
        super(id, "GeminiLLM", money, apiKey);
    }

    @Override
    public void notifyPlayerRole(PlayerRole role) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerRole'");
    }

    @Override
    public void notifyPlayerCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerCard'");
    }

    @Override
    public void notifyTableCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTableCard'");
    }

    @Override
    public void notifyMoneyAmount(int amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyMoneyAmount'");
    }

    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionMakePlay'");
    }

    @Override
    public void notifySmallBlindBet(int amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifySmallBlindBet'");
    }

    @Override
    public void notifyBigBlindBet(int amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyBigBlindBet'");
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

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
    }

    @Override
    public void notifyPlayerAction(PlayerRole role, String action, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerAction'");
    }
    
}
