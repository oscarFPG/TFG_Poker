package com.ucm.server.interfaces;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

public interface IPokerPlayer extends IPokerActions {
    
    public String getPlayerName();
    public int getMoneyOnBet();
    public int getMoneyOffBet();
    public int getCardsCounter();
    public boolean hasFolded();
    public boolean isWinner();
    public Card[] getPlayerCards();

    public void receiveRole(PlayerRole r);
    public void receiveCard(Card c);
    public void receiveTableCard(Card c);
    public void receiveNewMoney(int money);
    public void retrieveCards();
    public void foldPlayer();
    public void unfoldPlayer();

    public void notifyTurnWait();
    public void notifyTurnPlay();
    public void notifyRoundEnded();
    public void notifyHandEnded();
    public void notifyGameEnded();
    public void notifyHandWinner();
    public void notifyHandLoser();
    public void notifyGameWinner();
    public void notifyGameLoser();

    public void actionSmallBlindBet(final int sb);
    public void actionBigBlindBet(final int bb);
    public int actionMakePlay(final int sb, final int bb, final int maxBet);

}