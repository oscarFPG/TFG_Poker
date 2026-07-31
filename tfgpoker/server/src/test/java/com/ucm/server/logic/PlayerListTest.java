package com.ucm.server.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.ucm.common.exceptions.CancelGameException;
import com.ucm.common.exceptions.OnlyOnePlayerLeftException;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.FakePlayer;
import com.ucm.server.middleclasses.PlayerEvaluation;


public class PlayerListTest {


    static Stream<Arguments> playerRoleProvider() {
        return Stream.of(
            Arguments.of(2, PlayerRole.getRolesDistribution(2) ),
            Arguments.of(3, PlayerRole.getRolesDistribution(3) ),
            Arguments.of(4, PlayerRole.getRolesDistribution(4) ),
            Arguments.of(5, PlayerRole.getRolesDistribution(5) ),
            Arguments.of(6, PlayerRole.getRolesDistribution(6) ),
            Arguments.of(7, PlayerRole.getRolesDistribution(7) ),
            Arguments.of(8, PlayerRole.getRolesDistribution(8) ),
            Arguments.of(9, PlayerRole.getRolesDistribution(9) )
        );
    }


    private List<FakePlayer> getPlayerSubsetWithSize(final int size) {
        
        final int INITIAL_MONEY = 1000;

        List<FakePlayer> players = new ArrayList<>();
        for(int i = 0; i < size; i++)
            players.add( new FakePlayer(1, INITIAL_MONEY, 0) );

        return players;
    }

    private List<PlayerRole> getPlayerRoleDistributionWithSize(final int size) {
        return PlayerRole.getRolesDistribution(size);
    }

    @ParameterizedTest
    @MethodSource("playerRoleProvider")
    void initialRoleAssigment(int numPlayers, List<PlayerRole> expectedRoles) throws CancelGameException {

        final int INITIAL_MONEY = 1000;
        PlayerList playerList = new PlayerList(numPlayers);
        FakePlayer[] players = new FakePlayer[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new FakePlayer(i, INITIAL_MONEY, 0);
            playerList.addPlayer( players[i] );
        }

        playerList.assignRolesToAllPlayers();
        for (int i = 0; i < expectedRoles.size(); i++) {
            assertEquals(expectedRoles.get(i), players[i].getRole());
        }
    }

    @Test
    void assignNewRolesOnPassTurnWithTwoPlayers() throws CancelGameException {

        final int INITIAL_MONEY = 1000;
        FakePlayer player1 = new FakePlayer(0, INITIAL_MONEY, 0);
        FakePlayer player2 = new FakePlayer(1, INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(2);
        
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);


        playerList.assignRolesToAllPlayers();
        assertEquals(PlayerRole.SMALL_BLIND, player1.getRole());
        assertEquals(PlayerRole.BIG_BLIND, player2.getRole());

        playerList.passTurn();
        playerList.assignRolesToAllPlayers();
        assertEquals(PlayerRole.BIG_BLIND, player1.getRole());
        assertEquals(PlayerRole.SMALL_BLIND, player2.getRole());

        playerList.passTurn();
        playerList.assignRolesToAllPlayers();
        assertEquals(PlayerRole.SMALL_BLIND, player1.getRole());
        assertEquals(PlayerRole.BIG_BLIND, player2.getRole());
    }


    @ParameterizedTest(name = "Roles assignment on turns in {0} players")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9})
    void roleAssigmentsOnPassTurn(int numPlayers) throws CancelGameException {

        List<FakePlayer> players = getPlayerSubsetWithSize(numPlayers);
        List<PlayerRole> roles = getPlayerRoleDistributionWithSize(numPlayers);
        PlayerList playerList = new PlayerList(numPlayers);
        for(FakePlayer p : players)
            playerList.addPlayer(p);


        // Simulate turns and check new roles assignments
        playerList.assignRolesToAllPlayers();
        for(int times = 0; times < numPlayers; times++){    // 'numPlayers' different roles distributions

            int playerIndex = times;    // Moving pivot player -> first is always DEALER
            int roleIndex = 0;          // Constant pivot role
            for(int i = 0; i < numPlayers; i++){
                FakePlayer player = players.get(playerIndex);
                PlayerRole role = roles.get(roleIndex);
                assertEquals(role, player.getRole());

                playerIndex = (playerIndex + 1) % numPlayers;   // Treat as an circular array
                roleIndex = (roleIndex + 1) % numPlayers;       // Treat as an circular array
            }
            playerList.passTurn();
            playerList.assignRolesToAllPlayers();

        }

    }


    @Test
    void call() throws CancelGameException {

        final int INITIAL_MONEY = 1000;
        final int SB = 1;
        final int BB = SB * 2;
        
        FakePlayer p1 = new FakePlayer(0, INITIAL_MONEY, 0);
        FakePlayer p2 = new FakePlayer(1, INITIAL_MONEY, 0);
        FakePlayer p3 = new FakePlayer(2, INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(3);

        try {

            playerList.addPlayer( p1 );
            playerList.addPlayer( p2 ); // Small blind
            playerList.addPlayer( p3 ); // Big blind

            playerList.assignRolesToAllPlayers();

            p1.commands = new ArrayList<>( List.of("fold") );
            p2.commands = new ArrayList<>( List.of("fold") );
            playerList.playHand(SB, BB, true);      // 2 + 2 + 2 = 6$ total - 2$ = 4$ beneficio

            assertEquals(true, false, "This test should not reach this code");
        }
        catch(OnlyOnePlayerLeftException e) {

            playerList.calculatePrizeForPlayerLeft();

            assertEquals(INITIAL_MONEY, p1.getMoneyOffBet());
            assertEquals(INITIAL_MONEY - 1, p2.getMoneyOffBet());
            assertEquals(INITIAL_MONEY + 1, p3.getMoneyOffBet());
        }
    }

    @Test
    void raise() throws CancelGameException {
        
        final int INITIAL_MONEY = 1000;
        final int SB = 1;
        final int BB = SB * 2;
        
        FakePlayer p1 = new FakePlayer(0, INITIAL_MONEY, 0);
        FakePlayer p2 = new FakePlayer(1, INITIAL_MONEY, 0);
        FakePlayer p3 = new FakePlayer(2, INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(3);
 
        try {

            playerList.addPlayer( p1 );
            playerList.addPlayer( p2 );
            playerList.addPlayer( p3 );

            playerList.assignRolesToAllPlayers();

            p1.commands = new ArrayList<>( List.of("call") );
            p2.commands = new ArrayList<>( List.of("call") );
            p3.commands = new ArrayList<>( List.of("call") );
            playerList.playHand(SB, BB, true);      // 2 + 2 + 2 = 6$ total - 2$ = 4$ beneficio

            p2.commands = new ArrayList<>( List.of("check", "raise 200", "call") );
            p3.commands = new ArrayList<>( List.of("check", "raise 300") );
            p1.commands = new ArrayList<>( List.of("raise 100", "call") );
            playerList.playHand(SB, BB, false);     // 300 + 300 + 300 = 900$ total - 300$ = 600$ beneficio
            
            PlayerEvaluation p1Ev = new PlayerEvaluation(0, (short)20);
            PlayerEvaluation p2Ev = new PlayerEvaluation(1, (short)50);
            PlayerEvaluation p3Ev = new PlayerEvaluation(2, (short)100);

            playerList.calculatePrizeDistribution( new ArrayList<>( List.of(p1Ev, p2Ev, p3Ev) ) );

            assertEquals(INITIAL_MONEY + 604, p1.getMoneyOffBet(), "P1 does not have the corret money");
            assertEquals(INITIAL_MONEY - 300 - 2, p2.getMoneyOffBet(), "P2 does not have the corret money");
            assertEquals(INITIAL_MONEY - 300 - 2, p3.getMoneyOffBet(), "P3 does not have the corret money");

        }
        catch(OnlyOnePlayerLeftException e) {
            assertEquals(true, false, "This test should not reach this code");
        }
    }

    @Test
    void allin_raise() throws CancelGameException {

        final int INITIAL_MONEY = 1000;
        final int SB = 1;
        final int BB = SB * 2;
        
        FakePlayer p1 = new FakePlayer(0, 2 * INITIAL_MONEY, 0);
        FakePlayer p2 = new FakePlayer(1, INITIAL_MONEY, 0);
        FakePlayer p3 = new FakePlayer(2, INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(3);

        
        try {

            playerList.addPlayer( p1 );
            playerList.addPlayer( p2 );
            playerList.addPlayer( p3 );

            playerList.assignRolesToAllPlayers();

            p1.commands = new ArrayList<>( List.of("call") );
            p2.commands = new ArrayList<>( List.of("call") );
            p3.commands = new ArrayList<>( List.of("call") );
            playerList.playHand(SB, BB, true);      // 2 + 2 + 2 = 6$ total - 2$ = 4$ beneficio

            p2.commands = new ArrayList<>( List.of("check", "call", "all-in") );
            p3.commands = new ArrayList<>( List.of("check", "all-in") );
            p1.commands = new ArrayList<>( List.of("raise 500", "raise 1200") );
            playerList.playHand(SB, BB, false);
            
            PlayerEvaluation p1Ev = new PlayerEvaluation(0, (short)20);
            PlayerEvaluation p2Ev = new PlayerEvaluation(1, (short)50);
            PlayerEvaluation p3Ev = new PlayerEvaluation(2, (short)100);

            playerList.calculatePrizeDistribution( new ArrayList<>( List.of(p1Ev, p2Ev, p3Ev) ) );

            assertEquals(4 * INITIAL_MONEY, p1.getMoneyOffBet(), "P1 does not have the corret money");
            assertEquals(0, p2.getMoneyOffBet(), "P2 does not have the corret money");
            assertEquals(0, p3.getMoneyOffBet(), "P3 does not have the corret money");
        }
        catch(OnlyOnePlayerLeftException e) {
            assertEquals(true, false, "This test should not reach this code");
        }
    }

    @Test
    void allin1_allin2() throws CancelGameException {
        
        final int INITIAL_MONEY = 1000;
        final int SB = 1;
        final int BB = SB * 2;
        
        FakePlayer p1 = new FakePlayer(0, INITIAL_MONEY, 0);
        FakePlayer p2 = new FakePlayer(1, INITIAL_MONEY, 0);
        FakePlayer p3 = new FakePlayer(2, 2 * INITIAL_MONEY, 0);
        FakePlayer p4 = new FakePlayer(3, 2 * INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(4);

        try {

            playerList.addPlayer( p1 );
            playerList.addPlayer( p2 );
            playerList.addPlayer( p3 );
            playerList.addPlayer( p4 );

            playerList.assignRolesToAllPlayers();

            p2.commands = new ArrayList<>( List.of("all-in") );  // 1000
            p3.commands = new ArrayList<>( List.of("all-in") );  // 1000
            p4.commands = new ArrayList<>( List.of("all-in") );  // 2000
            p1.commands = new ArrayList<>( List.of("all-in") );  // 2000
            playerList.playHand(SB, BB, false);

            playerList.playHand(SB, BB, false); // No player should play here because all went all-in
            
            PlayerEvaluation p1Ev = new PlayerEvaluation(0, (short)20);     // Winner of the first pot between all players (1000, 1000, 1000, 1000)
            PlayerEvaluation p2Ev = new PlayerEvaluation(1, (short)50);
            PlayerEvaluation p3Ev = new PlayerEvaluation(2, (short)100);    // Winner of the second pot between him and p4 (1000, 1000)
            PlayerEvaluation p4Ev = new PlayerEvaluation(3, (short)200);

            playerList.calculatePrizeDistribution( new ArrayList<>( List.of(p1Ev, p2Ev, p3Ev, p4Ev) ) );

            assertEquals(4000, p1.getMoneyOffBet(), "P1 does not have the corret money");
            assertEquals(0, p2.getMoneyOffBet(), "P2 does not have the corret money");
            assertEquals(2000, p3.getMoneyOffBet(), "P3 does not have the corret money");
            assertEquals(0, p4.getMoneyOffBet(), "P3 does not have the corret money");

        }
        catch(OnlyOnePlayerLeftException e) {
            assertEquals(true, false, "This test should not reach this code");
        }
    }

    @Test
    void allin1_allin2_raise() throws CancelGameException {

        final int INITIAL_MONEY = 1000;
        final int SB = 1;
        final int BB = SB * 2;
        
        FakePlayer p0 = new FakePlayer(0, 2 * INITIAL_MONEY, 0);
        FakePlayer p1 = new FakePlayer(1, INITIAL_MONEY, 0);
        FakePlayer p2 = new FakePlayer(2, 2 * INITIAL_MONEY, 0);
        FakePlayer p3 = new FakePlayer(3, INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(4);

        try {

            playerList.addPlayer( p0 );
            playerList.addPlayer( p1 );
            playerList.addPlayer( p2 );
            playerList.addPlayer( p3 );

            playerList.assignRolesToAllPlayers();

            // Order: P1, P2, P3, P0
            p1.commands = new ArrayList<>( List.of("all-in") );  // 1000
            p2.commands = new ArrayList<>( List.of("call", "raise 500") );    // 1000 (1000$ restantes)
            p3.commands = new ArrayList<>( List.of("all-in") );  // 1000
            p0.commands = new ArrayList<>( List.of("call", "call") );    // 1000 (1000$ restantes)
            
            // P0 and P3 all-in but P2 and P4 have 1000$ left : 4000$ in total
            playerList.playHand(SB, BB, false);

            // ONLY P2 and P4 play : both bet 500$ : 1000$ in total
            playerList.playHand(SB, BB, false);
            
            // P2 wins the first pot (1000, 1000, 1000, 1000)
            // P1 wins the second pot (500, 500)
            // Both P2 and P4 lost the game 
            PlayerEvaluation p0Ev = new PlayerEvaluation(0, (short)100);
            PlayerEvaluation p1Ev = new PlayerEvaluation(1, (short) 50);
            PlayerEvaluation p2Ev = new PlayerEvaluation(2, (short)400);
            PlayerEvaluation p3Ev = new PlayerEvaluation(3, (short)500);

            playerList.calculatePrizeDistribution( new ArrayList<>( List.of(p0Ev, p1Ev, p2Ev, p3Ev) ) );

            assertEquals(1500, p0.getMoneyOffBet());
            assertEquals(4000, p1.getMoneyOffBet());
            assertEquals(500, p2.getMoneyOffBet());
            assertEquals(0, p3.getMoneyOffBet());
        }
        catch(OnlyOnePlayerLeftException e) {
            assertEquals(true, false, "This test should not reach this code");
        }
    }


}