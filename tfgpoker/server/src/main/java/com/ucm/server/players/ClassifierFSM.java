package com.ucm.server.players;

import java.io.IOException;

import com.ucm.common.BotStyle;
import com.ucm.common.GameType;
import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotFSM;
import com.ucm.server.interfaces.IPlayerInfo;


public class ClassifierFSM extends BotFSM {

    private static final int FSM_ID = GameType.BOT_FSM_1;



    public ClassifierFSM() {
        super(FSM_ID);
    }


    @Override
    public String notifyMakePlay(int sb, int bb, int maxBet, IPlayerInfo player) throws IOException {
        
        // Get table cards values -> Zero if there is no card on the table
        int valueTC1 = table.get(0) != null ? table.get(0).getNumber() : 0;
        int valueTC2 = table.get(1) != null ? table.get(1).getNumber() : 0;
        int valueTC3 = table.get(2) != null ? table.get(2).getNumber() : 0;
        int valueTC4 = table.get(3) != null ? table.get(3).getNumber() : 0;
        int valueTC5 = table.get(4) != null ? table.get(4).getNumber() : 0;

        // Get player cards values
        int valueC1 = player.getPlayerCards()[0].getNumber();
        int valueC2 = player.getPlayerCards()[1].getNumber();

        // Add up table cards and player cards values
        int totalValue = 
            valueTC1 + valueTC2 + valueTC3 + valueTC4 + valueTC5 + 
            valueC1 + valueC2;

        
        // Max sum possible is (A, A, A, A, K + K, K) = (14 + 14 + 14 + 14 + 13) + (13 + 13) = 95
        // There is 4 possible actions
        // We split up equally the decision between them -> 95/4 = 23 'points' for each action
        // Order: FOLD < CALL/CHECK < RAISE 'amount' < ALL_IN             
        final int points_per_action = 95 / 4;
        if(totalValue < points_per_action) {    // FOLD - x < 23
            return GameType.FOLD_ACTION_FULL;
        }
        else if(points_per_action <= totalValue && totalValue < 2 * points_per_action) {    // CALL/CHECK - 23 <= x < 46
            return GameType.CALL_ACTION_FULL;
        }
        else if(2 * points_per_action <= totalValue && totalValue < 3 * points_per_action) {    // RAISE - 46 <= x < 69
            return GameType.RAISE_ACTION_FULL + String.valueOf(2 * maxBet);
        }
        else {  // ALL_IN
            return GameType.ALL_IN_ACTION_FULL;
        }
    }

    @Override
    public String getFullDescription() {
        return "This is a bot that can decide his next move by adding all the card values. The total value will decide the final action";
    }

    @Override
    public String getDescription() {
        return "FSM(Finit State Machine) bot that ranks his cards to select an action";
    }

    @Override
    public Bot create(BotStyle style) {
        return new ClassifierFSM();
    }

    
    @Override
    public String getPlayerModel() {
        return "ClassifierFSM";
    }
    
}