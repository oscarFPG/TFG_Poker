package com.ucm.server.middleclasses;

import com.ucm.server.gameobjects.Card;
import com.ucm.server.interfaces.IPokerPlayer;

/**
 * Represents the information associated with a player's hand.
 *
 * @param cards  the two cards currently held by the player
 * @param player the player who owns the hand
 */
public record HandInfo(Card[] cards, IPokerPlayer player) {}