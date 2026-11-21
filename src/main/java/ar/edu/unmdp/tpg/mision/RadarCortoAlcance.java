package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;

public class RadarCortoAlcance {

    // Cuántos elementos hay en las 8 casillas que rodean a la nave
    public int contarElementosCercanos(Tablero tablero) {
        ElementoDelUniverso[][] alrededores = tablero.observarAlrededores();
        int cantidad = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boolean esLaNave = (i == 1 && j == 1);
                if (!esLaNave && alrededores[i][j] != null) {
                    cantidad++;
                }
            }
        }
        return cantidad;
    }

    // ¿Hay un obstáculo en la casilla a la que la nave va a saltar?
    // La próxima casilla de la ruta es vecina de la nave, así que está en la matriz de 3x3
    // que devuelve observarAlrededores(), con la nave en [1][1].
    public boolean hayObstaculoEn(Tablero tablero, Posicion siguiente) {
        Posicion nave = tablero.getPosicionNave();
        int fila = 1 + siguiente.getX() - nave.getX();
        int columna = 1 + siguiente.getY() - nave.getY();
        if (fila < 0 || fila > 2 || columna < 0 || columna > 2) {
            return false;   // no es una casilla vecina: el radar de corto alcance no llega
        }
        ElementoDelUniverso elemento = tablero.observarAlrededores()[fila][columna];
        return elemento != null && elemento.esObstaculo();
    }
}
