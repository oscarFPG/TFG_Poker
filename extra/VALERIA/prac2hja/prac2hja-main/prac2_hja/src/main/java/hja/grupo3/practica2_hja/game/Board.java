package hja.grupo3.practica2_hja.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Board {
    
    private final String numberOrder = "AKQJT98765432";
    private String[][] pairs;
    private String[][] pairsBoard;
    private Combos combos;
    private int nCombos;
    private Map<String, Integer> mapCombos;
    
    public Board(){};
  
    public void readRange (String range_str) {
        if (range_str.length() == 0) {
            pairs = new String[0][0];
        }
       
        else {
            String[] input = new String[range_str.length()/2];
            input = range_str.split(",");

            pairs = new String[range_str.length()/2][13];

            // PROCESAR CADA ENTRADA PARA VER QUE PAREJAS HAY QUE COLOREAR DE AMARILLO
            for (int i = 0; i < input.length ; i++) {
                // CARD1 CARD2 SUIT - CARD1 CARD3 SUIT
                if (input[i].length() > 4) { 
                    addRange(input[i].charAt(1), input[i].charAt(5),input[i].charAt(0), i, input[i].charAt(2)); 
                }
                
                // CARD CARD SUIT +
                else if (input[i].length() == 4) {
                    char suited = input[i].charAt(2);
                    char common = input[i].charAt(0);

                    addRange(common, input[i].charAt(1), common, i, suited);
                }
                
                // CARD CARD +
                else if ((input[i].length() == 3)&&(isEqual(input[i].charAt(0),input[i].charAt(1)))) {
                    addRangeEq(input[i], i);
                }
                
                // CARD1 CARD2
                else { pairs[i][0] = input[i]; }
            }
        }
    }
    
    boolean isEqual (char a, char b) {
        if (a == b) {return true;}
        
        return false;
    }
    
    private void addRangeEq (String range, int ind) {
        int ind2 = 0;
        int i = 0;
        
        while (ind2 <= numberOrder.indexOf(range.charAt(0))) {
            pairs[ind][i] = String.format("%c%c",numberOrder.charAt(ind2), numberOrder.charAt(ind2));
            i++; 
            ind2++;
        } 
    }
    
    private void addRange(char n1, char n2, char common, int ind, char suited) {
        int i = 0;
        int ind1 = numberOrder.indexOf(n1);
        int ind2 = numberOrder.indexOf(n2);
            
        while (ind1 <= ind2) {
                pairs[ind][i] = String.format("%c%c%c",common, numberOrder.charAt(ind1), suited);
                i++;
                ind1++;
        }
    }

    public String[][] getPairs() { return pairs; }
    
    public void readBoard (String range_str) {
       // String range = range_str.substring(1);
        
        if (range_str.length() == 0) {
            pairsBoard = new String[0][0];
        }
       
        else {
            String[] input = new String[range_str.length()/2];
            input = range_str.split(",");

            pairsBoard = new String[range_str.length()/2][13];

            // PROCESAR CADA ENTRADA PARA VER QUE PAREJAS HAY QUE COLOREAR DE AMARILLO
            for (int i = 0; i < input.length ; i++) {
                pairsBoard[i][0] = input[i];
            }
        }   
        return;
    }
    
    public String[][] getPairsBoard() { return pairsBoard; }
    
       
    public void infoToCombos() {
        List<String> ranges = new ArrayList<>();
        List<String> boardRanges = new ArrayList<>();
        
        for (String[] sl : pairs) {
            for (String s : sl) 
                if (s != null) ranges.add(s);
        }
        
        for (String[] sl : pairsBoard) {
            for (String s : sl) 
                if (s != null) boardRanges.add(s);
        }
                
        combos = new Combos(ranges, boardRanges);

        if (boardRanges.size() >= 3 && boardRanges.size() <= 5) {
            combos.countTotal();
            combos.countComb();
        }
        
        nCombos = combos.getTotalComb();
        mapCombos = combos.getComb();
    }
    
    public int getNComb() { return nCombos; }
    public Combos getCombos() { return this.combos; }
    public Map<String, Integer> getMapComb() { return mapCombos; }
    
}
