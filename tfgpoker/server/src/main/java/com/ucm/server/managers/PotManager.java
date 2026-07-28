package com.ucm.server.managers;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.ucm.server.middleclasses.PlayerEvaluation;
import com.ucm.server.middleclasses.PotDistribution;


public class PotManager {
    
    private class PlayerInfo {

        public int playerID;
        public short playerRank;
        public int playerPot;

        
        public PlayerInfo(final int id, final short rank, final int pot) {
            playerID = id;
            playerRank = rank;
            playerPot = pot;
        }


        public void increasePot(final int amount) {
            playerPot += amount;
        }

        public void decreasePot(final int amount) {
            playerPot -= amount;
        }

        public void restart(){
            playerRank = 0;
            playerPot = 0;
        }

    }

    private class Pot {

        public int totalPot;
        public List<PlayerInfo> possibleWinners;

        public Pot(List<PlayerInfo> playerList, final int total) {
            possibleWinners = playerList;
            totalPot = total;
        }

    }


    private List<PlayerInfo> _pots;


    public PotManager(final int n) {
        _pots = new ArrayList<>(n);
    }


    public void addPlayer(final int id) {
        _pots.add( new PlayerInfo(id, (short)0, 0) );
    }

    public void updatePlayerPot(final int id, final int amount, final boolean hasFolded) {
        
        PlayerInfo p = _pots.stream().
                        filter( player -> player.playerID == id).
                        findFirst().
                        orElse(null);

        p.increasePot(amount);
        if(hasFolded)
            p.playerRank = Short.MAX_VALUE;
    }

    public PotDistribution calculatePrizeForPlayer(final int id) {

        PlayerInfo winner = _pots.stream()
                            .filter( p -> p.playerID == id )
                            .findFirst()
                            .get();

        int total = 0;
        for(PlayerInfo p : _pots){
            total += p.playerPot;
        }

        return new PotDistribution(winner.playerID, total);
    }

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

    private List<PotDistribution> createDistribution(List<Pot> pots) {
        
        List<PotDistribution> distribution = new ArrayList<>();

        for(Pot pot : pots){

            List<PotDistribution> dist = resolvePot(pot);
            for(PotDistribution aux : dist)
                distribution.add(aux);
        }

        return distribution;
    }

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
        List<PlayerInfo> winners = players
                                    .stream()
                                    .filter( p -> p.playerRank == bestRank )
                                    .collect(Collectors.toList());

        final int prizePerPlayer = totalPot / winners.size();
        for(PlayerInfo p : winners) {
            dist.add( new PotDistribution(p.playerID, prizePerPlayer) );
        }

        return dist;
    }

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

    public void restartPots() {
        _pots.forEach( player -> player.restart() );
    }    

}