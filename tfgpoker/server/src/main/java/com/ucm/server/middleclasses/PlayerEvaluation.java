package com.ucm.server.middleclasses;

/**
 * This record represents the evaluation of a player in a poker game, including their unique identifier and rank.
 * 
 * @param playerID the unique identifier for the player
 * @param playerRank the rank of the player in the game, represented as a short value
 */
public record PlayerEvaluation(int playerID, short playerRank) {}