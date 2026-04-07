package com.ucm.server.logic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.ucm.server.FakePlayer;
import com.ucm.server.exceptions.OnlyOnePlayerLeftException;
import com.ucm.server.gameobjects.PlayerRole;
import com.ucm.server.middleclasses.PlayerEvaluation;


public class PlayerListTest {
 
    private static final int INITIAL_MONEY = 1000;

    private static final List<PlayerRole> _rolePositions = new ArrayList<>(
        List.of(
            PlayerRole.DEALER,
            PlayerRole.SMALL_BLIND,
            PlayerRole.BIG_BLIND,
            PlayerRole.UNDER_THE_GUN,
            PlayerRole.MIDDLE_POSITION,
            PlayerRole.CUT_OFF,
            PlayerRole.NO_ROLE,
            PlayerRole.NO_ROLE,
            PlayerRole.NO_ROLE
        )
    );

    static Stream<Arguments> playerRoleProvider() {
        return Stream.of(
            Arguments.of(2, 
                List.of(
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND
                )
            ),
            Arguments.of(3, 
                List.of(
                    PlayerRole.DEALER,
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND
                )
            ),
            Arguments.of(4, 
                List.of(
                    PlayerRole.DEALER, 
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND,
                    PlayerRole.UNDER_THE_GUN
                )
            ),
            Arguments.of(5, 
                List.of(
                    PlayerRole.DEALER, 
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND,
                    PlayerRole.UNDER_THE_GUN,
                    PlayerRole.MIDDLE_POSITION
                )
            ),
            Arguments.of(6, 
                List.of(
                    PlayerRole.DEALER, 
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND,
                    PlayerRole.UNDER_THE_GUN,
                    PlayerRole.MIDDLE_POSITION,
                    PlayerRole.CUT_OFF
                )
            ),
            Arguments.of(7, 
                List.of(
                    PlayerRole.DEALER, 
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND,
                    PlayerRole.UNDER_THE_GUN,
                    PlayerRole.MIDDLE_POSITION,
                    PlayerRole.CUT_OFF,
                    PlayerRole.NO_ROLE
                )
            ),
            Arguments.of(8, 
                List.of(
                    PlayerRole.DEALER, 
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND,
                    PlayerRole.UNDER_THE_GUN,
                    PlayerRole.MIDDLE_POSITION,
                    PlayerRole.CUT_OFF,
                    PlayerRole.NO_ROLE,
                    PlayerRole.NO_ROLE
                )
            ),
            Arguments.of(9, 
                List.of(
                    PlayerRole.DEALER, 
                    PlayerRole.SMALL_BLIND, 
                    PlayerRole.BIG_BLIND,
                    PlayerRole.UNDER_THE_GUN,
                    PlayerRole.MIDDLE_POSITION,
                    PlayerRole.CUT_OFF,
                    PlayerRole.NO_ROLE,
                    PlayerRole.NO_ROLE,
                    PlayerRole.NO_ROLE
                )
            )
        );
    }

    private List<PlayerRole> getSubListWithSize(final int size) {
        return _rolePositions.subList(0, size);
    }

    private List<FakePlayer> getPlayerSubsetWithSize(final int size) {
        
        List<FakePlayer> players = new ArrayList<>();
        for(int i = 0; i < size; i++)
            players.add( new FakePlayer(1, INITIAL_MONEY, 0) );

        return players;
    }

    @ParameterizedTest
    @MethodSource("playerRoleProvider")
    void initialRoleAssigment(int numPlayers, List<PlayerRole> expectedRoles) {

        PlayerList playerList = new PlayerList(numPlayers);
        FakePlayer[] players = new FakePlayer[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new FakePlayer(0, INITIAL_MONEY, 0);
            playerList.addPlayer( players[i] );
        }

        playerList.assignRolesToAllPlayers();
        for (int i = 0; i < expectedRoles.size(); i++) {
            assertEquals(expectedRoles.get(i), players[i].getRole());
        }
    }

    @Test
    void assignNewRolesOnPassTurnWithTwoPlayers() {

        FakePlayer player1 = new FakePlayer(0, INITIAL_MONEY, 0);
        FakePlayer player2 = new FakePlayer(1, INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(2);
        
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);


        playerList.assignRolesToAllPlayers();
        assertEquals(PlayerRole.SMALL_BLIND, player1.getRole());
        assertEquals(PlayerRole.BIG_BLIND, player2.getRole());

        playerList.passTurn();
        assertEquals(PlayerRole.BIG_BLIND, player1.getRole());
        assertEquals(PlayerRole.SMALL_BLIND, player2.getRole());

        playerList.passTurn();
        assertEquals(PlayerRole.SMALL_BLIND, player1.getRole());
        assertEquals(PlayerRole.BIG_BLIND, player2.getRole());
    }


    @ParameterizedTest(name = "Roles assignment on turns in {0} players")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9})
    void roleAssigmentsOnPassTurn(int numPlayers) {

        List<FakePlayer> players = getPlayerSubsetWithSize(numPlayers);
        List<PlayerRole> roles = getSubListWithSize(numPlayers);
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

        }

    }


    @Test
    void call() {

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
    void raise() {
        
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
    void allin_raise() {

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
            playerList.playHand(SB, BB, true);      // 2 + 2 + 2 = 6$ total - 2$ = 4$ beneficio

            p2.commands = new ArrayList<>( List.of("check", "call", "allin") );
            p3.commands = new ArrayList<>( List.of("check", "allin") );
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
    void allin1_allin2() {
        
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

            p2.commands = new ArrayList<>( List.of("allin") );  // 1000
            p3.commands = new ArrayList<>( List.of("allin") );  // 1000
            p4.commands = new ArrayList<>( List.of("allin") );  // 2000
            p1.commands = new ArrayList<>( List.of("allin") );  // 2000
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

}