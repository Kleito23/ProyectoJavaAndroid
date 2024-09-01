package com.example.unoproject;

public class CartaComodin extends Carta {

    public CartaComodin(Color color, String valor) {
        super(color,valor);
    }


    @Override
    public String toString() {
        return super.toString() + ","+ valor +")";
    }
    
    
}

