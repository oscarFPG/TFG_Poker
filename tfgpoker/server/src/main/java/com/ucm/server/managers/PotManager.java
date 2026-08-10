package com.ucm.server.managers;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.ucm.server.evaluator.Evaluator;
import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.PotDistribution;


/**
 * The PotManager class is responsible for managing the pots in a poker game. 
 * It keeps track of each player's contributions to the pot, their ranks, and calculates the prize distribution based on the players' ranks and contributions.
 * The class provides methods to add players, update their pots, calculate individual prizes, and determine the overall prize distribution among players.
 */
public class PotManager {
    
    /**
     * Represents information about a player, including their ID, rank, and the amount they have contributed to the pot.
     */
    private class PlayerInfo {

        /**
         * The unique identifier for the player.
         */
        public int playerID;

        /**
         * The rank of the player.
         */
        public short playerRank;

        /**
         * The amount of money the player has contributed to the pot.
         */
        public int playerPot;

        
        /**
         * Creates a new PlayerInfo instance with the specified ID, rank, and pot amount.
         * @param id The unique identifier for the player.
         * @param rank The rank of the player.
         * @param pot The amount of money the player has contributed to the pot.
         */
        public PlayerInfo(final int id, final short rank, final int pot) {
            playerID = id;
            playerRank = rank;
            playerPot = pot;
        }


        /**
         * Increases the player's pot by the specified amount.
         * @param amount The amount to increase the player's pot by.
         */
        public void increasePot(final int amount) {
            playerPot += amount;
        }

        /**
         * Decreases the player's pot by the specified amount.
         * @param amount The amount to decrease the player's pot by.
         */
        public void decreasePot(final int amount) {
            playerPot -= amount;
        }

        /**
         * Resets the player's rank and pot to their initial values.
         */
        public void restart() {
            playerRank = 0;
            playerPot = 0;
        }

    }

    /**
     * Represents a pot in the poker game, including the total amount in the pot and the list of players who are eligible to win it.
     * Each pot is associated with a list of players who have contributed to it
     */
    private class Pot {

        /**
         * The total amount of money in the pot.
         */
        public int totalPot;

        /**
         * The list of players who are eligible to win the pot.
         */
        public List<PlayerInfo> possibleWinners;

        /**
         * Creates a new Pot instance with the specified list of eligible players and total amount.
         * @param playerList The list of players who are eligible to win the pot.
         * @param total The total amount of money in the pot.
         */
        public Pot(List<PlayerInfo> playerList, final int total) {
            possibleWinners = playerList;
            totalPot = total;
        }

    }


    /**
     * The list of PlayerInfo objects representing the players and their contributions to the pot.
     */
    private List<PlayerInfo> _pots;


    /**
     * Creates a new PotManager instance with the specified number of players.
     * @param n The number of players in the poker game.
     */
    public PotManager(final int n) {
        _pots = new ArrayList<>(n);
    }


    /**
     * Adds a new player to the pot manager with the specified ID. The player's initial rank is set to 0, and their initial pot contribution is set to 0.
     * @param id The unique identifier for the player to be added.
     */
    public void addPlayer(final int id) {
        _pots.add( new PlayerInfo(id, (short)0, 0) );
    }

    /**
     * Updates the pot contribution and rank of a player with the specified ID. If the player has folded, their rank is set to the maximum value (Short.MAX_VALUE).
     * @param id The unique identifier for the player whose pot contribution and rank are to be updated.
     * @param amount The amount to be added to the player's pot contribution.
     * @param hasFolded A boolean indicating whether the player has folded. If true, the player's rank is set to Short.MAX_VALUE.
     */
    public void updatePlayerPot(final int id, final int amount, final boolean hasFolded) {
        
        PlayerInfo p = _pots.stream().
                        filter( player -> player.playerID == id).
                        findFirst().
                        orElse(null);

        p.increasePot(amount);
        if(hasFolded)
            p.playerRank = Short.MAX_VALUE;
    }

    /**
     * Calculates the prize distribution for a specific player based on their contributions to the pot and their rank.
     * @param id The unique identifier for the player whose prize distribution is to be calculated.
     * @return A PotDistribution object representing the prize distribution for the specified player, including their ID, total prize amount, and rank name.
     * @throws NoSuchElementException if the player with the specified ID is not found in. This has to be preventedby the game logic, so it should never happen.
     */
    public PotDistribution calculatePrizeForPlayer(final int id) {

        PlayerInfo winner = _pots.stream()
                            .filter( p -> p.playerID == id )
                            .findFirst()
                            .get();

        int total = 0;
        for(PlayerInfo p : _pots) {
            total += p.playerPot;
        }

        return new PotDistribution(winner.playerID, total, "none");
    }

    /**
     * Calculates the prize distribution among all players based on their contributions to the pot and their ranks.
     * @param players A list of PlayerEvaluation objects representing the players and their evaluations, including their IDs and ranks.
     * @return A list of PotDistribution objects representing the prize distribution among all players, including their IDs, total prize amounts, and rank names.
     */
    public List<PotDistribution> calculatePrizeDistribution(final List<PlayerEvaluation> players) {

        _pots.sort( Comparator.comparingInt(p -> p.playerPot) );   // Important: start from the player with the smaller pot

        // Associate players with their rank
        for(PlayerEvaluation p : players) {
            PlayerInfo player = _pots.stream()
                                    .filter( pl -> pl.playerID == p.playerID() )
                                    .findFirst()
                                    .get();
            player.playerRank = p.playerRank();
        }

        // Divide in pots
        List<PlayerInfo> potsCopy = new ArrayList<>(_pots);
        List<Pot> pots = new ArrayList<>();
        while(!potsCopy.isEmpty()){

            Pot pot = createPot(potsCopy);
            pots.add( pot );

            potsCopy.removeIf( p -> p.playerPot == 0 );
        }

        return createDistribution(pots);
    }

    /**
     * Creates a list of PotDistribution objects representing the prize distribution for each pot in the provided list of pots.
     * @param pots A list of Pot objects representing the pots in the poker game, each containing the total amount and the list of eligible players.
     * @return A list of PotDistribution objects representing the prize distribution for each pot, including the player IDs, total prize amounts, and rank names.
     */
    private List<PotDistribution> createDistribution(List<Pot> pots) {
        
        List<PotDistribution> distribution = new ArrayList<>();
        for(Pot pot : pots) {

            List<PotDistribution> dist = resolvePot(pot);
            for(PotDistribution aux : dist)
                distribution.add(aux);
        }

        return distribution;
    }

    /**
     * Resolves the prize distribution for a specific pot based on the ranks of the eligible players. 
     * It identifies the players with the best rank and distributes the total pot amount equally among them.
     * @param pot The pot for which to resolve the prize distribution.
     * @return A list of PotDistribution objects representing the prize distribution for the specified pot.
     */
    private List<PotDistribution> resolvePot(final Pot pot){

        List<PotDistribution> dist = new ArrayList<>();
        List<PlayerInfo> players = pot.possibleWinners;
        final int totalPot = pot.totalPot;

        players.sort(
            Comparator.comparingInt(
                p -> p.playerRank
            ) 
        );

        final short bestRank = players.getFirst().playerRank;
        final String rankName = Evaluator.getRankName(bestRank);
        List<PlayerInfo> winners = players
                                    .stream()
                                    .filter( p -> p.playerRank == bestRank )
                                    .collect(Collectors.toList());

        final int prizePerPlayer = totalPot / winners.size();
        for(PlayerInfo p : winners) {
            dist.add( new PotDistribution(p.playerID, prizePerPlayer, rankName) );
        }

        return dist;
    }

    /**
     * Creates a new Pot instance based on the contributions of the provided list of players.
     * @param playerPots A list of PlayerInfo objects representing the players and their contributions to the pot.
     * @return A new Pot instance containing the total amount in the pot and the list of eligible players.
     */
    private Pot createPot(List<PlayerInfo> playerPots) {

        List<PlayerInfo> potentialWinners = new ArrayList<>();
        final int potPerPlayer = playerPots.getFirst().playerPot;
        int total = 0;
        for(PlayerInfo pl : playerPots){
            pl.decreasePot(potPerPlayer);
            total += potPerPlayer;
            potentialWinners.add(pl);
        }
        
        return new Pot(potentialWinners, total);
    }

    /**
     * Restarts the pots for all players by resetting their ranks and pot contributions to their initial values.
     * This method is typically called at the beginning of a new round or game.
     */
    public void restartPots() {
        _pots.forEach( player -> player.restart() );
    }    

}