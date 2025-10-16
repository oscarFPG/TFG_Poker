package com.ucm.gameobjects;

<<<<<<< Updated upstream
=======
import com.ucm.commands.Command;
import com.ucm.commands.FoldCommand;
>>>>>>> Stashed changes

public class Player {
 
    private Card[] _cards;
    private int _id;
    private String _name;
<<<<<<< Updated upstream
    private boolean _fold = false;
    private int _money;
    private String _role;
    

    public Player(Card[] cards){
        this._cards = cards;
=======
    private int _money; // Player's total money
    private int _pocketMoney; // Money the play has bet. It is not lost unless the player folds or loses and
                              // it is a portion of the remaining of the total
    private PlayerRole _role;
    private Card[] _cards;
    private int _numCards;
    private boolean _fold;

    public Player(int id, String name, int money) {
        _id = id;
        _name = name;
        _money = money;
        _pocketMoney = 0;
        _role = PlayerRole.NO_ROLE;
        _cards = new Card[2];
        _numCards = 0;
        _fold = false;
    }

    public Command makePlay(final int sb, final int bb, final int maxBet) {
        // hacer linea comando para apuestas
        return new FoldCommand(this, sb);
    }

    public boolean receiveCard(Card c) {

        if (_numCards == 2)
            return false;

        _cards[_numCards++] = c;
        return true;
    }

    public Card retrieveCard() {

        if (_numCards <= 0)
            return null;

        Card c = _cards[_numCards--];
        return c;
    }

    public int placeBet() {

        int money = _pocketMoney;
        _pocketMoney = 0;
        return money;
    }

    private int makeForcedPlay(int q) {
        _money -= q;
        _pocketMoney += q;
        return q;
    }

    public void fold() {
        // Devuelvo las cartas
        Card c1 = retrieveCard();
        if (c1 != null)
            c1.setAvailable(true);

        Card c2 = retrieveCard();
        if (c2 != null)
            c2.setAvailable(true);
        // El jugador pierde su apuesta
        // Ya no tiene derecho a seguir jugando
        _fold = true;
    }

    public void call(int maxBet) {
        // Cogemos la apuesta que está en juego ahora y calculamos la diferencia entre
        // la apuesta en juego
        // y lo que tengo apostado de momento
        int resto = maxBet - this._pocketMoney;
        // Si quiero igualar pero no tengo dinero puedo hacer un allin
        if (resto > this._money) {
            allIn();
            return;
        }
        // Quito de mi cartera la diferencia
        decreaseMoney(resto);
        // Aumento la apuesta de mi ronda
        increasePoketMoney(resto);

    }

    public void allIn() {
        increasePoketMoney(this._money);
        this._money = 0;
    }

    public void raise(int maxBet) {
        // Si tengo menos dinero de lo que está apostado y quiero subir
        // entonces primero igualo y luego subo lo que sea(max All-in)
        if (this._pocketMoney < maxBet) {
            call(maxBet);
            // ejemplo: apuesto 10
            this._pocketMoney += 10;
            return;
        }

        // En cualquier otro caso subo lo que eliga el player(max All-in)
        this._pocketMoney += 10;
    }

    private void decreaseMoney(int resto) {
        this._money -= resto;
    }

    private void increasePoketMoney(int resto) {
        this._pocketMoney += resto;
>>>>>>> Stashed changes
    }

    public Player(String name){
        this._name = name;
    }

<<<<<<< Updated upstream
    public Player(int id, Card[] cards,  String name, int money){
        this._id = id;     
        this._cards = cards;
        this._name = name;
        this._money = money;
    }
        
    
    public Card[] getCards(){ return _cards;}
    
    public int getID(){  return _id;}
        
    public String getName(){return _name;}
=======
    public int getID() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getMoney() {
        return _money;
    }

    public int getPocketMoney() {
        return _pocketMoney;
    }

    public PlayerRole getPlayerRole() {
        return _role;
    }

    public int getNumCards() {
        return _numCards;
    }

    public boolean hasFolded() {
        return _fold;
    }
>>>>>>> Stashed changes

    public boolean getFold() {return _fold; }
    
    public int getMoney(){ return _money;}


    public void setCards(Card[] c) {
        _cards = c;
    }

    public void setName(String n) {
        _name = n;
    }

    public void setMoney(int m) {
        _money = m;
    }
    public void setFold(boolean f) {
        _fold = f;
    }
    
    
}
