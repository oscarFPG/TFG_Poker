package com.ucm.server.statistics;


public class PlayerExperimentData {

    /*--------------------------------------------------
     * HAND INFORMATION
     *--------------------------------------------------*/

    private int initialStack;
    private int finalStack;
    private int netChips;

    private double preflopEquity;
    private long decisionTime;

    private double flopEquity;
    private double turnEquity;
    private double riverEquity;

    /*--------------------------------------------------
     * POKER EVENTS
     *--------------------------------------------------*/

    private boolean vpip;
    private boolean pfr;
    private boolean threeBet;
  

    private boolean wtsd;
    private boolean wsd;
    private boolean wwsf;

    /*--------------------------------------------------
     * RESULT
     *--------------------------------------------------*/

    private boolean wonHand;

    public PlayerExperimentData() {
        reset();
    }

    public void reset() {

        initialStack = 0;
        finalStack = 0;
        netChips = 0;

        preflopEquity = 0;
        flopEquity = 0;
        turnEquity = 0;
        riverEquity = 0;

        decisionTime = 0;

        vpip = false;
        pfr = false;
        threeBet = false;
       

        wtsd = false;
        wsd = false;
        wwsf = false;

        wonHand = false;
    }

    public int getInitialStack() {
        return initialStack;
    }

    public void setInitialStack(int initialStack) {
        this.initialStack = initialStack;
    }

    public int getFinalStack() {
        return finalStack;
    }

    public void setFinalStack(int finalStack) {
        this.finalStack = finalStack;
    }

    public int getNetChips() {
        return netChips;
    }

    public void setNetChips(int netChips) {
        this.netChips = netChips;
    }

    public double getPreflopEquity() {
        return preflopEquity;
    }

    public void setPreflopEquity(double preflopEquity) {
        this.preflopEquity = preflopEquity;
    }
    

    public long getDecisionTime() {
        return decisionTime;
    }

    public void setDecisionTime(long decisionTime) {
        this.decisionTime = decisionTime;
    }

    public boolean isVPIP() {
        return vpip;
    }

    public void setVPIP(boolean vpip) {
        this.vpip = vpip;
    }

    public boolean isPFR() {
        return pfr;
    }

    public void setPFR(boolean pfr) {
        this.pfr = pfr;
    }

    public boolean isThreeBet() {
        return threeBet;
    }

    public void setThreeBet(boolean threeBet) {
        this.threeBet = threeBet;
    }


    public boolean isWTSD() {
        return wtsd;
    }

    public void setWTSD(boolean wtsd) {
        this.wtsd = wtsd;
    }

    public boolean isWSD() {
        return wsd;
    }

    public void setWSD(boolean wsd) {
        this.wsd = wsd;
    }

    public boolean isWWSF() {
        return wwsf;
    }

    public void setWWSF(boolean wwsf) {
        this.wwsf = wwsf;
    }

    public boolean isWonHand() {
        return wonHand;
    }

    public void setWonHand(boolean wonHand) {
        this.wonHand = wonHand;
    }

    public double getFlopEquity() {
        return flopEquity;
    }

    public void setFlopEquity(double flopEquity) {
        this.flopEquity = flopEquity;
    }

    public double getTurnEquity() {
        return turnEquity;
    }

    public void setTurnEquity(double turnEquity) {
        this.turnEquity = turnEquity;
    }

    public double getRiverEquity() {
        return riverEquity;
    }

    public void setRiverEquity(double riverEquity) {
        this.riverEquity = riverEquity;
    }

    public void setEquity(Street street, double equity) {

        switch (street) {

            case PREFLOP -> preflopEquity = equity;
            case FLOP    -> flopEquity = equity;
            case TURN    -> turnEquity = equity;
            case RIVER   -> riverEquity = equity;
        }
    }

}