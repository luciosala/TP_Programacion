package ar.edu.unmdp.tpg.elementos;
import ar.edu.unmdp.tpg.naves.Nave;

public interface ElementoDelUniverso {
    // Aplica a la nave las consecuencias del encuentro.
    // Devuelve un texto con lo que pasó, para que la misión lo anote en la bitácora.
    String interactuarCon(Nave nave);
}
