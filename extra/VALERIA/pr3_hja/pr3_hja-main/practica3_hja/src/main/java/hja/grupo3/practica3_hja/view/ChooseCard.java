package hja.grupo3.practica3_hja.view;

import hja.grupo3.practica3_hja.game.Card;
import hja.grupo3.practica3_hja.game.Board;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author usuario_local
 */
public class ChooseCard extends javax.swing.JFrame {
    
    private static final int INITIAL_CARDS = 0;
    private boolean ok = false;
    private int indice;
    private Board board;
    private Card[][] deck;
    private List<JLabel> selectedLabels;
    private int nPlayer;
    private static int nChosen;
    private ArrayList<Card> cardsPlayer;
    private MainWindow mainWindow;
    /**
     * Creates new form ChooseCard
     */
    public ChooseCard() {
    }
    public ChooseCard(MainWindow mainWindow, Board board, int indice) {
        this.board = board;
        this.indice = indice;
        this.mainWindow = mainWindow;
        initComponents();
        this.selectedLabels = new ArrayList<>();
        this.deck = board.getDeck();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        nPlayer = 1;
        nChosen = 0;
        cardsPlayer = new ArrayList<>();
        choosePlayer();
        generateCardPanel();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
    }
    
    public void choosePlayer() {
        if ( indice == 2 || indice == 4){
        player.setText(String.format("%s%d", "Player ", nPlayer));
        }
        else{
            player.setText("Board");
        }
    }
    
    public void generateCardPanel(){
        int auxi = 0;
        int auxj= 0;
        choose.setEnabled(false);
        int x = 20;
        int y = 10;
        for ( int i = 0; i  < 4; i++){
            for ( int j = 0; j < 13; j++){
                JLabel label = new JLabel();
                cartasPanel aux = new cartasPanel();
                if (deck[i][j].getActive()){
                label.setIcon(new ImageIcon(getClass().getResource(aux.chooseIcon(deck[i][j]))));
                }
                else{
                    Border border = new LineBorder(Color.BLUE, 2);
                    label.setBorder(border);
                    selectedLabels.add(label);
                    label.setIcon(new ImageIcon(getClass().getResource(aux.chooseTransparentIcon(deck[i][j]))));
                }
                /*if(!deck[i][j].getActive()) {
                    ImageFilter filter = new GrayFilter(true, 50); // 50 is the intensity, adjust as needed
                    ImageProducer producer = new FilteredImageSource(image.getImage().getSource(), filter);
                    Image grayscaleImage = Toolkit.getDefaultToolkit().createImage(producer);
                    label.setEnabled(false);
                }*/
                label.setText(String.format("%d%d", i, j));
                //label.setPreferredSize(new Dimension(49, 71));
                label.setBounds(x, y, 72, 105);// Tamaño del JLabel
                label.setOpaque(true);
                label.setVisible(true);
                cardsPanel.add(label);
                 x += 78;

                    label.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {                                       
                        JLabel clickedLabel = (JLabel) e.getSource();
                        String c = clickedLabel.getText();
                        String subString = c.substring(1);
                        int auxi= c.charAt(0) - '0';
                        int auxj = Integer.parseInt(subString);
                        if (deck[auxi][auxj].getActive()) {
                             if (selectedLabels.contains(clickedLabel)) {
                                  // Label is already selected, deselect it
                                  selectedLabels.remove(clickedLabel);
                                  label.setBorder(null);
                                  clickedLabel.repaint();
                                  nChosen--;
                                  if ( nChosen < indice){
                                      choose.setEnabled(false);
                                  }
                                  cardsPlayer.remove(deck[auxi][auxj]);
                               }
                             else{
                                 if (nChosen < indice) {
                                    selectedLabels.add(clickedLabel);
                                    Border border = new LineBorder(Color.BLUE, 2);
                                    label.setBorder(border);
                                    nChosen++;
                                    cardsPlayer.add(deck[auxi][auxj]);
                                 }
                                 if (nChosen == indice){
                                    choose.setEnabled(true);
                                 }
                             }
                        }
                     
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                      
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                       JLabel aux = (JLabel) e.getSource();
                       if (!selectedLabels.contains(aux) ){ 
                        Border border = new LineBorder(Color.GRAY, 2);
                        label.setBorder(border);}
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        JLabel aux = (JLabel) e.getSource();
                        if (!selectedLabels.contains(aux)){ 
                          label.setBorder(null);}
                    }
                    }    
                );
            }
            x= 20;
            y += 110;
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

        cardsPanel = new javax.swing.JPanel();
        player = new javax.swing.JLabel();
        choose = new javax.swing.JButton();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        cardsPanel.setBackground(new java.awt.Color(255, 255, 255));

        player.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        player.setText("jLabel1");

        choose.setBackground(new java.awt.Color(204, 204, 255));
        choose.setText("CHOOSE THIS CARDS");
        choose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseActionPerformed(evt);
            }
        });

        backButton.setText("BACK");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cardsPanelLayout = new javax.swing.GroupLayout(cardsPanel);
        cardsPanel.setLayout(cardsPanelLayout);
        cardsPanelLayout.setHorizontalGroup(
            cardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardsPanelLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(backButton)
                .addGap(243, 243, 243)
                .addComponent(player, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(choose, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(354, Short.MAX_VALUE))
        );
        cardsPanelLayout.setVerticalGroup(
            cardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardsPanelLayout.createSequentialGroup()
                .addContainerGap(473, Short.MAX_VALUE)
                .addGroup(cardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(choose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(player, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(backButton)))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseActionPerformed
        
// 1 --- CARTAS DEL JUGADOR
        if ( indice == 2 || indice == 4){
        board.chooseCards(nPlayer, cardsPlayer);
            //PARA PONER LOS JLABES TRANSPARENTES
            cartasPanel aux = new cartasPanel();
            for (JLabel label : selectedLabels){
                String c = label.getText();
                String subString = c.substring(1);
                int auxi= c.charAt(0) - '0';
                int auxj = Integer.parseInt(subString);
                label.setIcon(new ImageIcon(getClass().getResource(aux.chooseTransparentIcon(deck[auxi][auxj]))));
                //label.setBorder(null);
            }

            if(nPlayer < 6) {      
                nPlayer++;
                nChosen = 0;
                choose.setEnabled(false);
                cardsPlayer.clear();
                choosePlayer();
                generateCardPanel();
            }
            else {
                this.dispose();
            }
        }
        
 //2 - CARTAS DEL BOARD
        if ( indice == 5){
            board.chooseBoardCards(INITIAL_CARDS, cardsPlayer);
            //PARA PONER LOS JLABES TRANSPARENTES
            cartasPanel aux = new cartasPanel();
            for (JLabel label : selectedLabels){
                String c = label.getText();
                String subString = c.substring(1);
                int auxi= c.charAt(0) - '0';
                int auxj = Integer.parseInt(subString);
                label.setIcon(new ImageIcon(getClass().getResource(aux.chooseTransparentIcon(deck[auxi][auxj]))));
            }
            this.dispose();
        }
    }//GEN-LAST:event_chooseActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        this.dispose();
        mainWindow.setPlayEnable(false);
          if ( indice == 5){
             mainWindow.setCardsCommon(false);
             board.resetBoardCard();
          }
          else{
              mainWindow.setCardsPlayers(false);
              board.resetCard();
          }
       
    }//GEN-LAST:event_backButtonActionPerformed

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
            java.util.logging.Logger.getLogger(ChooseCard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChooseCard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChooseCard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChooseCard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChooseCard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel cardsPanel;
    private javax.swing.JButton choose;
    private javax.swing.JLabel player;
    // End of variables declaration//GEN-END:variables
}
