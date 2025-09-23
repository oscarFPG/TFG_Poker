/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hja.grupo3.practica1_hja.view;

import hja.grupo3.practica1_hja.game.Card;
import hja.grupo3.practica1_hja.game.Player;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class BoardViewer extends javax.swing.JFrame {

    /**
     * Creates new form BoardViewer
     */
    
    private int numJugadores;
    private Player[] players;
    private Card[] cardsPlayer;
    private Card[] commonCards;
    
    public BoardViewer() {
        initComponents();
        setVisible(true);
        this.setLocationRelativeTo(null);
    }
    
    public BoardViewer(int numJugadores, Player[] players, Card[] cardsPlayer, Card[] commonCards) {
        
        //-----CONSTRUCTOR----
        this.numJugadores = numJugadores;
        this.players = players;
        this.cardsPlayer = cardsPlayer;
        this.commonCards = commonCards;
        initComponents();
        setVisible(true);
        this.setLocationRelativeTo(null);
        
        
        //------FONDO------------
        BackGroundPanel panelFondo = new BackGroundPanel();
        this.setContentPane(panelFondo);
        
        
        //-------CARTAS-----------
        if (players != null){
            dibujarCartasJugadores();
            dibujarCartasMesa();
        }
        else {
            if ( cardsPlayer != null){
            dibujarCartasJugador();
            }
            if ( commonCards != null){
            dibujarCartasMesa();
            }
        }
        
        
        JButton salidaButton = new JButton();
        salidaButton.setText("RESULTADO");
        salidaButton.setBounds(this.getWidth()/2 - 70, this.getHeight()/2, 123, 36);
        salidaButton.setForeground(Color.WHITE);
        salidaButton.setBackground(Color.red);
        getContentPane().add(salidaButton);
        

        salidaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                
                try {
                    InputStream in = new FileInputStream(new File("GUI.txt"));
                    Scanner scanner = new Scanner(in);
                     StringBuilder str = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        String a = scanner.nextLine();
                        
                        str.append(a + "<br>");
                    }
                     resultadoPanel resultadoPanel = new resultadoPanel(str.toString());
                     resultadoPanel.setVisible(true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(BoardViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
        
            }
        });
        setLayout(null);
    }
    
    public void dibujarCartasJugador(){
        int centroX = this.getWidth() / 2;
        int centroY = this.getHeight() / 2;
        double radio = 200; // Radio del círculo
        double angulo = 2 * Math.PI / numJugadores;
        cartasPanel cartasPanel = new cartasPanel("J1", cardsPlayer);
        double x = centroX + radio * Math.cos(0 * angulo) - cartasPanel.getWidth() / 2;
        double y = centroY + radio * Math.sin(0 * angulo) - cartasPanel.getHeight() / 2;
        cartasPanel.setBounds((int) x, (int) y, 200, 225); 
        getContentPane().add(cartasPanel);
    }
    
    public void dibujarCartasMesa(){
        int X = this.getWidth() / 5;
        int Y = this.getHeight() / 3;
        cartasPanel cartasPanel = new cartasPanel("CARTAS COMUNES",commonCards);
        cartasPanel.setBounds(X, Y, 500,100); 
        getContentPane().add(cartasPanel);
    }

    public void dibujarCartasJugadores(){
        int centroX = (this.getWidth() / 3)+30;
        int centroY = (this.getHeight() / 3) +30;
        double radio = 230; // Radio del círculo
        double angulo = 2 * Math.PI / numJugadores;
         for (int i = 0; i < numJugadores; i++) {
            cartasPanel cartasPanel = new cartasPanel("J"+players[i].getID(), players[i].getCards());
            double x = centroX + radio * Math.cos(i * angulo) - cartasPanel.getWidth() / 2;
            double y = centroY + radio * Math.sin(i * angulo) - cartasPanel.getHeight() / 2;
            cartasPanel.setBounds((int) x, (int) y, 200, 225); 
            getContentPane().add(cartasPanel);
        }  
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        salidaButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        salidaButton.setBackground(new java.awt.Color(204, 0, 0));
        salidaButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        salidaButton.setForeground(new java.awt.Color(255, 255, 255));
        salidaButton.setText("RESULTADO");
        salidaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salidaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(385, Short.MAX_VALUE)
                .addComponent(salidaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(373, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(283, Short.MAX_VALUE)
                .addComponent(salidaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void salidaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salidaButtonActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_salidaButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BoardViewer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton salidaButton;
    // End of variables declaration//GEN-END:variables
}
