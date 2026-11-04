package ar.edu.unmdp.tpg.test;

import ar.edu.unmdp.tpg.destino.Observador;

/**
 * Observador de prueba para el Tablero de la catedra: cada vez que el tablero
 * notifica un cambio, lo avisa por consola (patron Observer, clase 11).
 */
public class ObservadorConsola implements Observador {

    @Override
    public void actualizar() {
        System.out.println("  [aviso] el tablero cambió");
    }
}
