package com.ucm.server.players;

import java.io.IOException;
import java.util.List;

import com.ucm.common.gameobjects.Card;
import com.ucm.common.gameobjects.PlayerRole;
import com.ucm.server.commands.Command;
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Player;
import com.ucm.server.interfaces.IPokerPlayer;
import com.ucm.server.middleclasses.CommandResult;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;


public class GeminiLLM extends BotLLM {


    public GeminiLLM(int id, int money, String apiKey) {
        super(id, "GeminiLLM", money, apiKey);
    }


    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {
        
        ChatModel gemini = GoogleAiGeminiChatModel.builder()
                                .apiKey( _apiKey )
                                .modelName("gemini-2.5-flash")
                                .build();

        String response = gemini.chat("This is an API test for po ker, response only with call, fold or raise <amount>");
        return response;
    }

    @Override
    public void notifyPlayerRole(PlayerRole role) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerRole'");
    }

    @Override
    public void notifyPlayerCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerCard'");
    }

    @Override
    public void notifyTableCard(Card c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTableCard'");
    }

    @Override
    public void notifyMoneyAmount(int amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyMoneyAmount'");
    }

    @Override
    public void notifySmallBlindBet(int amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifySmallBlindBet'");
    }

    @Override
    public void notifyBigBlindBet(int amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyBigBlindBet'");
    }

    @Override
    public void notifyTurnWait() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTurnWait'");
    }

    @Override
    public void notifyTurnPlay() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTurnPlay'");
    }

    @Override
    public void notifyRoundEnded() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyRoundEnded'");
    }

    @Override
    public void notifyHandEnded() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandEnded'");
    }

    @Override
    public void notifyGameEnded() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameEnded'");
    }

    @Override
    public void notifyGameKeeps() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameKeeps'");
    }

    @Override
    public void notifyHandWinner() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandWinner'");
    }

    @Override
    public void notifyHandLoser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandLoser'");
    }

    @Override
    public void notifyGameWinner() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameWinner'");
    }

    @Override
    public void notifyGameLoser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyGameLoser'");
    }

    @Override
    public void notifyHandEndsByFolds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyHandEndsByFolds'");
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
    }

    @Override
    public void notifyPlayerAction(PlayerRole role, String action, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerAction'");
    }


    @Override public void notifyOtherPlayerAction(IPokerPlayer p) {}



    @Override
    public void notifyPlayerState(final IPokerPlayer player, boolean last)
            throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyPlayerState'");
    }


    @Override
    public void notifyTotalPot(int total) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyTotalPot'");
    }
    
}
