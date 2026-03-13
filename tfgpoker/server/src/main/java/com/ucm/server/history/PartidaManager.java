package com.ucm.server.history;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class PartidaManager {


    //convierte partida a doc
    public static void savePartida(Partida partida, String ruta) throws Exception {
        
    }

    //cara partida de un doc
     public static Partida loadPartida(String ruta) throws Exception {
        ObjectInputStream par = new ObjectInputStream(
                new FileInputStream(ruta));
        Partida partida = (Partida) par.readObject();
        par.close();
        return partida;
    }
}