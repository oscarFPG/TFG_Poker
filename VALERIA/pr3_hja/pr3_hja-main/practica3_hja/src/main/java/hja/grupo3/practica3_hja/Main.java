package hja.grupo3.practica3_hja;

import hja.grupo3.practica3_hja.game.Board;
import hja.grupo3.practica3_hja.view.InitialWindow;
import java.io.IOException;

public class Main {
    
     public static void error(int op){
        if (op ==1) {System.out.println("El numero de parametros introducido es incorrecto.");}
        if (op ==2) {System.out.println("Error reading file");}
        if (op ==2) {System.out.println("Error writing in file.");}
    }
            
    public static void main(String[] args) throws IOException {
        startGUI();
    }
    
  
    public static void startGUI(){
        Board board = new Board();
        InitialWindow initialWindow = new InitialWindow(board);
        initialWindow.setVisible(true);
    }
}