package com.ucm.gameobjects;

public class Player {
 
    private int _id;
    private String _name;
    private int _money;
    private PlayerRole _role;
    private Card[] _cards;
    private boolean _fold;
    

    public Player(int id, String name, int money){
        _id = id;
        _name = name;
        _money = money;
        _role = _role.NO_ROL;
    }
     
    
    public PlayerAction makePlay(final int sb, final int bb){
        
        return PlayerAction.FOLD;
    }
    
    public void fold(){
        
    }
    
    public void setRole(PlayerRole pr){
        _role = pr;
    }

    public int getID(){ return _id; }
    public String getName(){ return _name; }
    public int getMoney(){ return _money; }
    public PlayerRole getPlayerRole(){ return _role; }
    
}
