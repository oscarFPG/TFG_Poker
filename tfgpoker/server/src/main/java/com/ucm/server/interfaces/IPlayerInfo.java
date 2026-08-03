package com.ucm.server.interfaces;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;

/**
 * Interface to ONLY access player state(e.g: Name, On bet money, Off bet money, Player role, etc...)
 */
public interface IPlayerInfo {

    public int getPlayerId();
    public String getPlayerName();
    public int getMoneyOnBet();
    public int getMoneyOffBet();
    public Card[] getPlayerCards();
    public int getCardsCounter();
    public boolean isFolded();
    public boolean isWinner();
    public boolean isAllIn();
    public boolean isEliminated();
    public PlayerRole getRole();
    public String getLastCommand();
    public String getLastRankName();

}