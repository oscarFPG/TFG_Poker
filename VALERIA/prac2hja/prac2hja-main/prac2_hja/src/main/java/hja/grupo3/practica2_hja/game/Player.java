package hja.grupo3.practica2_hja.game;

import hja.grupo3.practica2_hja.play.Flush;
import hja.grupo3.practica2_hja.play.Full_House;
import hja.grupo3.practica2_hja.play.Straight;
import hja.grupo3.practica2_hja.play.Poker;
import hja.grupo3.practica2_hja.play.High_Card;
import hja.grupo3.practica2_hja.play.Pair;
import hja.grupo3.practica2_hja.play.Two_Pairs;
import hja.grupo3.practica2_hja.play.Royal_Flush;
import hja.grupo3.practica2_hja.play.Straight_Flush;
import hja.grupo3.practica2_hja.play.Three_of_a_Kind;
import hja.grupo3.practica2_hja.play.Play;

public class Player {
 
    private Card[] cards;
    private String[] draws;
    private String id;
        
    private int best_hand = 12;
    private char high_card = 'X';
    
    private String name_play = "";
    
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
    
    public int getPriority() {
        return best_hand;
    } 
       
}
