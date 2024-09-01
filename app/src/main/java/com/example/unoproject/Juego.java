package com.example.unoproject;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import com.example.unoproject.Carta.Color;

public class Juego {
    private Baraja baraja;
    private Jugador jugador;
    private ArrayList<Carta> manoMaquina;
    private ArrayList<Carta> lineaJuego;
    private static Color color;
    private static boolean turno = true;
    private Random random = new Random();

    public Juego(Jugador jugador){
        baraja = new Baraja();
        this.jugador = jugador;
        manoMaquina = new ArrayList<>();
        lineaJuego = new ArrayList<>();
    }

    public Baraja getBaraja(){
        return baraja;
    }

    public Color getColor(){
        return color;
    }

    public ArrayList<Carta> getManoMaquina(){
        return manoMaquina;
    }


    public Carta tomarCartaAzar(){
        Carta c;
        do{
            c = baraja.getCarta(random.nextInt(baraja.getCartas().size()));
        }while(c instanceof CartaComodin);
        return c;
    }

    private void imprimirManoMaquina(){
        String p = "Mano de maquina -> ";
        int contador = 1;
        for(Carta c : manoMaquina){
            p += c;
            if(contador < manoMaquina.size()){
                p+= " - ";
                contador++;
            }
        }
        System.out.println(p);
    }

    public void repartirCartas(){
        int cont = 0;
        while(cont < 7){
            Carta c = baraja.getCarta(random.nextInt(baraja.getCartas().size()));
            jugador.addCarta(c);
            baraja.eliminarCarta(c);
            
            Carta c2 = baraja.getCarta(random.nextInt(baraja.getCartas().size()));
            manoMaquina.add(c2);
            baraja.eliminarCarta(c2);

            cont++;
        }
        
    }

    private void mostrarLinea(){
        String p = "Linea de juego -> ";
        int contador = 1;
        for(Carta c : lineaJuego){
            p += c;
            if(contador < lineaJuego.size()){
                p += " - ";
                contador++;
            }
        }
        System.out.println(p);

    }

    public void agregarCartaMano(int i, ArrayList<Carta> cartas){
        for(int j = 0; j < i; j++){
            cartas.add(baraja.getCartas().get(j));
            baraja.getCartas().remove(j);
        }
    }
    
    public void lanzarComodinMaquina(Carta c, Color color){
        if(c.getColor() == Color.N){
            do{
                Color[] colores = Color.values();
                Color col = colores[random.nextInt(colores.length-1)];
                actualizarColor(col);
            }while(color == Color.N);
            
            System.out.println("La maquina escogio el color: " + color);
            if(c.getValor().equals("+2")){
                agregarCartaMano(2,jugador.getCartas());
                System.out.println("Se te agregó dos cartas a la mano!");
                
            }else if(c.getValor().equals("+4")){
                agregarCartaMano(4, jugador.getCartas());
                System.out.println("Se te agregó cuatro cartas a la mano!");
                
            }
        }else{
            if(c.getValor().equals("+2")){
                agregarCartaMano(2,jugador.getCartas());
                System.out.println("Se te agregó dos cartas a la mano!");
                
            }else if(c.getValor().equals("+4")){
                agregarCartaMano(4, jugador.getCartas());
                System.out.println("Se te agregó cuatro cartas a la mano!");
                
            }else if(c.getValor().equals("^") || c.getValor().equals("&") ){
                actualizarTurno(false);
                System.out.println("Perdiste el turno!");
            }
            actualizarColor(color);
        }
    }

    public void lanzarComodinJugador(Carta c, Color color, Scanner sc){
        if(c.getColor() == Color.N){
            System.out.println("Ingrese el color que desea (R,A,V,Z): ");
            String col = sc.nextLine();
            actualizarColor(Color.valueOf(col.toUpperCase()));

            System.out.println("El color ha cambiado a: " + color);
            if(c.getValor().equals("+2")){
                agregarCartaMano(2, manoMaquina);
                System.out.println("Se ha agregado dos cartas al oponente");
            }else if(c.getValor().equals("+4")){
                agregarCartaMano(4, manoMaquina);
                System.out.println("Se ha agregado cuatro cartas al oponente!");
            }
        }else{
            if(c.getValor().equals("+2")){
                agregarCartaMano(2, manoMaquina);
                System.out.println("Se ha agregado dos cartas al oponente");
            }else if(c.getValor().equals("+4")){
                agregarCartaMano(4, manoMaquina);
                System.out.println("Se ha agregado cuatro cartas al oponente!");
            }else if(c.getValor().equals("^") || c.getValor().equals("&") ){
                actualizarTurno(false);
                System.out.println("La maquina pierde el turno!");
            }
            actualizarColor(color);
        }
    }

    public static void actualizarColor(Color c){
        color = c;
    }

    public static void actualizarTurno(boolean t){
        turno = t;
    }

    public boolean lanzarCarta(Carta c, Boolean maquina){
        if(c.getColor() == color || c.getValor().equals(lineaJuego.get(0).getValor())){
            lineaJuego.add(0,c);
            if(maquina){
                manoMaquina.remove(c);
            }else{
                jugador.getCartas().remove(c);
            }
            actualizarColor(color);
            
            return true;
        }else{
            return false;
        }
    }

    public boolean lanzarCartaComodin(Carta c, boolean maquina, Scanner sc){
        if(maquina){
            if(c.getColor() == color || c.getColor() == Color.N){
                lanzarComodinMaquina(c, color);
            }else{
                return false;
            }
            lineaJuego.add(0,c);
            manoMaquina.remove(c);
        }else{
            if(c.getColor() == color || c.getColor() == Color.N){
                lanzarComodinJugador(c, color, sc);
            }else{
                return false;
            }
            lineaJuego.add(0,c);
            jugador.getCartas().remove(c);
        }
        return true;
    }


    public boolean agregarCartaLinea(Carta c, Scanner sc, Boolean maquina){
        if(c == null){
            return false;
        }
        if(maquina){
            System.out.println("La maquina escogió:" + c);
        }
        if(c instanceof CartaNumerica){
            return lanzarCarta(c, maquina);
        }else{
            return lanzarCartaComodin(c, maquina, sc);
            
        }
    }

    public void reiniciarBaraja(){
        if(baraja.getCartas().size() <= 10){
            for(int i= 1; i < lineaJuego.size();){
                baraja.getCartas().add(lineaJuego.get(i));
                lineaJuego.remove(i);
            }
            baraja.mezclarCartas();
            Utilitaria.esperar(1);
            System.out.println("Se han tomado las cartas de la linea de juego ya que estaba por terminarse las cartas en la baraja!");
            Utilitaria.esperar(1);
            System.out.println("Se han mezclado las cartas de la baraja!");
        }
    }

    public boolean validarCartaJugador(boolean continuar, Scanner sc){
        if(continuar == false){
            System.out.println("Error, el color o numero no coincide. Intentar de nuevo? (Si/No)");
            String r = sc.nextLine();
            if(r.equalsIgnoreCase("si")){
                return true;
            }else{
                jugador.getCartas().add(baraja.getCarta(0));
                baraja.getCartas().remove(0);
                System.out.println("Se ha agregado una carta a tu mano!");
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean continuarJuego(Jugador jugador){
        if(jugador.getCartas().size() == 1 || manoMaquina.size() == 1){
            System.out.println("UNO!");
        }
        if(jugador.getCartas().size() == 0){
            System.out.println("Ganaste!");
            return false;
        }
        if(manoMaquina.size() == 0){
            System.out.println("Gano la máquina!");
            return false;
        }
        return true;
    }

    public void verificarCartas(Jugador jugador){
        if(jugador.getCartas().size() == 0){
            actualizarTurno(false);
        }
        
    }


    public void iniciarJuego(){
        repartirCartas();
        System.out.println("Se han repartido las cartas!");
        lineaJuego.add(tomarCartaAzar());
        actualizarColor(lineaJuego.get(0).getColor());
        System.out.println("Se ha puesto una carta al azar en la linea de juego!");
        boolean repetir = false;
        Scanner sc = new Scanner(System.in);


        while(continuarJuego(jugador)){
            if(turno){
                do{
                    Utilitaria.esperar(1);
                    jugador.imprimirMano();
                    mostrarLinea();
                    Utilitaria.esperar(1);
                    System.out.println("Indique el indice de la carta que desea jugar: ");
                    int i = sc.nextInt();
                    sc.nextLine();
                    Carta c = jugador.getCarta(i);
                    boolean continuar = agregarCartaLinea(c, sc, false);
                    Utilitaria.esperar(1);
                    repetir = validarCartaJugador(continuar, sc);
                }while(repetir);

                verificarCartas(jugador);
                System.out.println();
            }else{
                actualizarTurno(true);
            }
            reiniciarBaraja();
            if(turno){
                Utilitaria.esperar(1);
                imprimirManoMaquina();
                mostrarLinea();
                Carta c = manoMaquina.get(0);
                boolean continuar = agregarCartaLinea(c, sc, true);
                Utilitaria.esperar(1);
                if(!continuar){
                    System.out.println("Error, el color o numero no coincide. Se ha agregado una carta al mazo de la maquina!");
                    manoMaquina.add(0,baraja.getCarta(0));
                    baraja.getCartas().remove(0);
                }
                System.out.println("");
            }else{
                actualizarTurno(true);
            }
            Utilitaria.esperar(1);


        }
        sc.close();
    }



    
}
