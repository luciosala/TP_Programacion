package ar.edu.unmdp.tpg.destino;

/** Recibe avisos cuando se actualiza el tablero observado. */
public interface Observador {
    /** Ejecuta la actualización ante un cambio en el tablero. */
    void actualizar();
}