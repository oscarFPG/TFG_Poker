package com.ucm.server.gameobjects;


public abstract class Bot extends Player {


    public Bot() {
        super();
    }
    
    public Bot(int id, String name, int money) {
        super(id, name, money);
    }


    public String getFullDescription() {
        return this.getPlayerName() + " : " + this.getDescription();
    }

    public abstract String getDescription();
    
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
