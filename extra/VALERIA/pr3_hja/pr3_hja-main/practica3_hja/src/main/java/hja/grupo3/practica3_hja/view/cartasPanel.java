/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package hja.grupo3.practica3_hja.view;

import hja.grupo3.practica3_hja.game.Card;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 *
 * @author Usuario
 */
public class cartasPanel extends javax.swing.JPanel {

    /**
     * Creates new form cartasPanel
     */
    private float equity;
    private BoardViewer boardViewer;
    private Card[] cards;
    private String id;
    private JLabel equityLabel;
    private boolean omaha;
    private boolean fold = false;
    
    public cartasPanel() {}
   
    public cartasPanel(BoardViewer boardViewer, String id, Card[] cards, float equity, boolean omaha) {
        initComponents();
        this.boardViewer = boardViewer;
        this.id = id;
        this.cards = cards;
        this.equity = equity;
        this.omaha = omaha;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
       
        
        //---------ID JUGADOR-----------------
        JLabel tituloLabel = new JLabel();
        tituloLabel.setText("J"+id);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setHorizontalAlignment(SwingConstants.LEFT);
        this.add(tituloLabel);
        
         //----------CARTAS JUGADOR --------------
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        contenedor.setVisible(true);
        contenedor.setOpaque(false);
       
        for (int i = 0; i < cards.length && cards[i] != null; i++) {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(getClass().getResource(chooseIcon(cards[i]))));
            if (omaha)
            label.setPreferredSize(new Dimension(72, 105)); // Tamaño del JLabel
            contenedor.add(label);
        }
        
        if (omaha) {
            contenedor.setMaximumSize(new Dimension(600, 212));   
        }
        else {
            contenedor.setMaximumSize(new Dimension(600, 106));
        }
            
        
        
        
         // ---- FOLD BUTTON ----
        if ( id.length() >  0 ){
        JButton foldButton = new JButton("FOLD");
        foldButton.setForeground(Color.BLACK);
        foldButton.setBackground(new Color(204,255,204));
        foldButton.setEnabled(true);
        foldButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boardViewer.foldPlayer(id);
                boardViewer.addCard(cards);
                boardViewer.equity();
                boardViewer.actualizarGUI();
                equityLabel.setText(String.format("%.3f", 0.0)+"%");
                equityLabel.repaint();
                fold = true;
                contenedor.removeAll();
                for (int i = 0; i < cards.length && cards[i] != null; i++) {
                     JLabel label = new JLabel();
                     label.setIcon(new ImageIcon(getClass().getResource(chooseTransparentIcon(cards[i]))));
                     label.setPreferredSize(new Dimension(72, 105)); // Tamaño del JLabel
                     contenedor.add(label);
                 }
                  contenedor.revalidate();
                  contenedor.repaint();
                  boardViewer.repaint();
                  foldButton.setBackground(Color.GRAY);
                  foldButton.setEnabled(false);

              }
            });
        
        this.add(foldButton);
        }
        this.add(contenedor);
          
        if ( id.length() > 0){
        //--------EQUITY JUGADOR -------------------
        equityLabel= new JLabel();
        equityLabel.setText(String.format("%.3f", equity)+"%");
        equityLabel.setFont(new Font("Arial", Font.BOLD, 16));
        equityLabel.setForeground(Color.WHITE);
        equityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        equityLabel.setMaximumSize(new Dimension(100, 50));
        Border border = new LineBorder(Color.WHITE, 2);
        equityLabel.setBorder(border);
        equityLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        this.add(equityLabel);
        }
    }
   
     void actualizarEquity() {
         if( !fold){
            equityLabel.setText(String.format("%.3f", equity)+"%");
            equityLabel.repaint();
            boardViewer.repaint();
         }
     }

      void setEquity(float equity) {
          this.equity = equity;
      }

   
    public String chooseIcon(Card card){
        //--------A-------------------------
        if ( card.getNumber() == 'A' && card.getSuit() == 'h'){
            return "/images/imagen2/ace_of_hearts.png"; 
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 'd'){
            return "/images/imagen2/ace_of_diamonds.png";
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 'c'){
           return "/images/imagen2/ace_of_clubs.png";
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 's'){
            return "/images/imagen2/ace_of_spades2.png";
        }
        //-------------------2--------------------
        if ( card.getNumber() == '2' && card.getSuit() == 'h'){
           return "/images/imagen2/2_of_hearts.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 'd'){
            return "/images/imagen2/2_of_diamonds.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 'c'){
            return "/images/imagen2/2_of_clubs.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 's'){
           return  "/images/imagen2/2_of_spades.png";
        }
        //-----------------------3 ----------------------
        if ( card.getNumber() == '3' && card.getSuit() == 'h'){
            return "/images/imagen2/3_of_hearts.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 'd'){
           return "/images/imagen2/3_of_diamonds.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 'c'){
           return "/images/imagen2/3_of_clubs.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 's'){
           return "/images/imagen2/3_of_spades.png";
        }
        //------------------4------------------------------
        if ( card.getNumber() == '4' && card.getSuit() == 'h'){
           return "/images/imagen2/4_of_hearts.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 'd'){
           return "/images/imagen2/4_of_diamonds.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 'c'){
           return "/images/imagen2/4_of_clubs.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 's'){
            return "/images/imagen2/4_of_spades.png";
        }
         //-----------------5--------------------
        if ( card.getNumber() == '5' && card.getSuit() == 'h'){
           return "/images/imagen2/5_of_hearts.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 'd'){
           return "/images/imagen2/5_of_diamonds.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 'c'){
           return "/images/imagen2/5_of_clubs.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 's'){
           return "/images/imagen2/5_of_spades.png";
        }
         //-----------------6--------------------
        if ( card.getNumber() == '6' && card.getSuit() == 'h'){
           return "/images/imagen2/6_of_hearts.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 'd'){
           return "/images/imagen2/6_of_diamonds.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 'c'){
           return "/images/imagen2/6_of_clubs.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 's'){
           return "/images/imagen2/6_of_spades.png";
        }
         //-----------------7--------------------
        if ( card.getNumber() == '7' && card.getSuit() == 'h'){
           return "/images/imagen2/7_of_hearts.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 'd'){
           return "/images/imagen2/7_of_diamonds.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 'c'){
           return "/images/imagen2/7_of_clubs.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 's'){
           return "/images/imagen2/7_of_spades.png";
        }
        //-----------------8--------------------
        if ( card.getNumber() == '8' && card.getSuit() == 'h'){
            return "/images/imagen2/8_of_hearts.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 'd'){
           return "/images/imagen2/8_of_diamonds.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 'c'){
           return "/images/imagen2/8_of_clubs.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 's'){
           return "/images/imagen2/8_of_spades.png";
        }
        //-----------------9--------------------
        if ( card.getNumber() == '9' && card.getSuit() == 'h'){
           return "/images/imagen2/9_of_hearts.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 'd'){
            return "/images/imagen2/9_of_diamonds.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 'c'){
            return "/images/imagen2/9_of_clubs.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 's'){
            return "/images/imagen2/9_of_spades.png";
        }
        //-----------------10 = T--------------------
        if ( card.getNumber() == 'T' && card.getSuit() == 'h'){
           return "/images/imagen2/10_of_hearts.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 'd'){
           return "/images/imagen2/10_of_diamonds.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 'c'){
           return "/images/imagen2/10_of_clubs.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 's'){
          return "/images/imagen2/10_of_spades.png";
        }
        //-----------------J--------------------
        if ( card.getNumber() == 'J' && card.getSuit() == 'h'){
           return "/images/imagen2/jack_of_hearts.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 'd'){
           return "/images/imagen2/jack_of_diamonds.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 'c'){
           return "/images/imagen2/jack_of_clubs.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 's'){
           return "/images/imagen2/jack_of_spades.png";
        }
        //-----------------Q--------------------
        if ( card.getNumber() == 'Q' && card.getSuit() == 'h'){
           return "/images/imagen2/queen_of_hearts.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 'd'){
            return "/images/imagen2/queen_of_diamonds.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 'c'){
           return "/images/imagen2/queen_of_clubs.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 's'){
           return "/images/imagen2/queen_of_spades.png";
        }
        //-----------------K--------------------
        if ( card.getNumber() == 'K' && card.getSuit() == 'h'){
           return "/images/imagen2/king_of_hearts.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 'd'){
           return "/images/imagen2/king_of_diamonds.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 'c'){
           return "/images/imagen2/king_of_clubs.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 's'){
           return "/images/imagen2/king_of_spades.png";
        }
        return null;
    }
    
     public String chooseTransparentIcon(Card card){
        //--------A-------------------------
        if ( card.getNumber() == 'A' && card.getSuit() == 'h'){
            return "/images/imagen/ace_of_hearts.png"; 
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 'd'){
            return "/images/imagen/ace_of_diamonds.png";
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 'c'){
           return "/images/imagen/ace_of_clubs.png";
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 's'){
            return "/images/imagen/ace_of_spades2.png";
        }
        //-------------------2--------------------
        if ( card.getNumber() == '2' && card.getSuit() == 'h'){
           return "/images/imagen/2_of_hearts.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 'd'){
            return "/images/imagen/2_of_diamonds.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 'c'){
            return "/images/imagen/2_of_clubs_.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 's'){
           return  "/images/imagen/2_of_spades.png";
        }
        //-----------------------3 ----------------------
        if ( card.getNumber() == '3' && card.getSuit() == 'h'){
            return "/images/imagen/3_of_hearts.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 'd'){
           return "/images/imagen/3_of_diamonds.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 'c'){
           return "/images/imagen/3_of_clubs.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 's'){
           return "/images/imagen/3_of_spades.png";
        }
        //------------------4------------------------------
        if ( card.getNumber() == '4' && card.getSuit() == 'h'){
           return "/images/imagen/4_of_hearts.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 'd'){
           return "/images/imagen/4_of_diamonds.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 'c'){
           return "/images/imagen/4_of_clubs.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 's'){
            return "/images/imagen/4_of_spades.png";
        }
         //-----------------5--------------------
        if ( card.getNumber() == '5' && card.getSuit() == 'h'){
           return "/images/imagen/5_of_hearts.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 'd'){
           return "/images/imagen/5_of_diamonds.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 'c'){
           return "/images/imagen/5_of_clubs.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 's'){
           return "/images/imagen/5_of_spades.png";
        }
         //-----------------6--------------------
        if ( card.getNumber() == '6' && card.getSuit() == 'h'){
           return "/images/imagen/6_of_hearts.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 'd'){
           return "/images/imagen/6_of_diamonds.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 'c'){
           return "/images/imagen/6_of_clubs.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 's'){
           return "/images/imagen/6_of_spades.png";
        }
         //-----------------7--------------------
        if ( card.getNumber() == '7' && card.getSuit() == 'h'){
           return "/images/imagen/7_of_hearts.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 'd'){
           return "/images/imagen/7_of_diamonds.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 'c'){
           return "/images/imagen/7_of_clubs.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 's'){
           return "/images/imagen/7_of_spades.png";
        }
        //-----------------8--------------------
        if ( card.getNumber() == '8' && card.getSuit() == 'h'){
            return "/images/imagen/8_of_hearts.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 'd'){
           return "/images/imagen/8_of_diamonds.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 'c'){
           return "/images/imagen/8_of_clubs.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 's'){
           return "/images/imagen/8_of_spades.png";
        }
        //-----------------9--------------------
        if ( card.getNumber() == '9' && card.getSuit() == 'h'){
           return "/images/imagen/9_of_hearts.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 'd'){
            return "/images/imagen/9_of_diamonds.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 'c'){
            return "/images/imagen/9_of_clubs.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 's'){
            return "/images/imagen/9_of_spades.png";
        }
        //-----------------10 = T--------------------
        if ( card.getNumber() == 'T' && card.getSuit() == 'h'){
           return "/images/imagen/10_of_hearts.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 'd'){
           return "/images/imagen/10_of_diamonds.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 'c'){
           return "/images/imagen/10_of_clubs.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 's'){
          return "/images/imagen/10_of_spades.png";
        }
        //-----------------J--------------------
        if ( card.getNumber() == 'J' && card.getSuit() == 'h'){
           return "/images/imagen/jack_of_hearts.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 'd'){
           return "/images/imagen/jack_of_diamonds.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 'c'){
           return "/images/imagen/jack_of_clubs.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 's'){
           return "/images/imagen/jack_of_spades.png";
        }
        //-----------------Q--------------------
        if ( card.getNumber() == 'Q' && card.getSuit() == 'h'){
           return "/images/imagen/queen_of_hearts.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 'd'){
            return "/images/imagen/queen_of_diamonds.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 'c'){
           return "/images/imagen/queen_of_clubs.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 's'){
           return "/images/imagen/queen_of_spades.png";
        }
        //-----------------K--------------------
        if ( card.getNumber() == 'K' && card.getSuit() == 'h'){
           return "/images/imagen/king_of_hearts.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 'd'){
           return "/images/imagen/king_of_diamonds.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 'c'){
           return "/images/imagen/king_of_clubs.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 's'){
           return "/images/imagen/king_of_spades.png";
        }
        return null;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
