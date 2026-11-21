package ar.edu.unmdp.tpg.elementos;
import ar.edu.unmdp.tpg.naves.Nave;

public interface ElementoDelUniverso {
    // Aplica a la nave las consecuencias del encuentro.
    // Devuelve un texto con lo que pasó, para que la misión lo anote en la bitácora.
    String interactuarCon(Nave nave);

    // Si el radar de corto alcance lo ve en la próxima casilla, ¿la nave tiene que
    // esquivarlo con el Motor Warp? Por defecto no; el Asteroide dice que sí.
    default boolean esObstaculo() {
        return false;
    }
}
