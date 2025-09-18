package hja.grupo3.practica2_hja.game;

import hja.grupo3.practica2_hja.misc.CardComparatorNumber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Laura
 */
public class Combos {
    
    private String[] plays = {"royal flush", "str flush", "poker", "full house",
                      "flush", "straight", "3 of a kind", "two pair", "overpair", 
                      "top pair", "pp below tp", "middle pair", "weak pair", 
                      "no made hand"};
    
    private Map<String, Integer> combos;
    private Map<String, List<List<Card>>> rangeComb;
    
    private int nCombos = 0;
    
    private List<String> ranges;
    private List<String> boardRang;
    
    private final String numberOrder = "23456789TJQKA";
    
    public Combos(List<String> ranges, List<String> boardRang) { 
        
        combos = new HashMap<>();
        rangeComb = new HashMap<>();
        
        this.ranges = ranges;
        this.boardRang = boardRang;
        
        for (String n : plays) {
            combos.put(n, 0);
        }
        
        for (String n : ranges) {
            rangeComb.put(n, new ArrayList<>());
        }
               
        
    }
    
    public void countTotal() {
        for (String r : ranges) {
            if (r.length() == 2) { //parejas
                int comb = 0;
                List<List<Card>> lAux = new ArrayList<>();
                for(int i = 0; i < 4; ++i){
                    for (int j = i+1; j < 4; ++j){
                        if(!boardRang.contains(String.format("%s%s", r.charAt(0),Suit.getSuit(i))) && 
                                !boardRang.contains(String.format("%s%s", r.charAt(1),Suit.getSuit(j)))) {
                            comb++;
                           
                            Card c1 = new Card(r.charAt(0),Suit.getSuit(i));
                            Card c2 = new Card(r.charAt(1),Suit.getSuit(j));

                            List<Card> c = new ArrayList<>();
                            c.add(c1);
                            c.add(c2);
                            
                            lAux.add(c);
                        }
                    }
                }
                nCombos += comb;
                rangeComb.replace(r, lAux);
            }
            else if (r.contains("s") && r.charAt(0) != r.charAt(1)) {
                int comb = 0;
                List<List<Card>> lAux = new ArrayList<>();
                for(int i = 0; i < 4; ++i){
                    if(!boardRang.contains(String.format("%s%s", r.charAt(0),Suit.getSuit(i))) && 
                            !boardRang.contains(String.format("%s%s", r.charAt(1),Suit.getSuit(i)))) {
                        comb++;

                        Card c1 = new Card(r.charAt(0),Suit.getSuit(i));
                        Card c2 = new Card(r.charAt(1),Suit.getSuit(i));

                        List<Card> c = new ArrayList<>();
                        c.add(c1);
                        c.add(c2);

                        lAux.add(c);
                    }
                }
                nCombos += comb;
                rangeComb.replace(r, lAux);
            }
            else if (r.contains("o") && r.charAt(0) != r.charAt(1)) {
                int comb = 0;
                List<List<Card>> lAux = new ArrayList<>();
                for(int i = 0; i < 4; ++i){
                    for (int j = 0; j < 4; ++j){
                        if( i != j && !boardRang.contains(String.format("%s%s", r.charAt(0),Suit.getSuit(i))) && 
                                !boardRang.contains(String.format("%s%s", r.charAt(1),Suit.getSuit(j)))) {
                            comb++;
                           
                            Card c1 = new Card(r.charAt(0),Suit.getSuit(i));
                            Card c2 = new Card(r.charAt(1),Suit.getSuit(j));

                            List<Card> c = new ArrayList<>();
                            c.add(c1);
                            c.add(c2);

                            lAux.add(c);
                        }
                    }
                }
                nCombos += comb;
                rangeComb.replace(r, lAux);
            }
        }
    }
    
    public void countComb(){
        
        List<Card> cardBoard = new ArrayList<>();
        
        for(String s : boardRang) {
            cardBoard.add(new Card(s.charAt(0), s.charAt(1)));
        }
        
        for(String entry : rangeComb.keySet()) {
            
            for (List<Card> ls : rangeComb.get(entry)) {               
                List<Card> l = new ArrayList<>();

                for (Card c : ls) 
                    l.add(c);
                
                for (Card c : cardBoard)
                    l.add(c);

                List<List<Card>> result = new ArrayList<>();

                generateCombinations(l, 5, 0, new ArrayList<>(), result);

                int high_prio = 12;
                List<Card> card_sol = new ArrayList<>();

                for (List<Card> r: result){
                    Card[] cAux = listToArray(r);

                    Player p = new Player(cAux);

                    p.getBestHand();

                    if (high_prio > p.getPriority()) {
                        high_prio = p.getPriority();  
                        card_sol = r;
                    }
                }
                
                addComb(high_prio, ls, card_sol, cardBoard);
            }
        }        
    }
    
    private void generateCombinations(List<Card> c, int k, int ind, List<Card> currentCombination, List<List<Card>> result) {
        if (k == 0) {
            List<Card> sortedCombination = new ArrayList<>(currentCombination);
            
            CardComparatorNumber customComparator = new CardComparatorNumber();
            sortedCombination.sort(customComparator);

            if (!result.contains(sortedCombination)) {
                result.add(sortedCombination);
            }
            return;
        }

        for (int i = ind; i < c.size(); i++) {
            currentCombination.add(c.get(i));
            generateCombinations(c, k - 1, i + 1, currentCombination, result);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }
    
    private Card[] listToArray(List<Card> l) {
        Card[] sol = new Card[5];
        for (int i = 0; i < 5; ++i) {
            sol[i] = l.get(i);
        }
        return sol;
    }
    
    private void addComb(int prio, List<Card> l, List<Card> result, List<Card> cardBoard) {
        int n = 0;
        
        if (prio == 8) {
            if (l.get(0).getNumber() == l.get(1).getNumber()) prio = 9;
            else {
                int c = 0;
                for (int i = 0; i < cardBoard.size() && c < 2; ++i) {
                    if (l.get(0).getNumber() == cardBoard.get(i).getNumber() ||
                        l.get(1).getNumber() == cardBoard.get(i).getNumber())
                        c++;
                }
                if (c < 2) prio = 9;
            }
        }
        if (prio == 9) {
            boolean l_pair = false;
            for (int i = 0; i < cardBoard.size() && !l_pair; ++i) {
                if (l.get(0).getNumber() == l.get(1).getNumber() ||
                    l.get(0).getNumber() == cardBoard.get(i).getNumber() ||
                    l.get(1).getNumber() == cardBoard.get(i).getNumber())
                    l_pair = true;
            }
            if(!l_pair) prio = 10;
        }
        
        switch(prio) {
            case 1: //Royal flush
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break;
            case 2: //Straight flush
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break; 
            case 3: //Poker
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break;
            case 4: //Full house
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break;
            case 5: //Flush
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break;
            case 6: //Straight
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break;
            case 7: //3 of a kind
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break;
            case 8: //Two pairs
                n = combos.get(plays[prio-1]) + 1;
                combos.replace(plays[prio-1], n);
                break;
            case 9: //Pair -> distinguir casos
                List<Card> cb = cardBoard;
                cb.sort(new CardComparatorNumber());

                int numberComparison11 = numberOrder.indexOf(l.get(0).getNumber()) - numberOrder.indexOf(cb.get(0).getNumber());
                int numberComparison12 = numberOrder.indexOf(l.get(1).getNumber()) - numberOrder.indexOf(cb.get(0).getNumber());
               
                int numberComparison21 = numberOrder.indexOf(l.get(0).getNumber()) - numberOrder.indexOf(cb.get(1).getNumber());
               
                
                if (numberComparison11 > 0 && numberComparison11 == numberComparison12) { //"overpair" -> pareja en mano mejor que la carta mas alta del board
                    n = combos.get("overpair") + 1;
                    combos.replace("overpair", n);
                }
                else if (l.get(0).getNumber() == cb.get(0).getNumber() || l.get(1).getNumber() == cb.get(0).getNumber()) { //"top pair" -> pareja con la carta mas alta del board
                    n = combos.get("top pair") + 1;
                    combos.replace("top pair", n);
                }
                else if (l.get(0).getNumber() == cb.get(1).getNumber() || l.get(1).getNumber() == cb.get(1).getNumber()) { //"middle pair" -> pareja con la segunda carta mas alta del board
                    n = combos.get("middle pair") + 1;
                    combos.replace("middle pair", n);
                }
                else if (numberComparison11 < 0 && numberComparison11 == numberComparison12 && numberComparison21 > 0) { //"pp below tp" -> pareja en mano con cartas menores que la mas alta del board pero que no es débil
                    n = combos.get("pp below tp") + 1;
                    combos.replace("pp below tp", n);
                }            
                else { //"weak pair" -> otras parejas
                    n = combos.get("weak pair") + 1;
                    combos.replace("weak pair", n);
                }
                    
                break;
            case 10: //No made Hand o High Card
                n = combos.get(plays[plays.length-1]) + 1;
                combos.replace(plays[plays.length-1], n);
                break;
        }
    }
    
    public Map<String, Integer> getComb(){
        return combos;
    }
    
     public Map<String, List<List<Card>>> getRangeComb(){
        return rangeComb;
    }
    public int getTotalComb(){
        return nCombos;
    }
}
