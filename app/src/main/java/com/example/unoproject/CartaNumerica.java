package com.example.unoproject;
public class CartaNumerica extends Carta {
    public CartaNumerica(Color color, String valor) {
        super(color,valor);

    }

    @Override
    public String toString(){
        return super.toString() + "," + valor +")";
    }
}