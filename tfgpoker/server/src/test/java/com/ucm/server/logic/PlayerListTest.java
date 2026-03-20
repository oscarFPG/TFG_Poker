package com.ucm.server.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.ucm.server.FakePlayer;
import com.ucm.server.gameobjects.PlayerRole;

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
            players.add( new FakePlayer(INITIAL_MONEY, 0) );

        return players;
    }

    @ParameterizedTest
    @MethodSource("playerRoleProvider")
    void initialRoleAssigment(int numPlayers, List<PlayerRole> expectedRoles) {

        PlayerList playerList = new PlayerList(numPlayers);
        FakePlayer[] players = new FakePlayer[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new FakePlayer(INITIAL_MONEY, 0);
            playerList.addPlayer( players[i] );
        }

        playerList.assignRolesToAllPlayers();
        for (int i = 0; i < expectedRoles.size(); i++) {
            assertEquals(expectedRoles.get(i), players[i].role);
        }
    }

    @Test
    void assignNewRolesOnPassTurnWithTwoPlayers() {

        FakePlayer player1 = new FakePlayer(INITIAL_MONEY, 0);
        FakePlayer player2 = new FakePlayer(INITIAL_MONEY, 0);
        PlayerList playerList = new PlayerList(2);
        
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);


        playerList.assignRolesToAllPlayers();
        assertEquals(PlayerRole.SMALL_BLIND, player1.role);
        assertEquals(PlayerRole.BIG_BLIND, player2.role);

        playerList.passTurn();
        assertEquals(PlayerRole.BIG_BLIND, player1.role);
        assertEquals(PlayerRole.SMALL_BLIND, player2.role);

        playerList.passTurn();
        assertEquals(PlayerRole.SMALL_BLIND, player1.role);
        assertEquals(PlayerRole.BIG_BLIND, player2.role);
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
                assertEquals(role, player.role);

                playerIndex = (playerIndex + 1) % numPlayers;   // Treat as an circular array
                roleIndex = (roleIndex + 1) % numPlayers;       // Treat as an circular array
            }
            playerList.passTurn();

        }

    }


}