package ar.edu.unmdp.tpg.test;


import ar.edu.unmdp.tpg.destino.*;
import ar.edu.unmdp.tpg.elementos.*;
import ar.edu.unmdp.tpg.naves.NaveFactory;

import java.util.ArrayList;

public class PruebaTablero {

    public static void main(String[] args) throws Exception {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        for (int i = 0; i < 5; i++) elementos.add(new Asteroide(10));
        elementos.add(new EstacionDeCargaFija(20));
        elementos.add(NaveFactory.createNave("ENE-1", "combate"));

        Tablero tablero = new Tablero(7, 7, elementos);
        Destino destino = new Destino(tablero);

        // Observer (clase 11): el tablero avisa sus cambios a los observadores registrados
        tablero.agregarObservador(new ObservadorConsola());

        Posicion objetivo = tablero.obtenerObjetivo();
        dibujar(tablero, objetivo);

        System.out.println("\n--- turno del destino ---");
        destino.turnoDestino();
        dibujar(tablero, objetivo);
    }

    // @ = nave, X = objetivo, . = vacío, letra = inicial del elemento
    private static void dibujar(Tablero t, Posicion objetivo) {
        ElementoDelUniverso[][] m = t.verTablero();
        for (int i = 0; i < t.getX(); i++) {
            StringBuilder fila = new StringBuilder();
            for (int j = 0; j < t.getY(); j++) {
                Posicion p = new Posicion(i, j);
                char c = (m[i][j] == null) ? '.' : m[i][j].toString().charAt(0);
                if (p.equals(objetivo)) c = 'X';
                if (p.equals(t.getPosicionNave())) c = '@';
                fila.append(c).append(' ');
            }
            System.out.println(fila);
        }
    }
}