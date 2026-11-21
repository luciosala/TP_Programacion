package ar.edu.unmdp.tpg.elementos;
import ar.edu.unmdp.tpg.naves.Nave;

/**
 * Todo lo que la nave puede encontrarse en una casilla del tablero: otras naves,
 * asteroides y estaciones de carga (Enunciado Parte 2).
 */
public interface ElementoDelUniverso {
    // Aplica a la nave las consecuencias del encuentro.
    // Devuelve un texto con lo que pasó, para que la misión lo anote en la bitácora.
    String interactuarCon(Nave nave);

    // true si es un obstáculo que la nave intenta esquivar con un salto warp
    // (Anexo: "Si el radar de corto alcance detecta un obstáculo en la siguiente posición").
    boolean esObstaculo();

    // Abreviatura de tres letras para identificar el elemento en el mapa del tablero.
    String abreviatura();
}
