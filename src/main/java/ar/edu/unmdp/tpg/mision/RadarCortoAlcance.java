package ar.edu.unmdp.tpg.mision;

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
}