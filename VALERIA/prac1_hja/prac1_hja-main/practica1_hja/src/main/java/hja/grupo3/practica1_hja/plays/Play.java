package hja.grupo3.practica1_hja.plays;

import hja.grupo3.practica1_hja.game.Card;

public abstract class Play { 
    protected String name; // --> Nombre de la jugada
    protected String draw_name; // --> nombre del draw
    public abstract boolean checkPlay(Card cards[]);  
    public abstract boolean checkDraw(Card cards[]);  // 0 no hay draw, 1 open-ended, 2 gutshot, 3 ya había jugada pero se cumple otra jugada con menor prioridad
    public abstract char getHighCard(); //hay jugadas que es necesario esto para saber si hay empate cual es el ganador
    
    public String NumbertoString(char number) {
        switch(number){
            case 'A':
                return "Aces";
            case 'K':
                return "Kings";
            case 'Q':
                return "Queens";
            case 'J':
                return "Jacks";
            case 'T':
                return "Tens";
            case '9':
                return "Nines";
            case '8':
                return "Eights";
            case '7':
                return "Sevens";
            case '6':
                return "Sixes";
            case '5':
                return "Fives";
            case '4':
                return "Fours";
            case '3':
                return "Threes";
            case '2':
                return "Twos";
            default:
                return "";
        }
    }
    
    public String cardstoString(Card cards[]) {
        String str = "";
        for (Card c : cards){
            if (c.getSol()){
                str += c.getNumber();
                str += c.getSuit();
            }
        }
        return str;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDrawName() {
        return this.draw_name;
    }
    
    public boolean isOmaha(Card cards[]) {
        int countCommon = 0, countPlayer = 0;
        
        for (int i = 0; i < 4; i++) {
            if (cards[i].getSol()) { countPlayer++; }
        }
        
        for (int j = 4; j < cards.length ;j++) {
            if (cards[j].getSol()) { countCommon++; }
        }
        
        return ((countPlayer == 2)&&(countCommon == 3));
    }
}
