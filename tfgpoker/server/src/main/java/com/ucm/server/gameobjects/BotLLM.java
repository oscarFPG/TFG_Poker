package com.ucm.server.gameobjects;


public abstract class BotLLM extends Bot {


    protected String _apiKey;


    public BotLLM() {
        super();
    }

    public BotLLM(int id, String name, int money, String apiKey) {
        super(id, name, money);
        _apiKey = apiKey;
    }


    public abstract String actionMakePlay(int sb, int bb, int maxBet);

    public abstract void notifySmallBlindBet(final int amount);
    public abstract void notifyBigBlindBet(final int amount);
    public abstract void notifyTurnWait();
    public abstract void notifyTurnPlay();
    public abstract void notifyRoundEnded();
    public abstract void notifyHandEnded();
    public abstract void notifyGameEnded();
    public abstract void notifyGameKeeps();
    public abstract void notifyHandWinner();
    public abstract void notifyHandLoser();
    public abstract void notifyGameWinner();
    public abstract void notifyGameLoser();
    public abstract void notifyHandEndsByFolds();
    
}