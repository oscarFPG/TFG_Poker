package com.ucm.server.players;

import com.ucm.server.gameobjects.Bot;
import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;

import static dev.langchain4j.data.message.UserMessage.userMessage;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;


public class ChatgptLLM extends BotLLM {



    public ChatgptLLM() {
        super();
    }

    public ChatgptLLM(int id, int money) {
        super(id, "ChatGPT", money, "Una");
    }


    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {
        
        ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenCountEstimator(GPT_4_O_MINI));
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(_apiKey)
                .modelName(GPT_4_O_MINI)
                .build();

        chatMemory.add( userMessage("Dame un dato interesante") );
        AiMessage answer = model.chat(chatMemory.messages()).aiMessage();

        return answer.text();
    }

    @Override
    public void notifyPlayerRole(PlayerRole role) {
        
    }

    @Override
    public void notifyPlayerCard(Card c) {
        
    }

    @Override
    public void notifyTableCard(Card c) {
        
    }

    @Override
    public void notifyMoneyAmount(int amount) {
        
    }

    @Override
    public void notifySmallBlindBet(int amount) {
        
    }

    @Override
    public void notifyBigBlindBet(int amount) {
        
    }

    @Override
    public void notifyTurnWait() {
        
    }

    @Override
    public void notifyTurnPlay() {
        
    }

    @Override
    public void notifyRoundEnded() {
        
    }

    @Override
    public void notifyHandEnded() {
        
    }

    @Override
    public void notifyGameEnded() {
        
    }

    @Override
    public void notifyGameKeeps() {
        
    }

    @Override
    public void notifyHandWinner() {
        
    }

    @Override
    public void notifyHandLoser() {
        
    }

    @Override
    public void notifyGameWinner() {
        
    }

    @Override
    public void notifyGameLoser() {
        
    }

    @Override
    public void notifyHandEndsByFolds() {
        
    }
    
}
