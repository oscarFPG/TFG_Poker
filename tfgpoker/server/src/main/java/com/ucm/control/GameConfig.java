package com.ucm.control;

/**
 * this class should define all the hosts configuraations for the game.
 * Attributes such as:
 *      - Maximum number of players
 *      - Initial money for every player
 *      - etc...
 */
public record GameConfig(int maximumNumPlayers, int initialMoney) {
    
}
