package com.example.unoproject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilitaria {
    public static void esperar(int s){
        Logger log = Logger.getLogger(Utilitaria.class.getName());
        try{

            Thread.sleep(s*1000L);
        }catch(InterruptedException e){
            log.log(Level.WARNING, "Interrumpido", e);
            Thread.currentThread().interrupt();
        }
    }
}

