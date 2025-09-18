package hja.grupo3.practica3_hja.game;

import hja.grupo3.practica3_hja.misc.CardComparatorNumber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Board {
    
    private Card[][] deck;
    private final String numberOrder = "23456789TJQKA";

    //---------ERRORES------------
    public static void error(int op){
        if (op ==1) {System.out.println("El numero de parametros introducido es incorrecto.");}
        if (op ==2) {System.out.println("Error reading file");}
        if (op ==2) {System.out.println("Error writing in file.");}
    }
    private static final char[] SUITS = new char[] {'h','d','c','s'};
    private static final char[] NUMBERS = new char[] {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};
    //------------------------------
    
        
    //--------LOGICA--------------
    private Card[] cards;
    private Card[] cards_common;
    private Card[] next_cards;
    private boolean omaha = false;
    private Player[] players;
    private Player[] playersAntiguo;    
   //-------------------------------
    
    
    public Board(){
        this.deck = new Card[4][13];
        generateDeck();
        players = new Player[6];
        cards_common = new Card[5];
        next_cards= new Card[5];
    };
   
    
    //GENERA EL MAZO
    public void generateDeck(){
        String suitOrder = "hdcs";
        for ( int i = 0; i < suitOrder.length(); i++){
            for ( int j = 0; j < numberOrder.length(); j++){
                deck[i][j] =  new Card ( numberOrder.charAt(j), suitOrder.charAt(i));
            }
        }
    }
    
    public Card[] getPlayerCards(){
        return cards;
    } 
    
    public Player[] getplayersAntiguos(){
        return playersAntiguo;
    }
    
    public Card[][] getDeck(){
        return deck;
    }
    
    public boolean numberCorrect(char n) {
        for (char number : NUMBERS) {
            if (number == n) { return true;}
        }
        
        return false;
    }
    
    public boolean suitCorrect(char n) {
        for (char suit : SUITS) {
            if (suit == n) { return true;}
        }
        
        return false;
    }
    
    public void chooseCards(int nPlayer, ArrayList<Card> cards) {
        Card[] inpCards = new Card[cards.size()];
        for(int i = 0; i < cards.size(); i++) {
            inpCards[i] = cards.get(i);
            removeCard(inpCards[i]);
        }
        players[nPlayer - 1] = new Player(Integer.toString(nPlayer), inpCards);
    }
    
    public void chooseBoardCards(int indice, ArrayList<Card> cards) {
        Card[] inpCards = new Card[cards.size()];
        for(int i = 0; i < cards.size(); i++) {
            inpCards[i] = cards.get(i);
        }
        cards_common = inpCards;
        nextCard(indice);
    }
    
    public void nextCard(int indice){
        Card[] inpCards = new Card[indice];
        for(int i = 0; i < indice; i++) {
            inpCards[i] = cards_common[i];
            removeCard(inpCards[i]);
        }
        next_cards = inpCards;
    }
    
    public Card[] getNextCards() {
        return next_cards;
    }
    
    
    public void removeCard(Card card) {
        for (int i = 0; i < 4; i++) {
            if(deck[i][0].getSuit() == card.getSuit()) {
                for (int j = 0; j < 13; j++) {
                    if(deck[i][j].getNumber() == card.getNumber()) {
                        deck[i][j].setActive(false);
                    }
                }
            }    
        }
    }
    
    public void addCard(Card card) {
        for (int i = 0; i < 4; i++) {
            if(deck[i][0].getSuit() == card.getSuit()) {
                for (int j = 0; j < 13; j++) {
                    if(deck[i][j].getNumber() == card.getNumber()) {
                        deck[i][j].setActive(true);
                    }
                }
            }    
        }
    }
    
    public void randomCards(int n) {
        for (int i = 0; i < 6; ++i) { //seis jugadores
            Card[] inpCards = new Card[n];

            for (int j = 0; j < n; ++j) {
                Random rnd = new Random();
                int suit = rnd.nextInt(4);
                int number = rnd.nextInt(13);
                
                while(!deck[suit][number].getActive()) {
                    rnd = new Random();
                    suit = rnd.nextInt(4);
                    number = rnd.nextInt(13);
                }
                
                inpCards[j] = new Card(deck[suit][number].getNumber(), deck[suit][number].getSuit());
                removeCard(inpCards[j]);
            }
            
            players[i] = new Player(Integer.toString(i+1), inpCards);
        }
    }
    
     public void randomBoardCards() {
        Random rnd = new Random();

        Card[] inpCards = new Card[5];

        for (int j = 0; j < 5; ++j) {
            int suit = rnd.nextInt(4);
            int number = rnd.nextInt(13);

            while(!deck[suit][number].getActive()) {
                rnd = new Random();
                suit = rnd.nextInt(4);
                number = rnd.nextInt(13);
            }

            inpCards[j] = new Card(deck[suit][number].getNumber(), deck[suit][number].getSuit());
            removeCard(inpCards[j]);
        }

        cards_common = inpCards;
    }
     
    public Card[] getCommonCards(){
        return cards_common;
    }
    
    public Player[] getPlayers(){
        return players;
    }
    
     
    public void resetCard() {
        if (players == null) return;
        
        String suitOrder = "hdcs";
        
        for (Player p : players) {
            if (p != null)
                for (Card c : p.getCards()) {
                    deck[suitOrder.indexOf(c.getSuit())][numberOrder.indexOf(c.getNumber())].setActive(true);
                }
        }
        players = new Player[6];
    }
    
    public void resetBoardCard() {
        if (cards_common == null) return;
        
        String suitOrder = "hdcs";
        
        for (Card c : cards_common) {
            if (c != null)
                deck[suitOrder.indexOf(c.getSuit())][numberOrder.indexOf(c.getNumber())].setActive(true);
        }
        cards_common = new Card[5];
    }
        
    private void generateCombinations(List<Card> cFull, int k, List<List<Card>> result) {
        if (k == 5) {
            for (int i = 0; i < cFull.size(); ++i) {
                for (int j = i+1; j < cFull.size(); ++j) {
                    for (int m = j+1; m < cFull.size(); ++m) {
                        for (int n = m+1; n < cFull.size(); ++n) {
                            for (int o = n+1; o < cFull.size(); ++o) {
                                List<Card> c = new ArrayList<>();
                                c.add(cFull.get(i));
                                c.add(cFull.get(j));
                                c.add(cFull.get(m));
                                c.add(cFull.get(n));
                                c.add(cFull.get(o));
                                result.add(c);
                            }
                        }
                    }
                }
            }
        }
        else if (k == 2) {
            for (int i = 0; i < cFull.size(); ++i) {
                for (int j = i+1; j < cFull.size(); ++j) {
                    List<Card> c = new ArrayList<>();
                    c.add(cFull.get(i));
                    c.add(cFull.get(j));
                    result.add(c); 
                }
            }
        }
        else if (k == 1) {
            for (int i = 0; i < cFull.size(); i++) {
                List<Card> cards  = new ArrayList<>();
                cards.add(cFull.get(i));
                result.add(cards);
            }    
        }       
    }
    
    private Card getRandomCard(List<Card> cFull) {
        Random rnd = new Random();
        int pos = rnd.nextInt(0, cFull.size());

        while(!cFull.get(pos).getActive()) {
            rnd = new Random();
            pos = rnd.nextInt(0, cFull.size());
        }
        
        cFull.get(pos).setActive(false);
        
        return cFull.get(pos);
    }
    
    private void reset(List<Card> cFull) {
        for (int i = 0; i < cFull.size(); i++) {
            if (!cFull.get(i).getActive()) cFull.get(i).setActive(true);
        }
    }
    
    public void resetEquity() {
        for (int i = 0; i < players.length; ++i) {
            players[i].reset();
        }
    }
    
    public void foldPlayer(String id) {
        for (int i = 0; i < players.length; ++i) {
            if (players[i].getID().equals(id)){
                players[i].setFold(true);
                break;
            }
        }
    }
   
    public void equity(int indice) {
        
        resetEquity();
        
        List<List<Card>> result = new ArrayList<>();
        
        List<Card> cFull = new ArrayList<>();
        int aux = 0;
                
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                if(deck[i][j].getActive()) {
                    cFull.add(deck[i][j]);
                }
            }
        }
        
        generateCombinations(cFull, 5-indice, result);
        
        int sizeres = result.size();
        
        Card[][] res_final;
        
        if (indice == 5) {
            res_final = new Card[1][5];
            
            for (int k = 0; k < 5;++k) {
                res_final[0][k] = cards_common[k];
            }
        }
        else {
            res_final = new Card[result.size()][5];
            
            int i = 0, j = 0; 
            
            for (List<Card> l : result) {
                j = 0;
                if (cards_common != null)
                    for (int k = 0; k < indice;++k) {
                        res_final[i][j] = cards_common[k];
                        j++;
                    }
                for (Card c : l) {
                    res_final[i][j] = c;
                    j++;
                }
                i++;
            }
        }
             
  
        for (Card[] r : res_final) {
            int high_priority = 12;
            
            for (Player p : players) {
                if (!p.getFold()) {
                    int aux_prio = getPriorityPlayer(r,p,2);
                    if (aux_prio < high_priority)
                        high_priority = aux_prio;
                }
            }
            
            List<Integer> index_p = new ArrayList<>();
            for (int k = 0; k < players.length; k++) {
                if( !players[k].getFold()){
                if (players[k].getPriority() == high_priority){
                    index_p.add(k);
                }}
            }
            
            char[] high_card = null;
            for (int k : index_p) {
                if ( !players[k].getFold()){
                char[] card_player = players[k].getHighCard();
                
                if (high_card == null) {
                    high_card = card_player;
                }
                else {
                    for (int n = 0; n < high_card.length; n++) {
                        if (numberOrder.indexOf(high_card[n]) < numberOrder.indexOf(card_player[n])) {
                            high_card = card_player;
                            break;
                        }
                        else if (numberOrder.indexOf(high_card[n]) > numberOrder.indexOf(card_player[n])) //no comprueba más
                            break;
                    }
                }
                }
            }
            
            List<Integer> auxIndex_p = new ArrayList<>();
            
            for (int k : index_p) {
                auxIndex_p.add(k);
            }
            
            for (int k = 0; k < auxIndex_p.size(); ++k) {
                char[] card_player = players[auxIndex_p.get(k)].getHighCard();

                for (int n = 0; n < high_card.length; n++) {
                    if (high_card[n] != card_player[n]) {
                        index_p.remove(auxIndex_p.get(k));
                        break;
                    }
                }
            }
            
            float points = 1;
            if (index_p.size() != 0) {
                points = points/index_p.size();
            }
            
            for (int k = 0; k < index_p.size(); ++k) { 
                players[index_p.get(k)].setWin(points);
            }
            
            if (index_p.size() != 0) {aux++;}
        }  
        
        for (Player p : players) {
            p.setEquity((p.getWin()/aux)*100);
        } 
        
        return;
    }
    
    public void equityOmaha(int indice) {
        resetEquity();
        int a = indice;
        List<List<Card>> result = new ArrayList<>();
        List<Card> cFull = new ArrayList<>();
        int aux = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                if (deck[i][j].getActive()) {
                    cFull.add(deck[i][j]);
                }
            }
        }
        /*
        for (int i = cards_common.length - indice - 1; i >= 0; i--) {
            cFull.add(cards_common[i]);
        }*/
        
        for (Player p : players) {
            if ( !p.getFold()){
                Card[] playerCards = p.getCards();
                for (int i = 0; i < 3; i++) {
                    cFull.add(playerCards[i]);

                    for (int j = i; j < 4; j++) {
                        if (i != j) {
                            cFull.add(playerCards[j]);
                        }

                        generateCombinations(cFull, 5, result);

                        cFull.remove(playerCards[j]);
                    }
                    cFull.remove(playerCards[i]);
                }
            }
        }
        
        Card[][] res_final;

        if (indice == 5) {
            res_final = new Card[1][5];

            for (int k = 0; k < 5;++k) {
                res_final[0][k] = cards_common[k];
            }
        }
        else {
            res_final = new Card[result.size()][5];

            int i = 0, j = 0; 

            for (List<Card> l : result) {
                j = 0;
                if (cards_common != null)
                    for (int k = 0; k < indice;++k) {
                        res_final[i][j] = cards_common[k];
                        j++;
                    }
                for (Card c : l) {
                    res_final[i][j] = c;
                    j++;
                }
                i++;
            }
        }

        for (Card[] r : res_final) {
            int high_priority = 12;
            
            for (Player p : players) {
                if (!p.getFold()){
                int aux_prio = getPriorityPlayer(r,p,2);
                if (aux_prio < high_priority){
                    high_priority = aux_prio;}
                }
            }
            
            List<Integer> index_p = new ArrayList<>();
            for (int k = 0; k < players.length; k++) {
                if (!players[k].getFold()){
                    if (players[k].getPriority() == high_priority){
                        index_p.add(k);
                    }
                }
            }
            
            char[] high_card = null;
            for (int k : index_p) {
                if (!players[k].getFold()){
                    char[] card_player = players[k].getHighCard();

                    if (high_card == null) {
                        high_card = card_player;
                    }
                    else {
                        for (int n = 0; n < high_card.length; n++) {
                            if (numberOrder.indexOf(high_card[n]) < numberOrder.indexOf(card_player[n])) {
                                high_card = card_player;
                                break;
                            }
                            else if (numberOrder.indexOf(high_card[n]) > numberOrder.indexOf(card_player[n])) //no comprueba más
                                break;
                        }
                    }
                }
            }
            
            List<Integer> auxIndex_p = new ArrayList<>();
            
            for (int k : index_p) {
                auxIndex_p.add(k);
            }
            
            for (int k = 0; k < auxIndex_p.size(); ++k) {
                char[] card_player = players[auxIndex_p.get(k)].getHighCard();

                for (int n = 0; n < high_card.length; n++) {
                    if (high_card[n] != card_player[n]) {
                        index_p.remove(auxIndex_p.get(k));
                        break;
                    }
                }
            }
            
            float points = 1;
            if (index_p.size() != 0) {
                points = points/index_p.size();
            }
            
            for (int k = 0; k < index_p.size(); ++k) { 
                players[index_p.get(k)].setWin(points);
            }
            
            if (index_p.size() != 0) {aux++;}
            a = index_p.size();
        } 
        
        for (Player p : players) {
            p.setEquity((p.getWin()/aux)*100);
        } 
        
        return;
    }
    
    
    private int getPriorityPlayer(Card[] board, Player pl, int nCards) {
                
        List<List<Card>> result = new ArrayList<>();
  
        List<Card> cFull = new ArrayList<>();
       
        for (Card c : pl.getCards()) {
            cFull.add(c);
        }
        
        for (Card c : board) {
            cFull.add(c);
        }
        
        generateCombinations(cFull, 5, result);
        
        Card[][] res_final = new Card[result.size()][5];
        
        int i = 0, j = 0; 
        for (List<Card> l : result) {
            j = 0;
            for (Card c : l) {
                res_final[i][j] = c;
                j++;
            }
            i++;

        }
        
        int high_prio = 12;
        char[] high_card = null;
        for (Card[] r : res_final) { 
            Player p = new Player(r);            
            p.getBestHand();
           
            if (high_prio > p.getPriority() || high_prio == 12) {
                high_prio = p.getPriority(); 
                high_card = p.getHighCard();
            }
            else if (high_prio == p.getPriority()) {
                char[] card_player = p.getHighCard();

                for (int n = 0; n < high_card.length; n++) {
                    if (numberOrder.indexOf(high_card[n]) < numberOrder.indexOf(card_player[n]))
                        high_card = card_player;
                    else if (numberOrder.indexOf(high_card[n]) > numberOrder.indexOf(card_player[n])) //no comprueba más
                        break;
                }
            }
        }
     
        pl.setPriority(high_prio);
        pl.setHighCard(high_card);
        return high_prio;
    }
    
    public void setOmaha(boolean b) { omaha = b; }
    public boolean getOmaha() { return omaha; }
}
