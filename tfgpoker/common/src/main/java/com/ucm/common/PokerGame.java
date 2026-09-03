package com.ucm.common;

import java.io.IOException;
import java.io.InputStream;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.common.gameobjects.Suit;

/**
 * Utility class containing helper methods for receiving and decoding poker
 * game data from a network stream.
 * <p>
 * The methods provided by this class read protocol-specific values from an
 * {@link InputStream} and convert them into domain objects used by the
 * application, such as player roles, cards, and game streets.
 * </p>
 * 
 * <p>
 * This is a utility class and cannot be instantiated.
 * </p>
 */
public class PokerGame {
    
    /**
     * Prevents instantiation of this utility class.
     */
    private PokerGame() {}
    /**
     * Receives a player role from the given input stream and converts it into a
     * {@link PlayerRole} object.
     * <p>
     * The role is transmitted as a numeric network code and converted into
     * the corresponding {@link PlayerRole} enum value.
     * </p>
     * 
     * @param in input stream from which the role code is read
     * @return the decoded {@link PlayerRole}, or {@code null} if the received code does not correspond to any valid role
     * @throws IOException if an error occurs while reading from the stream
     */
    public static PlayerRole receivePlayerRole(InputStream in) throws IOException {
        int roleCode = SocketUtils.receiveInt(in);
        return PlayerRole.getPlayerRoleFromCode(roleCode);
    }
    /**
     * Receives a card from the given input stream and converts it into a
     * {@link Card} object.
     * <p>
     * A card is transmitted as two consecutive integer values:
     * </p>
     * <ul>
     *  <li>The card rank.</li>
     *  <li>The card suit.</li>
     * </ul>
     * <p>
     * These values are converted into a {@link Card} instance.
     * </p>
     * @param in input stream from which the card information is read
     * @return the decoded {@link Card}
     * @throws IOException IOException if an error occurs while reading from the stream
     */
    public static Card receiveCard(InputStream in) throws IOException {
        
		int valueCode = SocketUtils.receiveInt(in);
		int suitCode = SocketUtils.receiveInt(in);

        Card c = new Card(
            Card.getCardValueFromCode(valueCode), 
            Suit.getSuitFromCode(suitCode)
        );
        
		return c;
    }
    /**
     * Receives a poker street from the input stream.
     * <p>
     * The street is transmitted as a network code and mapped to the
     * corresponding {@link PokerStreet} value.
     * </p>
     *
     * @param in input stream from which the street code is read
     * @return the corresponding {@link PokerStreet}, or {@code null} if the
     *         received code is not associated with any known street
     * @throws IOException if an error occurs while reading from the stream
     */
    public static PokerStreet receiveStreet(InputStream in) throws IOException {
        
        int networkCode = SocketUtils.receiveInt(in);
        for(PokerStreet street : PokerStreet.values()) {
            if(street.getNetworkCode() == networkCode)
                return street;
        }

        return null;
    }
}