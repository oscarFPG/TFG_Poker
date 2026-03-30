package com.ucm.server.players;


import com.ucm.server.gameobjects.BotLLM;
import com.ucm.server.gameobjects.Card;
import com.ucm.server.gameobjects.PlayerRole;

import dev.langchain4j.data.message.AiMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;


public class ChatgptLLM extends BotLLM {


    public ChatgptLLM() {
        super();
    }

    public ChatgptLLM(int id, int money, final String apiKey) {
        super(id, "ChatGPT", money, apiKey);
    }


    @Override
    public String getDescription() {
        return "LLM developed by OpenAI";
    }

    @Override
    public String actionMakePlay(int sb, int bb, int maxBet) {
        
        ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenCountEstimator(GPT_4_O_MINI));
        OpenAiChatModel model = OpenAiChatModel
                                    .builder()
                                    .apiKey(_apiKey)
                                    .modelName(OpenAiChatModelName.GPT_4_1_MINI)
                                    .build();

        chatMemory.add( 
            userMessage(
                "Esto es una prueba de un tfg sobre bots de poker. Responde solo con call, fold, raise <cantidad> o all-in para simular tu respuesta"
            ) 
        );
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

    @Override
    public void notifyPlayerAction(PlayerRole role, String action, double amount) {
        
    }
    
}
