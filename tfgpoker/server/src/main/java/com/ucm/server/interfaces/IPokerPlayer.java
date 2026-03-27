package com.ucm.server.interfaces;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

public interface IPokerPlayer extends IPokerActions {

    public int getPlayerId();
    public String getPlayerName();
    public Card[] getPlayerCards();
    public int getCardsCounter();

    public void receiveRole(PlayerRole r);
    public void receiveCard(Card c);
    public void receiveTableCard(Card c);
    public void receiveNewMoney(int money);
    public void receivePriceMoney(int amount);

    public void notifySmallBlindBet(final int amount);
    public void notifyBigBlindBet(final int amount);
    public void notifyTurnWait();
    public void notifyTurnPlay();
    public void notifyRoundEnded();
    public void notifyHandEnded();
    public void notifyGameEnded();
    public void notifyGameKeeps();
    public void notifyHandWinner();
    public void notifyHandLoser();
    public void notifyGameWinner();
    public void notifyGameLoser();
    public void notifyHandEndsByFolds();

    public void retrieveCards();
    public void actionSmallBlindBet(final int sb);
    public void actionBigBlindBet(final int bb);
    public String actionMakePlay(final int sb, final int bb, final int maxBet);

}