package com.ucm.server.middleclasses;

/**
 * Represents the distribution of the pot among players based on their contributions and ranks.
 * 
 * @param playerID the unique identifier for the player receiving the pot distribution
 * @param potPrize the total amount of the pot awarded to the player
 * @param rankName the name of the rank associated with the player's hand
 */
public record PotDistribution(int playerID, int potPrize, String rankName) {}