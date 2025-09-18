package hja.grupo3.practica2_hja;

import hja.grupo3.practica2_hja.game.Board;
import hja.grupo3.practica2_hja.view.MainFrame;
import java.io.IOException;

public class Main {
    // Attributes
    
    private static Board board;
    
   
     public static void error(int op){
        if (op ==1) {System.out.println("El numero de parametros introducido es incorrecto.");}
        if (op ==2) {System.out.println("Error reading file");}
        if (op ==2) {System.out.println("Error writing in file.");}
    }
            
    public static void main(String[] args) throws IOException {
        Board board = new Board();
        
        MainFrame mainFrame = new MainFrame(board);
        mainFrame.setVisible(true);
        
    }
}