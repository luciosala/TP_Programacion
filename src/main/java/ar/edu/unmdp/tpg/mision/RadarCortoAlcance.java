package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;

/**
 * Radar de corto alcance: observa las 8 casillas que rodean a la nave
 * (Tablero.observarAlrededores) para evaluar el entorno inmediato.
 * Solo lee el tablero: no controla la nave ni decide el movimiento.
 */
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

    /**
     * Indica si en la casilla vecina indicada hay un obstáculo.
     *
     * Precondiciones (assert):
     * - siguiente no es nula.
     *
     * Postcondiciones:
     * - Devuelve false si la casilla no es vecina de la nave (no la alcanza el radar).
     * - No modifica el tablero.
     *
     * @param tablero tablero donde está la nave
     * @param siguiente próxima casilla de la ruta
     * @return true si en esa casilla hay un elemento que es obstáculo
     */
    public boolean hayObstaculoEn(Tablero tablero, Posicion siguiente) {
        assert siguiente != null : "La posición a revisar no puede ser nula";
        Posicion nave = tablero.getPosicionNave();
        int dx = siguiente.getX() - nave.getX();
        int dy = siguiente.getY() - nave.getY();
        if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
            return false;
        }
        // La nave está en [1][1]; la vecina en [1 + dx][1 + dy]
        ElementoDelUniverso elemento = tablero.observarAlrededores()[1 + dx][1 + dy];
        return elemento != null && elemento.esObstaculo();
    }
}
