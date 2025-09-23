/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hja.grupo3.practica1_hja.misc;

import hja.grupo3.practica1_hja.game.Player;
import java.util.Comparator;

/**
 *
 * @author sofia
 */
public class HandComparator implements Comparator<Player> {
     private final String numberOrder;
     
    public HandComparator(){
        this.numberOrder = "23456789TJQKA";
    }
    
     @Override
    public int compare(Player P1, Player P2) {
            
        int numberComparison = P1.getPriority() - P2.getPriority();
        
        if (numberComparison == 0) {
            return numberOrder.indexOf(P2.getHighCard()) - numberOrder.indexOf(P1.getHighCard());
        }
        
        return numberComparison;
      
    }
    
    
}
