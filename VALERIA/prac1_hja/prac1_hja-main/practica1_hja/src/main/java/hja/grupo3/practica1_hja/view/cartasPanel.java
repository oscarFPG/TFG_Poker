/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package hja.grupo3.practica1_hja.view;

import hja.grupo3.practica1_hja.game.Card;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Usuario
 */
public class cartasPanel extends javax.swing.JPanel {

    /**
     * Creates new form cartasPanel
     */
    
    String _icon;
    
    public cartasPanel() {
        initComponents();
    }
    
    public cartasPanel(String id, Card[] cards) {
        initComponents();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
       
        JLabel tituloLabel = new JLabel();
        tituloLabel.setText(id);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setHorizontalAlignment(SwingConstants.LEFT);
        this.add(tituloLabel);
        
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        contenedor.setVisible(true);
        contenedor.setOpaque(false);
       
        for (int i = 0; i < cards.length && cards[i] != null; i++) {
            JLabel label = new JLabel();
            chooseIcon(cards[i]);
            label.setIcon(new ImageIcon(getClass().getResource(_icon)));
            label.setPreferredSize(new Dimension(49, 71)); // Tamaño del JLabel
            contenedor.add(label);
        }
        
        this.add(contenedor);
    }
    
    public void chooseIcon(Card card){
        //--------A-------------------------
        if ( card.getNumber() == 'A' && card.getSuit() == 'h'){
            _icon = "/images/imagen/ace_of_hearts.png";
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 'd'){
            _icon = "/images/imagen/ace_of_diamonds.png";
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 'c'){
            _icon = "/images/imagen/ace_of_clubs.png";
        }
        if ( card.getNumber() == 'A' && card.getSuit() == 's'){
            _icon = "/images/imagen/ace_of_spades.png";
        }
        //-------------------2--------------------
        if ( card.getNumber() == '2' && card.getSuit() == 'h'){
            _icon = "/images/imagen/2_of_hearts.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 'd'){
            _icon = "/images/imagen/2_of_diamonds.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 'c'){
            _icon = "/images/imagen/2_of_clubs.png";
        }
        if ( card.getNumber() == '2' && card.getSuit() == 's'){
            _icon = "/images/imagen/2_of_spades.png";
        }
        //-----------------------3 ----------------------
        if ( card.getNumber() == '3' && card.getSuit() == 'h'){
            _icon = "/images/imagen/3_of_hearts.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 'd'){
            _icon = "/images/imagen/3_of_diamonds.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 'c'){
            _icon = "/images/imagen/3_of_clubs.png";
        }
        if ( card.getNumber() == '3' && card.getSuit() == 's'){
            _icon = "/images/imagen/3_of_spades.png";
        }
        //------------------4------------------------------
        if ( card.getNumber() == '4' && card.getSuit() == 'h'){
            _icon = "/images/imagen/4_of_hearts.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 'd'){
            _icon = "/images/imagen/4_of_diamonds.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 'c'){
            _icon = "/images/imagen/4_of_clubs.png";
        }
        if ( card.getNumber() == '4' && card.getSuit() == 's'){
            _icon = "/images/imagen/4_of_spades.png";
        }
         //-----------------5--------------------
        if ( card.getNumber() == '5' && card.getSuit() == 'h'){
            _icon = "/images/imagen/5_of_hearts.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 'd'){
            _icon = "/images/imagen/5_of_diamonds.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 'c'){
            _icon = "/images/imagen/5_of_clubs.png";
        }
        if ( card.getNumber() == '5' && card.getSuit() == 's'){
            _icon = "/images/imagen/5_of_spades.png";
        }
         //-----------------6--------------------
        if ( card.getNumber() == '6' && card.getSuit() == 'h'){
            _icon = "/images/imagen/6_of_hearts.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 'd'){
            _icon = "/images/imagen/6_of_diamonds.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 'c'){
            _icon = "/images/imagen/6_of_clubs.png";
        }
        if ( card.getNumber() == '6' && card.getSuit() == 's'){
            _icon = "/images/imagen/6_of_spades.png";
        }
         //-----------------7--------------------
        if ( card.getNumber() == '7' && card.getSuit() == 'h'){
            _icon = "/images/imagen/7_of_hearts.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 'd'){
            _icon = "/images/imagen/7_of_diamonds.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 'c'){
            _icon = "/images/imagen/7_of_clubs.png";
        }
        if ( card.getNumber() == '7' && card.getSuit() == 's'){
            _icon = "/images/imagen/7_of_spades.png";
        }
        //-----------------8--------------------
        if ( card.getNumber() == '8' && card.getSuit() == 'h'){
            _icon = "/images/imagen/8_of_hearts.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 'd'){
            _icon = "/images/imagen/8_of_diamonds.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 'c'){
            _icon = "/images/imagen/8_of_clubs.png";
        }
        if ( card.getNumber() == '8' && card.getSuit() == 's'){
            _icon = "/images/imagen/8_of_spades.png";
        }
        //-----------------9--------------------
        if ( card.getNumber() == '9' && card.getSuit() == 'h'){
            _icon = "/images/imagen/9_of_hearts.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 'd'){
            _icon = "/images/imagen/9_of_diamonds.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 'c'){
            _icon = "/images/imagen/9_of_clubs.png";
        }
        if ( card.getNumber() == '9' && card.getSuit() == 's'){
            _icon = "/images/imagen/9_of_spades.png";
        }
        //-----------------10 = T--------------------
        if ( card.getNumber() == 'T' && card.getSuit() == 'h'){
            _icon = "/images/imagen/10_of_hearts.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 'd'){
            _icon = "/images/imagen/10_of_diamonds.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 'c'){
            _icon = "/images/imagen/10_of_clubs.png";
        }
        if ( card.getNumber() == 'T' && card.getSuit() == 's'){
            _icon = "/images/imagen/10_of_spades.png";
        }
        //-----------------J--------------------
        if ( card.getNumber() == 'J' && card.getSuit() == 'h'){
            _icon = "/images/imagen/jack_of_hearts.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 'd'){
            _icon = "/images/imagen/jack_of_diamonds.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 'c'){
            _icon = "/images/imagen/jack_of_clubs.png";
        }
        if ( card.getNumber() == 'J' && card.getSuit() == 's'){
            _icon = "/images/imagen/jack_of_spades.png";
        }
        //-----------------Q--------------------
        if ( card.getNumber() == 'Q' && card.getSuit() == 'h'){
            _icon = "/images/imagen/queen_of_hearts.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 'd'){
            _icon = "/images/imagen/queen_of_diamonds.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 'c'){
            _icon = "/images/imagen/queen_of_clubs.png";
        }
        if ( card.getNumber() == 'Q' && card.getSuit() == 's'){
            _icon = "/images/imagen/queen_of_spades.png";
        }
        //-----------------K--------------------
        if ( card.getNumber() == 'K' && card.getSuit() == 'h'){
            _icon = "/images/imagen/king_of_hearts.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 'd'){
            _icon = "/images/imagen/king_of_diamonds.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 'c'){
            _icon = "/images/imagen/king_of_clubs.png";
        }
        if ( card.getNumber() == 'K' && card.getSuit() == 's'){
            _icon = "/images/imagen/king_of_spades.png";
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
