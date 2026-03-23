package com.ucm.server.interfaces;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

public interface IPokerPlayer extends IPokerActions {

    public void receiveRole(PlayerRole r);
    public void receiveCard(Card c);
    public void receiveTableCard(Card c);
    public void receiveNewMoney(int money);
    public void receivePriceMoney(int amount);
    public void retrieveCards();
    public void setIsWinner(boolean state);
    public void setIsEliminated(boolean state);

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

    public void actionSmallBlindBet(final int sb);
    public void actionBigBlindBet(final int bb);
    public String actionMakePlay(final int sb, final int bb, final int maxBet);

}