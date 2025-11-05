package com.ucm.evaluator;


public class Evaluator {

    
    private static Evaluator _eval;

    /**
     * Evitar instanciamiento desde fuera de esta clase
     */
    private Evaluator(){} 
    
    
    /**
     * Asegurarse de que esta clase es un Singleton: 
     * Esta clase utilizará una gran cantidad de memoria y debe ser accesible por cualquier partida sin necesidad de instanciamiento
     */
    static public Evaluator getInstance(){
        
        if(_eval == null){
            _eval = new Evaluator();
        }
        
        return _eval;
    }
}