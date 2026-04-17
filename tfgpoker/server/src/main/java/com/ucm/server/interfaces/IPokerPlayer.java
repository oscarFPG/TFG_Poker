package com.ucm.server.interfaces;

import java.io.IOException;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;


public interface IPokerPlayer extends IPokerActions, IPokerNotification {

    // Getters to retrieve player info
    public int getPlayerId();
    public String getPlayerName();
    public Card[] getPlayerCards();
    public int getCardsCounter();
    public boolean isFolded();
    public boolean isWinner();
    public boolean isAllIn();
    public boolean isEliminated();
    public PlayerRole getRole();
    public String getLastCommand();

    // Setters to modify player state
    public int placeOnBetMoney();
    public void unfoldPlayer();
    public void setIsWinner(boolean state);
    public void setIsEliminated(boolean state);
    public void setAllIn(boolean state);

    // Methods that modify the player state in the game
    public void receiveRole(PlayerRole r);
    public void receiveCard(Card c);
    public void receivePriceMoney(int amount);
    public void retrieveCards();

    // Actions available during players turn as small blind, big blind and turn player
    public void actionSmallBlindBet(final int sb);
    public void actionBigBlindBet(final int bb);

}