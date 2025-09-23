package hja.grupo3.practica1_hja;

import hja.grupo3.practica1_hja.game.Board;
import hja.grupo3.practica1_hja.view.MainWindow;
import java.io.IOException;

public class Main {
    // Attributes
    private static int option;
    private static String inFile = null;
    private static String outFile = null;
    private static Board board;
   
     public static void error(int op){
        if (op ==1) {System.out.println("El numero de parametros introducido es incorrecto.");}
        if (op ==2) {System.out.println("Error reading file");}
        if (op ==2) {System.out.println("Error writing in file.");}
    }
            
    public static void main(String[] args) throws IOException {
        if (args.length == 0){
            startGUI();
        }
        else{
            startConsole(args);
        }
        
    }
    
    public static void startConsole(String[] args) throws IOException {
        Board board = new Board();
        if (args.length != 3) {
            error(1);
        } else {
            option = Integer.parseInt(args[0]);
            inFile = args[1];
            outFile = args[2];
            board.loadData(option, inFile, outFile);
        }          
    }
    
    public static void startGUI(){
        Board board = new Board();
        MainWindow mainWindow = new MainWindow(board);
        mainWindow.setVisible(true);
    }
}