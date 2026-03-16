package com.ucm.server.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.interfaces.IPokerActions;
import com.ucm.server.interfaces.IPokerPlayer;

public class AllInCommandTest {
 
    private static final int INITIAL_MONEY = 1000;

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
            
            offBetMoney += resto;
            onBetMoney -= resto;
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
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getCardsCounter'");
        }

        @Override
        public boolean hasFolded() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'hasFolded'");
        }

        @Override
        public boolean isWinner() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'isWinner'");
        }

        @Override
        public Card[] getPlayerCards() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getPlayerCards'");
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
        public void retrieveCards() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'retrieveCards'");
        }

        @Override
        public void foldPlayer() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'foldPlayer'");
        }

        @Override
        public void unfoldPlayer() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'unfoldPlayer'");
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
        public int actionMakePlay(int sb, int bb, int maxBet) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'actionMakePlay'");
        }

    }
    
    @Test
    public void allInFromZero(){

        IPokerActions player = new FakePlayer(0, INITIAL_MONEY);
    
        AllInCommand command = new AllInCommand(player, player., 0);
    }


}