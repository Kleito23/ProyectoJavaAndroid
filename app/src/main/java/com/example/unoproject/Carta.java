package com.example.unoproject;

import java.util.ArrayList;

public abstract class Carta {
    enum Color {R,A,V,Z,N}
    private static ArrayList<String> comodines;
    protected Color color;
    protected String valor;

    protected Carta(Color color, String valor) {
        this.color = color;
        this.valor = valor;
        comodines = new ArrayList<>();
        setComodines();
    }

    public Color getColor() {
        return color;
    }
    public String getValor() {
        return valor;
    }
    public String getColorStr(){
        return color.toString();
    }

    private void setComodines(){
        comodines.add("^");
        comodines.add("&");
        comodines.add("%");
        comodines.add("+4");
        comodines.add("+2");
    }

    public static ArrayList<String> getComodines() {
        return comodines;
    }

    @Override
    public String toString() {
        return "("+ color;
    }

}
