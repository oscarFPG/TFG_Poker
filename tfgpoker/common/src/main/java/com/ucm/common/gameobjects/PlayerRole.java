package com.ucm.common.gameobjects;

import java.util.ArrayList;
import java.util.List;

import com.ucm.common.GameType;

/**
* Represents the possible positions (roles) of a player at a poker table.
* <p>
* Each role is associated with a network code used for communication between
* client and server. The enum also provides utility methods for:
* </p>
* <ul>
*   <li>Converting a network role code into a {@code PlayerRole}.</li>
*   <li>Generating the appropriate role distribution based on the number
* of players at the table.</li>
* </ul>
*
* <p>
* Supported table sizes range from 2 to 9 players.
* </p>
 */
public enum PlayerRole {

    /**
     * First position to act preflop, immediately after the big blind. In a 9-handed game, this is the first player to act.
     */
    UNDER_THE_GUN(GameType.PLAYER_ROLE_UNDER_THE_GUN),
    /**
     * Second Under The Gun position at larger tables (8 or 9 players). This player acts after the first Under The Gun player.
     */
    UNDER_THE_GUN_1(GameType.PLAYER_ROLE_UNDER_THE_GUN_1),
    /**
     * Third Under The Gun position at 9-handed tables. This player acts after the second Under The Gun player.
     */
    UNDER_THE_GUN_2(GameType.PLAYER_ROLE_UNDER_THE_GUN_2),
    /**
     * Lojack position, typically the third-to-last player to act preflop. This player is seated two positions to the right of the dealer.
     */
    LOJACK(GameType.PLAYER_ROLE_LOJACK),
    /**
     * Hijack position, typically the second-to-last player to act preflop. This player is seated one position to the right of the dealer.
     */
    HIJACK(GameType.PLAYER_ROLE_HIJACK),
    /**
     * Cutoff position, typically the last player to act preflop before the dealer. This player is seated directly to the right of the dealer.
     */
    CUT_OFF(GameType.PLAYER_ROLE_CUT_OFF),
    /**
     * Dealer position, responsible for dealing the cards and acting last in each betting round. This player is seated at the dealer button.
     */
    DEALER(GameType.PLAYER_ROLE_DEALER),
    /**
     * Small blind position, responsible for posting the small blind bet. This player is seated immediately to the left of the dealer.
     */
    SMALL_BLIND(GameType.PLAYER_ROLE_SMALL_BLIND),
    /**
     * Big blind position, responsible for posting the big blind bet. This player is seated immediately to the left of the small blind.
     */
    BIG_BLIND(GameType.PLAYER_ROLE_BIG_BLIND);
    /**
     * Network code associated with the player role, used for communication between client and server.
     */
    int networkCode;

    /**
     * Constructor for the PlayerRole enum, associating a network code with the role.
     * @param code
     */
    PlayerRole(final int code) {
        networkCode = code;
    }
    /**
     * Static method to retrieve a PlayerRole based on its network code.
     * @param roleCode
     * @return the matching {@code PlayerRole}, or {@code null} if the code does not correspond to any role.
     */
    public static PlayerRole getPlayerRoleFromCode(final int roleCode) {

        switch (roleCode) {
        case GameType.PLAYER_ROLE_UNDER_THE_GUN:
            return UNDER_THE_GUN;

        case GameType.PLAYER_ROLE_UNDER_THE_GUN_1:
            return UNDER_THE_GUN_1;

        case GameType.PLAYER_ROLE_UNDER_THE_GUN_2:
            return UNDER_THE_GUN_2;

        case GameType.PLAYER_ROLE_LOJACK:
            return LOJACK;

        case GameType.PLAYER_ROLE_HIJACK:
            return HIJACK;

        case GameType.PLAYER_ROLE_CUT_OFF:
            return CUT_OFF;

        case GameType.PLAYER_ROLE_DEALER:
            return DEALER;

        case GameType.PLAYER_ROLE_SMALL_BLIND:
            return SMALL_BLIND;

        case GameType.PLAYER_ROLE_BIG_BLIND:
            return BIG_BLIND;

        default:
            return null;
        }
    }
    /**
     * Returns the role distribution for a poker table with the specified number of players.
     * <p>
     * The returned list contains the seating positions in play for the given number of players, starting from the dealer and moving clockwise around the table.
     * </p>
     *  
     * @param numPlayers
     * @return a list of {@code PlayerRole} representing the roles for the specified number of players, or {@code null} if the number of players is not supported (less than 2 or more than 9).
     */
    public static List<PlayerRole> getRolesDistribution(final int numPlayers) {
        switch (numPlayers) {
            case 2:
                return new ArrayList<>(List.of(
                    SMALL_BLIND,
                    BIG_BLIND
                ));

            case 3:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND
                ));

            case 4:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN
                ));

            case 5:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    CUT_OFF
                ));

            case 6:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    HIJACK,
                    CUT_OFF
                ));

            case 7:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    LOJACK,
                    HIJACK,
                    CUT_OFF
                ));

            case 8:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    UNDER_THE_GUN_1,
                    LOJACK,
                    HIJACK,
                    CUT_OFF
                ));

            case 9:
                return new ArrayList<>(List.of(
                    DEALER,
                    SMALL_BLIND,
                    BIG_BLIND,
                    UNDER_THE_GUN,
                    UNDER_THE_GUN_1,
                    UNDER_THE_GUN_2,
                    LOJACK,
                    HIJACK,
                    CUT_OFF
                ));

            default:
                return null;
        }
    }
    /**
     * Returns the network code associated with this player role.
     * @return the network code for this {@code PlayerRole}.
     */
    public int getNetworkCode() {
        return networkCode;
    }

}