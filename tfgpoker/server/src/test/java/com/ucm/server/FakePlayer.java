package com.ucm.server;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerPlayer;


public class FakePlayer implements IPokerPlayer {

    public int offBetMoney;
    public int onBetMoney;
    public PlayerRole role;
    public boolean isFolded;
    public boolean isWinner;


    public FakePlayer(int offBet, int onBet){
        offBetMoney = offBet;
        onBetMoney = onBet;
        isFolded = false;
        isWinner = false;
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
        
        onBetMoney += resto;
        offBetMoney -= resto;
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
    public boolean isFolded() {
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
    public void receiveRole(PlayerRole r) {
        role = r;
    }

    @Override
    public void receiveCard(Card c) {}

    @Override
    public void receiveTableCard(Card c) {}

    @Override
    public void receiveNewMoney(int money) {}

    @Override
    public void receivePriceMoney(int amount) {
        offBetMoney += amount;   
    }

    @Override
    public void retrieveCards() {}

    @Override
    public int placeOnBetMoney() {

        int money = onBetMoney;
        onBetMoney = 0;
        return money;
    }

    @Override
    public void foldPlayer() {}

    @Override
    public void unfoldPlayer() {}

    @Override
    public void setIsWinner(boolean state) {
        isWinner = state;
    }

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
    public void notifyHandWinner() {}

    @Override
    public void notifyHandLoser() {}

    @Override
    public void notifyGameWinner() {}

    @Override
    public void notifyGameLoser() {}

    @Override
    public void actionSmallBlindBet(int sb) {
        onBetMoney += sb;
        offBetMoney -= sb;
    }

    @Override
    public void actionBigBlindBet(int bb) {
        onBetMoney += bb;
        offBetMoney -= bb;
    }

    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) { return null; }

    @Override
    public void notifyHandEndsByFolds() {}

}