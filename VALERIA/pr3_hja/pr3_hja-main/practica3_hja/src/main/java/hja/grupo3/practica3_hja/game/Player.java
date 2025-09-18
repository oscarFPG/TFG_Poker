package hja.grupo3.practica3_hja.game;

import hja.grupo3.practica3_hja.plays.Two_Pairs;
import hja.grupo3.practica3_hja.plays.Three_of_a_Kind;
import hja.grupo3.practica3_hja.plays.Straight;
import hja.grupo3.practica3_hja.plays.Royal_Flush;
import hja.grupo3.practica3_hja.plays.Pair;
import hja.grupo3.practica3_hja.plays.High_Card;
import hja.grupo3.practica3_hja.plays.Flush;
import hja.grupo3.practica3_hja.plays.Full_House;
import hja.grupo3.practica3_hja.plays.Straight_Flush;
import hja.grupo3.practica3_hja.plays.Poker;
import hja.grupo3.practica3_hja.plays.Play;

public class Player {
 
    private Card[] cards;
    private Card[] cards_sol;
    private String[] draws;
    private String id;
    
    private boolean fold = false;
        
    private float equity = 0;
    
    private int best_hand = 12;
    private char[] high_card;
    
    private String name_play = "";
    
    private float win = 0;
    
    public Player(Card[] cards){
        this.cards = cards;
    }
    
    public Player(String id, Card[] cards) {
        this.cards = cards;
        this.id = id;     
    }
        
    public String getBestHand(){        
        Play play = new Royal_Flush();
        if (play.checkPlay(cards)){
            best_hand = 1; /*Priorities.Royal_Flush;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
        }
        play = new Straight_Flush();
        if(play.checkPlay(cards)){
            best_hand = 2; /*Priorities.Straight_Flush*/;
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Poker();
        if(play.checkPlay(cards)){
            best_hand = 3; /*Priorities.Poker;*/            
            high_card = play.getHighCard();
            name_play = play.getName().split(" \\(")[0];
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Full_House();
        if(play.checkPlay(cards)){
            best_hand = 4; /*Priorities.Full_House;*/
            high_card = play.getHighCard();
            name_play = play.getName().split(" \\(")[0];
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Flush();
        if(play.checkPlay(cards)){
            best_hand = 5; /*Priorities.Flush;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Straight();
        if(play.checkPlay(cards)){
            best_hand = 6; /*Priorities.Straight;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Three_of_a_Kind();
        if(play.checkPlay(cards)){
            best_hand = 7; /*Priorities.Three_of_a_Kind;*/
            high_card = play.getHighCard();
            name_play = play.getName().split(" \\(")[0];
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Two_Pairs();
        if(play.checkPlay(cards)){
            best_hand = 8; /*Priorities.Two_Pairs;*/
            high_card = play.getHighCard();
            name_play = play.getName().split(" \\(")[0];
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Pair();
        if(play.checkPlay(cards)){
            best_hand = 9; /*Priorities.Pair;*/
            high_card = play.getHighCard();
            name_play = play.getName().split(" \\(")[0];
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new High_Card();
        if(play.checkPlay(cards)){
            best_hand = 10; /*Priorities.High_Card;*/
            high_card = play.getHighCard();
            name_play = play.getName().split(" \\(")[0];
            return "- Best Hand: " + play.getName() + "\n";
        }
        return "No hay mejor jugada"; //ESTO NO ES NECESARIO 
    }
    
    public String getBestOmaha(){
        Play play = new Royal_Flush();
        if (play.checkPlay(cards) && play.isOmaha(cards)){
            best_hand = 1; /*Priorities.Royal_Flush;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
        }
        play = new Straight_Flush();
        if(play.checkPlay(cards) && play.isOmaha(cards)){
            best_hand = 2; /*Priorities.Straight_Flush*/;
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Poker();
        if(play.checkPlay(cards) && play.isOmaha(cards)){
            best_hand = 3; /*Priorities.Poker;*/            
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Full_House();
        if(play.checkPlay(cards) && play.isOmaha(cards)){
            best_hand = 4; /*Priorities.Full_House;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Flush();
        if(play.checkPlay(cards) && play.isOmaha(cards)){
            best_hand = 5; /*Priorities.Flush;*/
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Straight();
        if(play.checkPlay(cards) && play.isOmaha(cards)){
            best_hand = 6; /*Priorities.Straight;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Three_of_a_Kind();
        if(play.checkPlay(cards)){
            best_hand = 7; /*Priorities.Three_of_a_Kind;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Two_Pairs();
        if(play.checkPlay(cards)){
            best_hand = 8; /*Priorities.Two_Pairs;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new Pair();
        if(play.checkPlay(cards)){
            best_hand = 9; /*Priorities.Pair;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
         }
        play = new High_Card();
        if(play.checkPlay(cards)){
            best_hand = 10; /*Priorities.High_Card;*/
            high_card = play.getHighCard();
            name_play = play.getName();
            return "- Best Hand: " + play.getName() + "\n";
        }
        return "No hay mejor jugada"; //ESTO NO ES NECESARIO 
    }
    
    
     
    public int getPriority() {
        return best_hand;
    } 
    
    public void setPriority(int prio) {
        best_hand = prio;
    }
        
    public Card[] getCardsSol(){    
        return cards_sol;
    }
    
    public void setCardsSol(Card[] cards_hand) {
        cards_sol = cards_hand;
    }
    
    public char[] getHighCard(){
        return high_card;
    }
    
    public void setHighCard(char[] card) {
        high_card = card;
    }
    
    public String getID(){
        return id;
    }
        
    public String getNamePlay(){
        return name_play;
    }
    
    public Card[] getCards(){
        return cards;
    }
    
    public float getEquity() {
        return equity;
    }
    
    public void setEquity(float eq) {
        equity = eq;
    }
    
    public float getWin() {
        return win;
    }
    
    public void setWin(float point) {
        win += point;
    }
    
    public void reset() {
        win = 0;
        equity = 0;
    }
    
    public void setFold(boolean f) {
        fold = f;
    }
    
    public boolean getFold() {
        return fold;
    }
}
