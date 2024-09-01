package com.example.unoproject;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    LinearLayout mazoMaquina;
    LinearLayout mazoJugador;
    LinearLayout linea;
    Button boton;
    private static ArrayList<Carta> lineaJuego = new ArrayList<>();
    private static boolean isPlayerBlocked = false;
    private static Carta.Color color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Se conecta las variables con los elementos del constraint
        mazoMaquina = (LinearLayout) findViewById(R.id.mazoMaquina);
        mazoJugador = (LinearLayout) findViewById(R.id.mazoJugador);
        linea = (LinearLayout) findViewById(R.id.lineaJuego);
        boton = findViewById(R.id.button);
        int width = 300;
        int height = 300;

        Jugador jugador = new Jugador("Kleber");
        Juego juego = new Juego(jugador);
        Timer timer = new Timer();
        juego.repartirCartas();
        Carta primerCarta = juego.tomarCartaAzar();
        lineaJuego.add(primerCarta);
        //Se toma el color de la primera carta y se establece ese color a la mesa central
        color = primerCarta.getColor();
        if(color == Carta.Color.R){
            linea.setBackgroundColor(Color.RED);
        } else if (color == Carta.Color.V) {
            linea.setBackgroundColor(Color.GREEN);
        }else if( color == Carta.Color.A){
            linea.setBackgroundColor(Color.YELLOW);
        }else if(color == Carta.Color.Z){
            linea.setBackgroundColor(Color.BLUE);
        }
        //Se inserta la carta como ImageView al Layout central "Linea"
        String p = primerCarta.getColorStr().toLowerCase() + primerCarta.getValor();
        int id = getResources().getIdentifier(p,"drawable",getPackageName());
        ImageView pCarta = new ImageView(this);
        pCarta.setImageResource(id);
        pCarta.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        linea.addView(pCarta);

        //Se le agrega el listener OnClick al boton el cual hará que si el jugador de click toma una carta de la baraja
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Carta c = juego.getBaraja().getCarta(0);
                jugador.getCartas().add(c);
                juego.getBaraja().getCartas().remove(0);
                insertarImv(c,width,height,mazoJugador,true,juego,jugador, timer);
                mostrarDialogoEmergente("Se te ha agregado una carta a tu mano!");

                // Este view postDelayed es para que lo que está dentro del run se tarde x tiempo de milisegundos en accionarse, en este caso son 2000 ms
                // Esto sirve para que, si el jugador toma una carta, pierde el turno y la maquina procede a hacer su movimiento
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        maquina(juego,jugador,width,height, timer);
                        if(juego.getManoMaquina().size() == 1){
                            mostrarDialogoEmergente("UNO!");
                        }else if(juego.getManoMaquina().isEmpty()){
                            mostrarDialogoEmergente("Ganó la maquina!");
                        }
                    }
                },2000);
            }
        });
        // Aqui se recorre el mazo de cartas del jugador para empezar a agregarlo al Layout MazoJugador con sus respectivos ImageView
        for(Carta c: jugador.getCartas()){
            ImageView imv =new ImageView(this);
            imv.setLayoutParams(new LinearLayout.LayoutParams(width,height));
            String nombre = c.getColorStr().toLowerCase();
            switch (c.getValor()) {
                case "^":
                    nombre += "reverse";
                    break;
                case "&":
                    nombre += "bloqueo";
                    break;
                case "%":
                    nombre += "cc";
                    break;
                case "+4":
                    nombre += "mas4";
                    break;
                case "+2":
                    nombre += "mas2";
                    break;
                default:
                    nombre += c.getValor();
                    break;
            }
            int idImagen = getResources().getIdentifier(nombre,"drawable",getPackageName());
            imv.setImageResource(idImagen);
            /* Aquí le agregamos la lógica a cada carta en el mazo del jugador. Esta logica se dispará cada vez que el jugador de click en alguna carta!*/
            imv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(c instanceof CartaNumerica){
                        /* Si la carta es numérica entra aquí(Jugador)*/
                        if(c.getColor() == color || c.getValor().equals(lineaJuego.get(0).getValor())){
                            lineaJuego.add(0,c);
                            color = c.getColor();
                            linea.removeAllViews();
                            insertarImv(c,width,height,linea,false,juego,jugador, timer);
                            mazoJugador.removeView(imv);
                            jugador.getCartas().remove(c);
                            if(color == Carta.Color.R){
                                linea.setBackgroundColor(Color.RED);
                            } else if (color == Carta.Color.V) {
                                linea.setBackgroundColor(Color.GREEN);
                            }else if( color == Carta.Color.A){
                                linea.setBackgroundColor(Color.YELLOW);
                            }else if(color == Carta.Color.Z){
                                linea.setBackgroundColor(Color.BLUE);
                            }
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            maquina(juego,jugador,width,height, timer);
                                            if(juego.getManoMaquina().size() == 1){
                                                mostrarDialogoEmergente("UNO!");
                                            }else if(juego.getManoMaquina().isEmpty()){
                                                mostrarDialogoEmergente("Ganó la maquina!");
                                            }
                                        }
                                    });
                                }
                            },1000);


                        }
                    }else{
                        /* Si la carta es comodin entra aquí (Jugador)*/
                        if(c.getColor() == color || c.getColor() == Carta.Color.N){
                            if(c.getColor() == Carta.Color.N){
                                mostrarDialogoEmergente(c,juego,jugador, timer);


                            }else{
                                switch (c.getValor()) {
                                    case "+2":
                                        agregarCartaMano(2, juego.getManoMaquina(), juego, mazoMaquina, false, jugador, timer);
                                        mostrarDialogoEmergente(2, false, timer, juego, jugador);

                                        break;
                                    case "+4":
                                        agregarCartaMano(4, juego.getManoMaquina(), juego, mazoMaquina, false, jugador, timer);
                                        mostrarDialogoEmergente(4, false, timer, juego, jugador);

                                        break;
                                    case "^":
                                    case "&":
                                        mostrarDialogoEmergente("La máquina pierde el turno!");
                                        break;
                                }
                                color = c.getColor();
                            }
                            linea.removeAllViews();
                            mazoJugador.removeView(imv);
                            jugador.getCartas().remove(c);
                            lineaJuego.add(0,c);
                            insertarImv(c,width,height,linea,false,juego,jugador, timer);
                            if(jugador.getCartas().size()== 1){
                                mostrarDialogoEmergente("UNO!");
                            }else if(jugador.getCartas().isEmpty()){
                                mostrarDialogoEmergente("GANASTE!");

                            }

                        }
                    }


                }


            });
            mazoJugador.addView(imv);
        }

        /* Aqui se empieza a crear los ImageView para el layout de la maquina*/
        for(Carta c:juego.getManoMaquina()){
            ImageView imv =new ImageView(this);
            imv.setLayoutParams(new LinearLayout.LayoutParams(width,height));
            String nombre = c.getColorStr().toLowerCase();
            switch (c.getValor()) {
                case "^":
                    nombre += "reverse";
                    break;
                case "&":
                    nombre += "bloqueo";
                    break;
                case "%":
                    nombre += "cc";
                    break;
                case "+4":
                    nombre += "mas4";
                    break;
                case "+2":
                    nombre += "mas2";
                    break;
                default:
                    nombre += c.getValor();
                    break;
            }
            imv.setTag(nombre);
            int idImagen = getResources().getIdentifier(nombre,"drawable",getPackageName());
            imv.setImageResource(idImagen);
            mazoMaquina.addView(imv);
        }



    }
    public void agregarCartaMano(int i, ArrayList<Carta> cartas, Juego juego, LinearLayout mazo,boolean bool,Jugador jugador, Timer timer){

        for(int j = 0; j < i; j++){
            Carta c = juego.getBaraja().getCarta(j);
            cartas.add(c);
            juego.getBaraja().getCartas().remove(j);
            insertarImv(c,300,300,mazo,bool,juego,jugador, timer);
        }
    }
    public void insertarImv(Carta c,int width,int height, LinearLayout mazo, LinearLayout mazo2){
        /* Este metodo es para insertar la imagen de la carta que lanza la maquina en la linea y quitárselo a el mismo*/
        ImageView imv =new ImageView(this);
        imv.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        String nombre = c.getColorStr().toLowerCase();
        switch (c.getValor()) {
            case "^":
                nombre += "reverse";
                break;
            case "&":
                nombre += "bloqueo";
                break;
            case "%":
                nombre += "cc";
                break;
            case "+4":
                nombre += "mas4";
                break;
            case "+2":
                nombre += "mas2";
                break;
            default:
                nombre += c.getValor();
                break;
        }
        imv.setTag(nombre);
        int idImagen = getResources().getIdentifier(nombre,"drawable",getPackageName());
        imv.setImageResource(idImagen);
        mazo.addView(imv);
        mazo2.removeView(mazo2.findViewWithTag(nombre));
    }
    public void insertarImv(Carta c,int width,int height, LinearLayout mazo,boolean bool, Juego juego, Jugador jugador, Timer timer){
        ImageView imv =new ImageView(this);
        imv.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        String nombre = c.getColorStr().toLowerCase();
        switch (c.getValor()) {
            case "^":
                nombre += "reverse";
                break;
            case "&":
                nombre += "bloqueo";
                break;
            case "%":
                nombre += "cc";
                break;
            case "+4":
                nombre += "mas4";
                break;
            case "+2":
                nombre += "mas2";
                break;
            default:
                nombre += c.getValor();
                break;
        }
        imv.setTag(nombre);
        int idImagen = getResources().getIdentifier(nombre,"drawable",getPackageName());
        imv.setImageResource(idImagen);
        if(bool){
            imv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(c instanceof CartaNumerica){
                        if(c.getColor() == color || c.getValor().equals(lineaJuego.get(0).getValor())){
                            lineaJuego.add(0,c);
                            color = c.getColor();
                            linea.removeAllViews();
                            insertarImv(c,width,height,linea,false,juego,jugador, timer);
                            mazoJugador.removeView(imv);
                            jugador.getCartas().remove(c);
                            if(color == Carta.Color.R){
                                linea.setBackgroundColor(Color.RED);
                            } else if (color == Carta.Color.V) {
                                linea.setBackgroundColor(Color.GREEN);
                            }else if( color == Carta.Color.A){
                                linea.setBackgroundColor(Color.YELLOW);
                            }else if(color == Carta.Color.Z){
                                linea.setBackgroundColor(Color.BLUE);
                            }
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            maquina(juego,jugador,width,height, timer);
                                            if(juego.getManoMaquina().size() == 1){
                                                mostrarDialogoEmergente("UNO!");
                                            }else if(juego.getManoMaquina().isEmpty()){
                                                mostrarDialogoEmergente("Ganó la maquina!");
                                            }
                                        }
                                    });
                                }
                            },1000);


                        }
                    }else{
                        if(c.getColor() == color || c.getColor() == Carta.Color.N){
                            if(c.getColor() == Carta.Color.N){
                                mostrarDialogoEmergente(c,juego,jugador, timer);


                            }else{
                                switch (c.getValor()) {
                                    case "+2":
                                        agregarCartaMano(2, juego.getManoMaquina(), juego, mazoMaquina, false, jugador, timer);
                                        mostrarDialogoEmergente(2, false, timer, juego, jugador);

                                        break;
                                    case "+4":
                                        agregarCartaMano(4, juego.getManoMaquina(), juego, mazoMaquina, false, jugador, timer);
                                        mostrarDialogoEmergente(4, false, timer, juego, jugador);

                                        break;
                                    case "^":
                                    case "&":
                                        mostrarDialogoEmergente("La máquina pierde el turno!");
                                        break;
                                }
                                color = c.getColor();
                            }
                            linea.removeAllViews();
                            mazoJugador.removeView(imv);
                            jugador.getCartas().remove(c);
                            lineaJuego.add(0,c);
                            insertarImv(c,width,height,linea,false,juego,jugador, timer);
                            if(jugador.getCartas().size()== 1){
                                mostrarDialogoEmergente("UNO!");
                            }else if(jugador.getCartas().isEmpty()){
                                mostrarDialogoEmergente("GANASTE!");

                            }

                        }
                    }


                }


            });
        }
        mazo.addView(imv);
    }

    public void maquina(Juego juego,Jugador jugador,int width,int height, Timer timer){
        if(isPlayerBlocked){
            /* Entra aquí cuando la maquina bloquea al jugador y tiene que volver a lanzar carta. Se usa recursividad aquí*/
            isPlayerBlocked = false;
            maquina(juego,jugador,width,height, timer);
        }else{
            /* Aquí empezara a buscar la primera carta que pueda lanzar*/
            for(Carta carta: juego.getManoMaquina()){
                if(carta instanceof CartaNumerica){
                    if(carta.getValor().equals(lineaJuego.get(0).getValor()) || carta.getColor() == lineaJuego.get(0).getColor()){
                        color = carta.getColor();
                        linea.removeAllViews();
                        insertarImv(carta,width,height,linea,mazoMaquina);
                        lineaJuego.add(0,carta);
                        juego.getManoMaquina().remove(carta);
                        if(color == Carta.Color.R){
                            linea.setBackgroundColor(Color.RED);
                        } else if (color == Carta.Color.V) {
                            linea.setBackgroundColor(Color.GREEN);
                        }else if( color == Carta.Color.A){
                            linea.setBackgroundColor(Color.YELLOW);
                        }else if(color == Carta.Color.Z){
                            linea.setBackgroundColor(Color.BLUE);
                        }
                        /* Los returns que ven aqui sirve para que, al encontrar una carta y lanzar, ya salga de la función.
                        * Si no le ponemos el return el for seguirá buscando y lanzando cartas. */
                        return;
                    }
                }else{
                    /* Aquí entra para las cartas comodin*/
                    if(carta.getColor() == color || carta.getColor() == Carta.Color.N ){
                        if(carta.getColor() == Carta.Color.N){
                            mostrarDialogoEmergenteMaquina(carta,jugador,juego,timer);

                            linea.removeAllViews();
                            lineaJuego.add(0,carta);
                            insertarImv(carta,width,height,linea,mazoMaquina);
                            juego.getManoMaquina().remove(carta);
                        }else{
                            switch (carta.getValor()) {
                                case "+2":
                                    agregarCartaMano(2, jugador.getCartas(), juego, mazoJugador, true, jugador, timer);
                                    mostrarDialogoEmergente(2, true, timer, juego, jugador);

                                    break;
                                case "+4":
                                    agregarCartaMano(4, jugador.getCartas(), juego, mazoJugador, true, jugador, timer);
                                    mostrarDialogoEmergente(4, true, timer, juego, jugador);

                                    break;
                                case "^":
                                case "&":
                                    mostrarDialogoEmergente("Perdiste el turno!");
                                    isPlayerBlocked = true;
                                    linea.removeAllViews();
                                    lineaJuego.add(0, carta);
                                    insertarImv(carta, width, height, linea, mazoMaquina);
                                    juego.getManoMaquina().remove(carta);
                                    maquina(juego, jugador, width, height, timer);
                                    return;
                            }
                            color = carta.getColor();
                            linea.removeAllViews();
                            lineaJuego.add(0,carta);
                            insertarImv(carta,width,height,linea,mazoMaquina);
                            juego.getManoMaquina().remove(carta);
                        }

                        return;
                    }



                }
            }
            /* Aqui entra cuando no encuentra ninguna carta para lanzar. Tomará una carta automaticamente de la baraja y perderá el turno*/
            Carta c = juego.getBaraja().getCarta(0);
            juego.getManoMaquina().add(c);
            juego.getBaraja().getCartas().remove(0);
            insertarImv(c,width,height,mazoMaquina,false,juego,jugador, timer);
            mostrarDialogoEmergente("La maquina no tiene cartas para jugar! Se le ha agregado una carta de la baraja!");
        }
    }



    private void mostrarDialogoEmergente(Carta c, Juego juego, Jugador jugador, Timer timer){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escoge un color");

        String[] colores = {"Rojo", "Azul", "Verde", "Amarillo"};

        builder.setItems(colores, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String colorSeleccionado = colores[i];
                aplicarColorSeleccionado(colorSeleccionado);

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(c.getValor().equals("+2")){
                    agregarCartaMano(2,juego.getManoMaquina(),juego,mazoMaquina,false,jugador, timer);
                    mostrarDialogoEmergente(2,false,timer,juego,jugador);
                }else if(c.getValor().equals("+4")){
                    agregarCartaMano(4,juego.getManoMaquina(),juego,mazoMaquina,false,jugador, timer);
                    mostrarDialogoEmergente(4,false,timer,juego,jugador);
                }else{
                    maquina(juego,jugador,300,300,timer);
                }
            }
        });
        dialog.show();

    }

    private void mostrarDialogoEmergenteMaquina(Carta carta, Jugador jugador, Juego juego, Timer timer){
        Random random = new Random();
        do{
            Carta.Color[] colores = Carta.Color.values();
            color = colores[random.nextInt(colores.length-1)];
        }while(color == Carta.Color.N);
        if(color == Carta.Color.R){
            linea.setBackgroundColor(Color.RED);
        } else if (color == Carta.Color.V) {
            linea.setBackgroundColor(Color.GREEN);
        }else if( color == Carta.Color.A){
            linea.setBackgroundColor(Color.YELLOW);
        }else if(color == Carta.Color.Z){
            linea.setBackgroundColor(Color.BLUE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("La máquina escogió el color: " + color);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(carta.getValor().equals("+2")){
                    agregarCartaMano(2,jugador.getCartas(),juego,mazoJugador,true,jugador, timer);
                    mostrarDialogoEmergente(2,true,timer,juego,jugador);
                }else if(carta.getValor().equals("+4")){
                    agregarCartaMano(4,jugador.getCartas(),juego,mazoJugador,true,jugador, timer);
                    mostrarDialogoEmergente(4,true,timer,juego,jugador);
                }
            }
        });
        dialog.show();
    }

    private void mostrarDialogoEmergente(int i, boolean maquina, Timer timer, Juego juego, Jugador jugador){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atención!");
        if(maquina){
            builder.setMessage("Se te ha agregado " + i + " cartas a tu mazo!");
        }else{
            builder.setMessage("Se ha agregado " + i + " cartas a la maquina!");
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(!maquina){
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    maquina(juego,jugador,300,300, timer);
                                    if(juego.getManoMaquina().size() == 1){
                                        mostrarDialogoEmergente("UNO!");
                                    }else if(juego.getManoMaquina().isEmpty()){
                                        mostrarDialogoEmergente("Ganó la maquina!");
                                    }
                                }
                            });
                        }
                    },1000);
                }
            }
        });
        dialog.show();

    }

    private void mostrarDialogoEmergente(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atención!");
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void aplicarColorSeleccionado(String c){
        switch(c){
            case "Rojo":
                color = Carta.Color.R;
                linea.setBackgroundColor(Color.RED);
                break;
            case "Azul":
                color = Carta.Color.Z;
                linea.setBackgroundColor(Color.BLUE);
                break;
            case "Amarillo":
                color = Carta.Color.A;
                linea.setBackgroundColor(Color.YELLOW);
                break;
            case "Verde":
                color = Carta.Color.V;
                linea.setBackgroundColor(Color.GREEN);
                break;
        }
    }







}