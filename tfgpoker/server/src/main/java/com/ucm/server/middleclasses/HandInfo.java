package com.ucm.server.middleclasses;

import com.ucm.common.gameobjects.Card;

/**
 * Represents the information associated with a player's hand.
 *
 * @param playerID the unique identifier for the player
 * @param cards  the two cards currently held by the player
 */
public record HandInfo(int playerID, Card[] cards) {}