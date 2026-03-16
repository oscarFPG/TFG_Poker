package com.ucm.server;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerPlayer;


public class FakePlayer implements IPokerPlayer {

    public int offBetMoney;
    public int onBetMoney;
    public boolean isFolded;

    public FakePlayer(int offBet, int onBet){
        offBetMoney = offBet;
        onBetMoney = onBet;
        isFolded = false;
    }

    @Override
    public boolean call(int amount) {
        
        int resto = amount - onBetMoney;
        
        offBetMoney -= resto;
        onBetMoney += resto;
        return true;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public boolean fold() {
        isFolded = true;
        return true;
    }

    @Override
    public boolean raise(int amount) {
        
        if(amount > offBetMoney + onBetMoney){
            call(amount);
            return true;
        }

        int resto = amount - onBetMoney;
        
        offBetMoney += resto;
        onBetMoney -= resto;
        return true;
    }

    @Override
    public boolean allIn() {
        onBetMoney += offBetMoney;
        offBetMoney = 0;
        return true;
    }

    @Override
    public String getPlayerName() {
        return "FakePlayer";
    }

    @Override
    public int getMoneyOnBet() {
        return onBetMoney;
    }

    @Override
    public int getMoneyOffBet() {
        return offBetMoney;
    }

    @Override
    public int getCardsCounter() {
        return 2;
    }

    @Override
    public boolean hasFolded() {
        return isFolded;
    }

    @Override
    public boolean isWinner() {
        return false;
    }

    @Override
    public Card[] getPlayerCards() {
        return null;
    }

    @Override
    public void receiveRole(PlayerRole r) {}

    @Override
    public void receiveCard(Card c) {}

    @Override
    public void receiveTableCard(Card c) {}

    @Override
    public void receiveNewMoney(int money) {}

    @Override
    public void retrieveCards() {}

    @Override
    public void foldPlayer() {}

    @Override
    public void unfoldPlayer() {}

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
    public void notifyHandWinner() {}

    @Override
    public void notifyHandLoser() {}

    @Override
    public void notifyGameWinner() {}

    @Override
    public void notifyGameLoser() {}

    @Override
    public void actionSmallBlindBet(int sb) {}

    @Override
    public void actionBigBlindBet(int bb) {}

    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) { return ""; }

}