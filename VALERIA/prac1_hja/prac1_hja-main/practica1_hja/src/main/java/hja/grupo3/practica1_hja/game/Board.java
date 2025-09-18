package hja.grupo3.practica1_hja.game;

import hja.grupo3.practica1_hja.misc.CardComparatorNumber;
import hja.grupo3.practica1_hja.misc.HandComparator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Board {
    
    //---------ERRORES------------
    public static void error(int op){
        if (op ==1) {System.out.println("El numero de parametros introducido es incorrecto.");}
        if (op ==2) {System.out.println("Error reading file");}
        if (op ==2) {System.out.println("Error writing in file.");}
    }
    private static final char[] SUITS = new char[] {'h','d','c','s'};
    private static final char[] NUMBERS = new char[] {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};
    //------------------------------
    
    
    //------ LECTURA -------
    private String m_cards_str;
    private String m_common_str;
    private int m_nPlayer;
    private int m_nCommon;
    private char[][] m_cards;
    private char[] m_common_cards;
    //-------------------------
    
    
    //--------LOGICA--------------
    private char[][] cards_infile;
    private char[] common_cards_infile;
    private int option;
    private int nCommon;
    private int nPlayer;
    private Card[] cards;
    private Card[] cards_common;
    private boolean river = false;
    private boolean omaha = false;
    private Player[] players;
    private Player[] playersAntiguo;
    private boolean thirdOp = false;
   //-------------------------------
    
    
    public Board(){};
    
    public String run(){
        switch (option) {
            case 1:
                return firstOption();
            case 2:
                return secondOption();
            case 3:
                thirdOp = true;
                return thirdOption();
            case 4:
                return omaha();
            default:
                break;
        }
        return null;
    }
    
    public String firstOption(){
        StringBuilder str = new StringBuilder();
        
        str.append(cards_infile[0]);
        
        str.append("\n");
        
        initializeCards();
        Player p1 = new Player(cards);
        str.append(p1.getBestHand());
        str.append(p1.getDraws());
        
        str.append("\n");

        return str.toString();
    }
    
    public String secondOption(){       
        StringBuilder str = new StringBuilder();
        
        str.append(cards_infile[0]);
        str.append(";");        
        str.append(nCommon);
        str.append(";");
        str.append(common_cards_infile);
        
        str.append("\n");
        
        initializeCards();
        initializeCardsCommon();
        
        str.append(combination());
        
        str.append("\n");
        
        return str.toString();
    }
    
    public String thirdOption(){
        StringBuilder str = new StringBuilder();
        
        
        guardarCartasAntiguas();
        initializeCards(); 
        initializeCardsCommon();
        
        str.append(nPlayer);
        
        for (int i = 0; i < cards.length; ++i){
            if (i % 2 == 0) {
                str.append(String.format(";J%d", i/2+1));  
              
            }
            str.append(String.format("%s%s", cards[i].getNumber(),cards[i].getSuit()));
        }
        
        str.append(";");              
        str.append(common_cards_infile);
        str.append("\n");
        
        
        int j = 0;
        for (int i = 0; i < cards.length; i+=2){
            Card[] c = combinationPlayer(i);
            players[j] = new Player(String.format("J%d", i/2+1), c);
            players[j].getBestHand();
            j++;
        }
        
        HandComparator playersComparator = new HandComparator();
        Arrays.sort(players, playersComparator);
       
        str.append(bestHandPlayer());    
        str.append("\n");

        return str.toString();
    }
    
    public String omaha () {
        omaha = true;
        
        StringBuilder str = new StringBuilder();
        
        str.append(cards_infile[0]);
        str.append(";");        
        str.append(nCommon);
        str.append(";");
        str.append(common_cards_infile);
        
        str.append("\n");
        
        initializeCards();
        initializeCardsCommon();
        
        str.append(combination());

        str.append("\n");
        
        return str.toString();
    }
     
    public void initializeCards(){
        int j = 0;

        for (int k = 0; k < nPlayer; k++){
             for ( int i = 0; i < cards_infile[k].length ; i+=2){
               cards[j] = new Card(cards_infile[k][i], cards_infile[k][i+1]);
                j++;
            }
        }
        
    }
    
    public void guardarCartasAntiguas(){
         int j = 0;
        for (int k = 0; k < nPlayer; k++){
            Card[] aux =  new Card[2];
             for ( int i = 0; i < cards_infile[k].length ; i+=2){
                aux[j] = new Card(cards_infile[k][i], cards_infile[k][i+1]);
                j++;
            }
            this.playersAntiguo[k] = new Player(Integer.toString(k+1), aux);
            j = 0;
        }
    }
    
    private void initializeCardsCommon(){
        int j = 0;
        for ( int i = 0; i < common_cards_infile.length ; i+=2){
            cards_common[j] = new Card(common_cards_infile[i], common_cards_infile[i+1]);
            j++;
        }   
    }
    
    
    private String combination() {
        List<List<Card>> result = new ArrayList<>();
        
        List<Card> cFull = new ArrayList<>();
        
        for(Card ca : cards){
            cFull.add(ca);
        }
        for (Card ca : cards_common){
            cFull.add(ca);
        }
        
        
        generateCombinations(cFull, 5, 0, new ArrayList<>(), result);
        
        StringBuilder str_sol = new StringBuilder();
        Card[] card_sol = new Card[5];    
        int high_prio = 12;
        
        for (List<Card> r : result) {
            Card[] c = listToArray(r);
            
            Player p1 = new Player(c);
            StringBuilder str = new StringBuilder();
            
            p1.getBestHand();
            
            if (!omaha) {
                str.append(String.format("- Best Hand: %s with %s\n", p1.getNamePlay(), cardstoString(c)));           
            }
            
            else {
                str.append(String.format("%s with %s\n", p1.getBestOmaha().split(" \\(")[0], cardstoString(c)));
            }
            
            if (!river) str.append(p1.getDraws());
            
            if (high_prio > p1.getPriority()) {
                high_prio = p1.getPriority();
                str_sol = str;
                card_sol = c;                
            } 
        }
        
        return str_sol.toString();
    }
    
    
    private Card[] combinationPlayer(int ini){
        List<List<Card>> result = new ArrayList<>();
        
        List<Card> c = new ArrayList<>();
        
        for(int i = ini; i < ini+2; ++i){ //solo coges dos porque cada jugador tiene dos cartas
            c.add(cards[i]);
        }
        for (Card ca : cards_common){
            c.add(ca);
        }
                
        generateCombinations(c, 5, 0, new ArrayList<>(), result);

        Card[] card_sol = new Card[5];
        int high_prio = 12;

        for (List<Card> r: result){
            Card[] cAux = listToArray(r);

            Player p = new Player(cAux);
            
            if (!omaha){
               p.getBestHand();
                if (!river) p.getDraws();
            } else {
                p.getBestOmaha();
            }

            if (high_prio > p.getPriority()) {
            high_prio = p.getPriority();
            card_sol = cAux;                
            }
        }
        return card_sol;
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
   
    private String bestHandPlayer(){
        StringBuilder str = new StringBuilder();
        int i, j = 0;
   
        for (Player p: players){ 
            str.append(String.format("%s: %s (%s)\n", p.getID(), cardstoString(p.getCardsSol()), p.getNamePlay()));
        }
     
        return str.toString();
    }
    
    private String cardstoString(Card ca[]) {
        String str = "";
        for (Card c : ca){
            str += c.getNumber();
            str += c.getSuit();
        }
        return str;
    }
    
    private Card[] listToArray(List<Card> l) {
        Card[] sol = new Card[5];
        for (int i = 0; i < 5; ++i) {
            sol[i] = l.get(i);
        }
        return sol;
    }
    
    public void setOption(int op){
        this.option = op;
    }
           
    public Card[] getCommonCards(){
        return cards_common;
    }
    public Card[] getPlayerCards(){
        return cards;
    } 
    
    public Player[] getPlayers(){
        return players;
    }
    
    public Player[] getplayersAntiguos(){
        return playersAntiguo;
    }
    
    //------------------LECTURA
    public void setValues() {
        this.cards_infile = m_cards;
        
        this.common_cards_infile = m_common_cards;
        
        this.nCommon = m_nCommon;
        if (this.nCommon == 5) river = true;
                
        if (option != 3)
            this.cards = new Card[m_cards[0].length/2];
        else 
            this.cards = new Card[m_nPlayer*2];
        
        this.cards_common = new Card[m_nCommon];
        
        this.nPlayer = m_nPlayer;
        this.players = new Player[this.nPlayer];
       
        this.playersAntiguo = new Player[this.nPlayer];
       
    }
   
    
     public void loadData(int option, String inFile, String outFile) throws IOException{
        
        setOption(option);
        File oldfile = new File(outFile);
        oldfile.delete();
        File file = new File(outFile);

        FileWriter fw = new FileWriter(outFile,true);

        InputStream in = new FileInputStream(new File(inFile));
        Scanner scanner = new Scanner(in);

        switch(option) {
            case 1:
                option1(file, fw, in, scanner);
            case 2:
                option2(file, fw, in, scanner);
            case 3:
                option3(file, fw, in, scanner);
            case 4:
                option2(file, fw, in, scanner);
            default:
                break;
        }
    }
     
    public void option1(File file, FileWriter fw, InputStream in, Scanner scanner) throws IOException {
        while (scanner.hasNextLine()) {
            m_cards_str = scanner.nextLine();
            m_nPlayer = 1;
            m_cards = new char[m_nPlayer][m_cards_str.length()];
            try {
                if (!readPlayerCards(0,0)) {error(2);}
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.setValues();
            writeFile(file, fw);
        }
        
        fw.close();
    }
     
    public void option2(File file, FileWriter fw, InputStream in, Scanner scanner) throws IOException {
        String[] input = new String[3];
        while (scanner.hasNextLine()) {
            input = scanner.nextLine().split(";");
            m_cards_str =  input[0];
            m_nCommon = Integer.parseInt(input[1]);
            m_common_str = input[2];
            m_nPlayer = 1;
            m_cards = new char[m_nPlayer][m_cards_str.length()];
            m_common_cards = new char[m_nCommon*2];
            try {
                if (!readPlayerCards(0,0) || !readCommonCards()) {error(2);}
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.setValues();
            writeFile(file, fw);
        }
        
        fw.close();
    }
    
    public void option3(File file, FileWriter fw, InputStream in, Scanner scanner) throws IOException {
        //int nPlayers;
        String[] input = new String[11]; // 9 players (max) + common cards + nPlayers
        while (scanner.hasNextLine()) {
            input = scanner.nextLine().split(";");
            m_nPlayer =  Integer.parseInt(input[0]);
            m_cards = new char[m_nPlayer][4];
            
            // Common cards
            m_nCommon = 5;
            m_common_str = input[m_nPlayer+1];
            m_common_cards = new char[m_nCommon*2];
            try {
                if (!readCommonCards()) {error(2);}
            } catch (FileNotFoundException ex) {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
            for(int i = 0; i < m_nPlayer; i++) {
                m_cards_str = input[i+1];
                try {
                    if (!readPlayerCards(i, 2)) {error(2);}
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
     
            this.setValues();
            writeFile(file, fw);
        }
        
        fw.close();
    
    }
    
     public boolean readPlayerCards(int n, int i) throws FileNotFoundException {   
        int j = 0;
        while(i < m_cards_str.length()) {
            if (numberCorrect(m_cards_str.charAt(i))){m_cards[n][j] = m_cards_str.charAt(i);} else return false;
            if (suitCorrect(m_cards_str.charAt(i+1))){m_cards[n][j+1] = m_cards_str.charAt(i+1);} else return false;
            i = i + 2;
            j += 2;
        }    
        
        return true;
    }
     
     public boolean readCommonCards() throws FileNotFoundException {
        int i = 0;
        while(i < m_nCommon*2) {
            if (numberCorrect(m_common_str.charAt(i))){m_common_cards[i] = m_common_str.charAt(i);} else return false;
            if (suitCorrect(m_common_str.charAt(i+1))){m_common_cards[i+1] = m_common_str.charAt(i+1);} else return false;
            i = i + 2;
        }    
        return true;
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
    
    public void writeFile(File file, FileWriter fw) {
        try {
            if (file.exists()) {    
                fw.write(this.run());
            }
        }
        catch (IOException io) {
            error(3);
            io.printStackTrace();   
        }
    }
    
}
