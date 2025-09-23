package hja.grupo3.practica2_hja.view;
import hja.grupo3.practica2_hja.game.Board;
import hja.grupo3.practica2_hja.game.Card;
import hja.grupo3.practica2_hja.game.Combos;
import hja.grupo3.practica2_hja.game.Suit;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static javax.swing.SwingConstants.CENTER;

public class MainFrame extends javax.swing.JFrame {
    
    private List<String> jugadas;
    private final String[] rangoC = {"KK", "AKs","QQ","AKo","JJ","AQs","TT","AQo","99","AJs","88","ATs","AJo","77","66","ATo","A9s","55", "A8s", "KQs","44","A9o","A7s","KJs","A5s","A8o","A6s", "A4s", "33","KTs", "A7o","A3s","KQo", "A2s","A5o","A6o","A4o","KJo","QJs","A3o", "22","K9s","A2o", "KTo", "QTs", "K8s", "K7s","JTs","K9o", "K6s", "QJo", "Q9s", "K5s","K8o", "K4s","QTo", "K7o","K3s", "K2s", "Q8s","K6o","J9s","K5o","Q9o","JTo","K4o","Q7s","T9s","Q6s","K3o","J8s", "Q5s","K2o","Q8o", "Q4s", "Q3s","J9o","T8s","J7s","Q7o", "Q2s","Q6o","98s", "Q5o","T9o","J8o","J6s","J5s", "T7s","Q4o","J4s","J7o","Q3o","97s","J3s","T8o","T6s","Q2o","J2s","87s","J6o","98o", "T7o","96s","J5o","T5s", "T4s", "86s","J4o","T3s","97o","T6o","95s","76s","J3o","T2s","87o","85s","96o","J2o","T5o","94s", "75s","65s","T4o","93s","86o","84s","95o","76o","T3o","92s","74s","85o","54s","T2o","64s","83s","75o","94o","82s","73s","93o","65o","53s","63s","84o","92o","43s","72s","74o","54o","62s", "52s", "64o","83o","42s","82o","73o","63o","53o","43o","32s","72o","62o","52o","42o","32o"};
    
    private final String numberOrder = "AKQJT98765432";
        
    private static JLabel[][] labelTable;
    
    private static JLabel[][] labelTableBoard;
    
    private List<String> selectedLabelsBoard;
    
    private List<String> selectedLabels;

    private String textFrame = "";
    private JPanel numComboBarra;
    private JPanel prueba;
    
    private Board board;

    /**
     * Creates new form MainFrame
     */
    
     public MainFrame(Board board) {
        labelTable = new JLabel[13][13];
        selectedLabels = new ArrayList<>();
        
        labelTableBoard = new JLabel[4][13];
        selectedLabelsBoard = new ArrayList<>();
        jugadas = new ArrayList<>();
        this.board = board;
        this.numComboBarra = new JPanel();
        this.prueba = new JPanel();
        initComponents();
        porcentageLabel.setText(porcentageSlider.getValue() + "%");
        this.setLocationRelativeTo(null);
        
        paintTable();
        paintTableBoard();
       
        textFrame = jTextUsuario.getText();
        
      //  BoardPanel boardpanel = new BoardPanel(board, textFrame);
      //  boardpanel.setVisible(true);
      //  jBoardPanel.add(boardpanel);
    }
    
    public MainFrame() {
        this.board = new Board();
        paintTableCombos();
        labelTable = new JLabel[13][13];
        selectedLabels = new ArrayList<>();
        
        labelTableBoard = new JLabel[4][13];
        selectedLabelsBoard = new ArrayList<>();
        jugadas = new ArrayList<>();        
        initComponents();
        porcentageLabel.setText(porcentageSlider.getValue() + "%");
        this.setLocationRelativeTo(null);
        
        paintTable();
        paintTableBoard();
       
        textFrame = jTextUsuario.getText();
        
        
    
      //  BoardPanel boardpanel = new BoardPanel(board, textFrame);
      //  boardpanel.setVisible(true);
      //  jBoardPanel.add(boardpanel);
    }
    
    private void paintTable() {
        int distX =-20;
        int distY = 22;
        
        int aux = 0;
       
        for (int i = 0; i < 13; i++) {
            for ( int j = 0; j < 13 ; j++){   
                String name;
                labelTable[i][j] = new JLabel();               
                if (j < aux) {
                    name = String.format("%c%c", numberOrder.charAt(j), numberOrder.charAt(i));
                    labelTable[i][j].setBackground(new Color(146,179,213));
                    name += "o";
                }
                else if (j > aux) {
                    name = String.format("%c%c", numberOrder.charAt(i), numberOrder.charAt(j));
                    labelTable[i][j].setBackground(new Color(255,153,153));                    
                    name += "s";
                }
                else {
                    name = String.format("%c%c", numberOrder.charAt(i), numberOrder.charAt(j));
                    labelTable[i][j].setBackground(new Color(204,255,204));
                }              

                labelTable[i][j].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                labelTable[i][j].setOpaque(true);
                labelTable[i][j].setText(name);
                labelTable[i][j].setHorizontalAlignment(CENTER);
                distX += 42;             
                labelTable[i][j].setBounds(distX, distY, 40, 40); 
                getContentPane().add(labelTable[i][j]);
                labelTable[i][j].repaint();

                labelTable[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JLabel clickedLabel = (JLabel) e.getSource();
                        if (selectedLabels.contains(clickedLabel.getText())) {
                            // Label is already selected, deselect it
                            selectedLabels.remove(clickedLabel.getText());
                            if (clickedLabel.getText().contains("o")) 
                                clickedLabel.setBackground(new Color(146,179,213));    
                            else if (clickedLabel.getText().contains("s")) 
                                clickedLabel.setBackground(new Color(255,153,153));    
                            else 
                                clickedLabel.setBackground(new Color(204,255,204));    
                            clickedLabel.repaint();
                        } 
                        else {
                            // Label is not selected, select it
                            selectedLabels.add(clickedLabel.getText());
                            //clickedLabel.setBackground(new Color(255,255,0));
                            changeColor(clickedLabel.getText());
                        }
                        graphicToText();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                
            }
            distX = -20;
            distY += 42;
            aux++;
        }  
    }
    
    public void changeColorPairs() {
        paintTable();
        String[][] pairs = board.getPairs();
        if (pairs == null) { paintTable(); return; }
        for (int i = 0; i < pairs.length; i++) {
            for (int j = 0; j < pairs[0].length; j++) {
                if (pairs[i][j] != null) { changeColor(pairs[i][j]); }
            }           
        }
    }
    
    
    //Lo llama el controller?
    public void changeColor(String name) {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; ++j) {
                if (labelTable[i][j].getText().equals(name)) {
                    labelTable[i][j].setBackground(new Color(255,255,0));
                    labelTable[i][j].setOpaque(true);
                    labelTable[i][j].repaint();
                    return;
                }
            }
        }
    }
    
    public void graphicToText() {
        boolean[][] labelPrinted = new boolean[13][13];
        
        String text = "";
        
        for (int i = 0; i < 13; ++i) {
            for (int j = 0; j < 13; ++j) {
                labelPrinted[i][j] = false;
            }
        }
        
        for (int i = 0; i < 13; ++i) {
            for (int j = 0; j < 13; ++j) {
                Color back = labelTable[i][j].getBackground();
                if (labelTable[i][j].getBackground().equals(new Color(255,255,0)) && !labelPrinted[i][j]) {
                    String first = labelTable[i][j].getText();
                    String set = first.charAt(first.length()-1) != 'o' && first.charAt(first.length()-1) != 's' ? "" : ""+first.charAt(first.length()-1);
                    int sizeText = first.length();  
                    
                    //vertical
                    if (i < 12 && labelTable[i+1][j].getBackground().equals(new Color(255,255,0)) && sizeText == labelTable[i+1][j].getText().length() && labelTable[i+1][j].getText().contains(set)) {
                        
                        labelPrinted[i][j] = true;
                        String last = labelTable[i+1][j].getText();
                        labelPrinted[i+1][j] = true;

                        for (int k = i+2; k < 13; ++k) {
                            if (!labelTable[k][j].getBackground().equals(new Color(255,255,0)) || sizeText != labelTable[k][j].getText().length() || !labelTable[k][j].getText().contains(set))
                                break;
                            else {
                                last = labelTable[k][j].getText();
                                labelPrinted[k][j] = true;
                            }
                        }
                        
                        String range = "";
                        
                        if (set.equals("o") && (i == 0 || labelTable[i-1][j].getText().length() == 2))
                            range = String.format("%s+", last);                       
                        else
                            range = String.format("%s-%s", first, last);
                        
                        text += text.equals("") ? range : ","+range;
                    }
                    //horizontal
                    else if (j < 12 && labelTable[i][j+1].getBackground().equals(new Color(255,255,0)) && sizeText == labelTable[i][j+1].getText().length() && labelTable[i][j+1].getText().contains(set)) {
                
                        labelPrinted[i][j] = true;
                        String last = labelTable[i][j+1].getText();
                        labelPrinted[i][j+1] = true;

                        for (int k = j+2; k < 13; ++k) {
                            if (!labelTable[i][k].getBackground().equals(new Color(255,255,0)) || sizeText != labelTable[i][k].getText().length() || !labelTable[i][k].getText().contains(set))
                                break;
                            else {
                                last = labelTable[i][k].getText();
                                labelPrinted[i][k] = true;
                            }
                        }
                        
                        String range = "";
                        
                        if (set.equals("s") && (j == 0 || labelTable[i][j-1].getText().length() == 2))
                            range = String.format("%s+", last);                       
                        else
                            range = String.format("%s-%s", first, last);
                        
                        text += text.equals("") ? range : ","+range;
                    }
                    //diagonal
                    else if (j == i && i < 12 && labelTable[i+1][j+1].getBackground().equals(new Color(255,255,0)) && sizeText == 2) {
                        labelPrinted[i][j] = true;
                        String last = labelTable[i+1][j+1].getText();
                        labelPrinted[i+1][j+1] = true;

                        for (int k = i+2; k < 13; ++k) {
                            if (!labelTable[k][k].getBackground().equals(new Color(255,255,0)) || sizeText != 2)
                                break;
                            else {
                                last = labelTable[k][k].getText();
                                labelPrinted[k][k] = true;
                            }
                        }
                        
                        String range = "";
                        
                        if (i == 0)
                            range = String.format("%s+", last);                       
                        else
                            range = String.format("%s-%s", first, last);
                        
                        text += text.equals("") ? range : ","+range;
                    }
                    else {
                        text += text.equals("") ? labelTable[i][j].getText() : ","+labelTable[i][j].getText();
                        labelPrinted[i][j] = true;
                    }
              
                }
            }
        }
        
        jTextUsuario.setText(text);
    }
    
    private void showPorcentage(int x){
        paintTable();
        int indice = ((x * rangoC.length) / 100 );
        
        //¿AA cuando se pinta? 
        labelTable[0][0].setBackground(new Color(204,153,255));
        labelTable[0][0].setOpaque(true);
        labelTable[0][0].repaint();
        
        for ( int i = 0; i < indice ; i++){
            changeColorPorcentage(rangoC[i]);
        }
    }
    
    public void changeColorPorcentage(String name) {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; ++j) {
                if (labelTable[i][j].getText().equals(name)) {
                    labelTable[i][j].setBackground(new Color(204,153,255));
                    labelTable[i][j].setOpaque(true);
                    labelTable[i][j].repaint();
                    return;
                }
            }
        }
    }
    
    private void paintTableBoard() {
        int distX =610;
        int distY =-20;
        
        int aux = 0;
       
        for (int i = 0; i < 4; i++) {
            for ( int j = 0; j < 13 ; j++){   
                String name;
                labelTableBoard[i][j] = new JLabel();               
                   
                name = String.format("%c", numberOrder.charAt(j));
                
                char suit = Suit.getSuit(i);
                
                switch(suit) { 
                    case Suit.HEARTS -> labelTableBoard[i][j].setBackground(new Color(255,204,204));
                    case Suit.CLUBS -> labelTableBoard[i][j].setBackground(new Color(204,255,153));
                    case Suit.DIAMONDS -> labelTableBoard[i][j].setBackground(new Color(178,216,255));
                    case Suit.SPADES -> labelTableBoard[i][j].setBackground(new Color(204,204,204));
                }              

                name += suit;
                
                labelTableBoard[i][j].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                labelTableBoard[i][j].setOpaque(true);
                labelTableBoard[i][j].setText(name);
                labelTableBoard[i][j].setHorizontalAlignment(CENTER);
                distY += 42;             
                labelTableBoard[i][j].setBounds(distX, distY, 40, 40); 
                this.add(labelTableBoard[i][j]);
                labelTableBoard[i][j].repaint();

                labelTableBoard[i][j].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JLabel clickedLabel = (JLabel) e.getSource();
                        if (selectedLabelsBoard.contains(clickedLabel.getText())) {
                            // Label is already selected, deselect it
                            selectedLabelsBoard.remove(clickedLabel.getText());
                            String card = clickedLabel.getText();
                            char suit = card.charAt(card.length()-1);

                            switch(suit) { 
                                case Suit.HEARTS -> clickedLabel.setBackground(new Color(255,204,204));
                                case Suit.CLUBS -> clickedLabel.setBackground(new Color(204,255,153));
                                case Suit.DIAMONDS -> clickedLabel.setBackground(new Color(178,216,255));
                                case Suit.SPADES -> clickedLabel.setBackground(new Color(204,204,204));
                            }  
                          
                            clickedLabel.repaint();
                        } 
                        else {
                            // Label is not selected, select it
                            selectedLabelsBoard.add(clickedLabel.getText());
                            //clickedLabel.setBackground(new Color(255,255,0));
                            changeColorBoard(clickedLabel.getText());
                        }
                        graphicToTextBoard();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                
            }
            distY = -20;
            distX += 42;
            aux++;
        }  
    }
    
    private void paintTableCombos() {
      
        prueba.removeAll();
        board.infoToCombos();
        int distXCombo = 800;
        int distX = 100 + distXCombo;
        int distY = 25;
        int width;
        Map<String, Integer> combos = board.getMapComb();
        
        prueba.setBounds(distXCombo, distY, this.getWidth(), this.getHeight());
       
        distXCombo = 0;
        distX = 100 + distXCombo;
        
        for ( String aux : combos.keySet()){
            if ( combos.get(aux) >= 0){
            JLabel textCombos = new JLabel(aux);
            JLabel numComboBarra = new JLabel(combos.get(aux).toString());
            JLabel textComboBarra = new JLabel(aux);
        
            textCombos.setBounds(distXCombo, distY, 200, 20);
            width = 10 + 5 * combos.get(aux);
            numComboBarra.setBounds(distX, distY, width, 20);
            textComboBarra.setBounds(distX + width + 5, distY,200, 20);
            
            numComboBarra.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            numComboBarra.setBackground(Color.BLUE);
            numComboBarra.setForeground(Color.WHITE);
            textComboBarra.setForeground(Color.BLUE);
            
            numComboBarra.setOpaque(true);
            textComboBarra.setOpaque(true);
            textCombos.setOpaque(true);
            prueba.add(numComboBarra);
            prueba.add(textComboBarra);
            prueba.add(textCombos);
            
            distY +=20;
            }
        }
        
        JLabel textTotalCombos = new JLabel("Total numbers of combos: " + board.getNComb());
        textTotalCombos.setBounds(distXCombo, distY + 25, 200, 20);
        textTotalCombos.setOpaque(true);
        prueba.add(textTotalCombos);
        
        prueba.setVisible(true);
        this.add(prueba);
        
    }
    
    public void changeColorPairsBoard() {
        paintTableBoard();
        String[][] pairs = board.getPairsBoard();
        if (pairs == null) { paintTableBoard(); return; }
        for (int i = 0; i < pairs.length; i++) {
            for (int j = 0; j < pairs[0].length; j++) {
                if (pairs[i][j] != null) { changeColorBoard(pairs[i][j]); }
            }           
        }
    }
    
    
    //Lo llama el controller?
    public void changeColorBoard(String name) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; ++j) {
                if (labelTableBoard[i][j].getText().equals(name)) {
                    labelTableBoard[i][j].setBackground(new Color(255,255,0));
                    labelTableBoard[i][j].setOpaque(true);
                    labelTableBoard[i][j].repaint();
                    return;
                }
            }
        }
    }
    
    public void graphicToTextBoard() {
        boolean[][] labelPrinted = new boolean[13][13];
        
        String text = "";
        
        for (int i = 0; i < 13; ++i) {
            for (int j = 0; j < 13; ++j) {
                labelPrinted[i][j] = false;
            }
        }
        
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                Color back = labelTableBoard[i][j].getBackground();
                if (back.equals(new Color(255,255,0)) && !labelPrinted[i][j]) {                       
                    text += text.equals("") ? labelTableBoard[i][j].getText() : ","+labelTableBoard[i][j].getText();
                        labelPrinted[i][j] = true;
                }
            }
        }
        
        jTextUsuario2.setText(text);
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextUsuario = new javax.swing.JTextField();
        jEvaluateButton = new javax.swing.JButton();
        porcentageSlider = new javax.swing.JSlider();
        porcentageLabel = new javax.swing.JLabel();
        infoButton = new javax.swing.JButton();
        jTextUsuario2 = new javax.swing.JTextField();
        OKbutton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jEvaluateButton.setText("EVALUATE");
        jEvaluateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEvaluateButtonActionPerformed(evt);
            }
        });

        porcentageSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                porcentageSliderStateChanged(evt);
            }
        });

        porcentageLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        porcentageLabel.setText("jLabel1");

        infoButton.setText("INFO");
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        OKbutton.setText("OK");
        OKbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKbuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jEvaluateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(porcentageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(porcentageLabel)))
                .addGap(120, 120, 120)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextUsuario2, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                    .addComponent(infoButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(OKbutton)
                .addContainerGap(306, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(581, 581, 581)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextUsuario)
                            .addComponent(jEvaluateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextUsuario2)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(OKbutton)
                        .addGap(10, 10, 10)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(porcentageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(porcentageLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jEvaluateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEvaluateButtonActionPerformed
        String text = jTextUsuario.getText();
        board.readRange(text);
        changeColorPairs();
    }//GEN-LAST:event_jEvaluateButtonActionPerformed

    private void porcentageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_porcentageSliderStateChanged
        porcentageLabel.setText(porcentageSlider.getValue() + "%");
        showPorcentage(porcentageSlider.getValue());
    }//GEN-LAST:event_porcentageSliderStateChanged

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        // TODO add your handling code here:
        String text = jTextUsuario.getText();
        board.readRange(text);
        changeColorPairs();
        
        String text2 = jTextUsuario2.getText();
        board.readBoard(text2);
        changeColorPairsBoard();
        
        
        if ( text.length() > 0 && text2.length() > 0){
             //PARA LOS COMBOS
            this.remove(numComboBarra);
            paintTableCombos();
        }
        else{
            this.remove(prueba);
             JLabel texto = new JLabel("<html><body>NO HAY RANGO DE CARTAS <br> NI BOARD SELECCIONADOS</body></html>");
             texto.setBounds(0, 0, 200, 100);
             texto.setOpaque(true);
             numComboBarra.setBounds(850, 300,400, 100);
             numComboBarra.setOpaque(true);
             numComboBarra.add(texto);
             this.add(numComboBarra);
             
        }
        this.repaint();
    }//GEN-LAST:event_infoButtonActionPerformed

    private void OKbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKbuttonActionPerformed
        // TODO add your handling code here:
        String text2 = jTextUsuario2.getText();
        board.readBoard(text2);
        changeColorPairsBoard();
    }//GEN-LAST:event_OKbuttonActionPerformed

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        labelTable = new JLabel[13][13];
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OKbutton;
    private javax.swing.JButton infoButton;
    private javax.swing.JButton jEvaluateButton;
    private javax.swing.JTextField jTextUsuario;
    private javax.swing.JTextField jTextUsuario2;
    private javax.swing.JLabel porcentageLabel;
    private javax.swing.JSlider porcentageSlider;
    // End of variables declaration//GEN-END:variables
}
