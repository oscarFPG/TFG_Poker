package com.ucm.server.statistics; 
 
import com.ucm.common.PokerStreet; 
 
 
/** 
 * Stores the data collected for a player during a poker hand. 
 * 
 * <p> 
 * The class contains information about the player's stack, equity, 
 * decision time, poker events and hand result. 
 * </p> 
 */ 
public class PlayerExperimentData { 
 
    /*-------------------------------------------------- 
     * HAND INFORMATION 
     *--------------------------------------------------*/ 
 
    /** Initial chip stack at the beginning of the hand. */ 
    private int initialStack; 
 
    /** Final chip stack at the end of the hand. */ 
    private int finalStack; 
 
    /** Net change in chips during the hand. */ 
    private int netChips; 
 
    /** Player's equity before the flop. */ 
    private double preflopEquity; 
 
    /** Time taken by the player to make a decision. */ 
    private long decisionTime; 
 
    /** Player's equity on the flop. */ 
    private double flopEquity; 
 
    /** Player's equity on the turn. */ 
    private double turnEquity; 
 
    /** Player's equity on the river. */ 
    private double riverEquity; 
 
    /*-------------------------------------------------- 
     * POKER EVENTS 
     *--------------------------------------------------*/ 
 
    /** Indicates whether the player voluntarily put money into the pot. */ 
    private boolean vpip; 
 
    /** Indicates whether the player made a preflop raise. */ 
    private boolean pfr; 
 
    /** Indicates whether the player made a three-bet. */ 
    private boolean threeBet; 
   
 
    /** Indicates whether the player reached the showdown. */ 
    private boolean wtsd; 
 
    /** Indicates whether the player won at showdown. */ 
    private boolean wsd; 
 
    /** Indicates whether the player won the hand after seeing the flop. */ 
    private boolean wwsf; 
 
    /*-------------------------------------------------- 
     * RESULT 
     *--------------------------------------------------*/ 
 
    /** Indicates whether the player won the hand. */ 
    private boolean wonHand; 
 
    /** 
     * Creates a new instance and initializes all values to their defaults. 
     */ 
    public PlayerExperimentData() { 
        reset(); 
    } 
 
    /** 
     * Resets all stored data to its default values. 
     */ 
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
 
    /** 
     * Returns the initial chip stack. 
     * 
     * @return initial stack 
     */ 
    public int getInitialStack() { 
        return initialStack; 
    } 
 
    /** 
     * Sets the initial chip stack. 
     * 
     * @param initialStack initial stack 
     */ 
    public void setInitialStack(int initialStack) { 
        this.initialStack = initialStack; 
    } 
 
    /** 
     * Returns the final chip stack. 
     * 
     * @return final stack 
     */ 
    public int getFinalStack() { 
        return finalStack; 
    } 
 
    /** 
     * Sets the final chip stack. 
     * 
     * @param finalStack final stack 
     */ 
    public void setFinalStack(int finalStack) { 
        this.finalStack = finalStack; 
    } 
 
    /** 
     * Returns the net change in chips. 
     * 
     * @return net chips 
     */ 
    public int getNetChips() { 
        return netChips; 
    } 
 
    /** 
     * Sets the net change in chips. 
     * 
     * @param netChips net change in chips 
     */ 
    public void setNetChips(int netChips) { 
        this.netChips = netChips; 
    } 
 
    /** 
     * Returns the player's preflop equity. 
     * 
     * @return preflop equity 
     */ 
    public double getPreflopEquity() { 
        return preflopEquity; 
    } 
 
    /** 
     * Sets the player's preflop equity. 
     * 
     * @param preflopEquity preflop equity 
     */ 
    public void setPreflopEquity(double preflopEquity) { 
        this.preflopEquity = preflopEquity; 
    } 
     
 
    /** 
     * Returns the decision time. 
     * 
     * @return decision time 
     */ 
    public long getDecisionTime() { 
        return decisionTime; 
    } 
 
    /** 
     * Sets the decision time. 
     * 
     * @param decisionTime decision time 
     */ 
    public void setDecisionTime(long decisionTime) { 
        this.decisionTime = decisionTime; 
    } 
 
    /** 
     * Returns whether the player voluntarily put money into the pot. 
     * 
     * @return {@code true} if VPIP occurred 
     */ 
    public boolean isVPIP() { 
        return vpip; 
    } 
 
    /** 
     * Sets the VPIP status. 
     * 
     * @param vpip VPIP status 
     */ 
    public void setVPIP(boolean vpip) { 
        this.vpip = vpip; 
    } 
 
    /** 
     * Returns whether the player made a preflop raise. 
     * 
     * @return {@code true} if PFR occurred 
     */ 
    public boolean isPFR() { 
        return pfr; 
    } 
 
    /** 
     * Sets the PFR status. 
     * 
     * @param pfr PFR status 
     */ 
    public void setPFR(boolean pfr) { 
        this.pfr = pfr; 
    } 
 
    /** 
     * Returns whether the player made a three-bet. 
     * 
     * @return {@code true} if a three-bet occurred 
     */ 
    public boolean isThreeBet() { 
        return threeBet; 
    } 
 
    /** 
     * Sets the three-bet status. 
     * 
     * @param threeBet three-bet status 
     */ 
    public void setThreeBet(boolean threeBet) { 
        this.threeBet = threeBet; 
    } 
 
 
    /** 
     * Returns whether the player reached the showdown. 
     * 
     * @return {@code true} if the player reached showdown 
     */ 
    public boolean isWTSD() { 
        return wtsd; 
    } 
 
    /** 
     * Sets the WTSD status. 
     * 
     * @param wtsd WTSD status 
     */ 
    public void setWTSD(boolean wtsd) { 
        this.wtsd = wtsd; 
    } 
 
    /** 
     * Returns whether the player won at showdown. 
     * 
     * @return {@code true} if the player won at showdown 
     */ 
    public boolean isWSD() { 
        return wsd; 
    } 
 
    /** 
     * Sets the WSD status. 
     * 
     * @param wsd WSD status 
     */ 
    public void setWSD(boolean wsd) { 
        this.wsd = wsd; 
    } 
 
    /** 
     * Returns whether the player won the hand after seeing the flop. 
     * 
     * @return {@code true} if the player won after the flop 
     */ 
    public boolean isWWSF() { 
        return wwsf; 
    } 
 
    /** 
     * Sets the WWSF status. 
     * 
     * @param wwsf WWSF status 
     */ 
    public void setWWSF(boolean wwsf) { 
        this.wwsf = wwsf; 
    } 
 
    /** 
     * Returns whether the player won the hand. 
     * 
     * @return {@code true} if the player won the hand 
     */ 
    public boolean isWonHand() { 
        return wonHand; 
    } 
 
    /** 
     * Sets the hand result. 
     * 
     * @param wonHand {@code true} if the player won the hand 
     */ 
    public void setWonHand(boolean wonHand) { 
        this.wonHand = wonHand; 
    } 
 
    /** 
     * Returns the player's flop equity. 
     * 
     * @return flop equity 
     */ 
    public double getFlopEquity() { 
        return flopEquity; 
    } 
 
    /** 
     * Sets the player's flop equity. 
     * 
     * @param flopEquity flop equity 
     */ 
    public void setFlopEquity(double flopEquity) { 
        this.flopEquity = flopEquity; 
    } 
 
    /** 
     * Returns the player's turn equity. 
     * 
     * @return turn equity 
     */ 
    public double getTurnEquity() { 
        return turnEquity; 
    } 
 
    /** 
     * Sets the player's turn equity. 
     * 
     * @param turnEquity turn equity 
     */ 
    public void setTurnEquity(double turnEquity) { 
        this.turnEquity = turnEquity; 
    } 
 
    /** 
     * Returns the player's river equity. 
     * 
     * @return river equity 
     */ 
    public double getRiverEquity() { 
        return riverEquity; 
    } 
 
    /** 
     * Sets the player's river equity. 
     * 
     * @param riverEquity river equity 
     */ 
    public void setRiverEquity(double riverEquity) { 
        this.riverEquity = riverEquity; 
    } 
 
    /** 
     * Sets the equity corresponding to the specified poker street. 
     * 
     * @param street poker street for which the equity is set 
     * @param equity equity value 
     */ 
    public void setEquity(PokerStreet street, double equity) { 
 
        switch (street) { 
 
            case PREFLOP -> preflopEquity = equity; 
            case FLOP    -> flopEquity = equity; 
            case TURN    -> turnEquity = equity; 
            case RIVER   -> riverEquity = equity; 
        } 
    } 
 
}