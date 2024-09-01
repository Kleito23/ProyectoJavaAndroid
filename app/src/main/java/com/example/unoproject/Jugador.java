package com.example.unoproject;
import java.util.ArrayList;

public class Jugador {
    private String nombre;
    private ArrayList<Carta> mano;
    public Jugador(String nombre ){
        this.nombre = nombre;
        mano = new ArrayList<>();
    }
    public String getNombre(){
        return nombre;
    }
    public void imprimirMano(){
        String p = "Mano de " + nombre + ":";
        int contador = 1;
        for(Carta c : mano){
            p += c;
            if(contador < mano.size()){
                p += " - ";
                contador++;
            }
        }
        System.out.println(p);
    }

    public Carta getCarta(int i){
        if(i >= 0 && i < mano.size())
            return mano.get(i);
        return null;
    }

    public ArrayList<Carta> getCartas(){
        return mano;
    }

    public void addCarta(Carta c){
        mano.add(c);
    }

    
}
