package ar.edu.unmdp.tpg.excepciones;

/**
 * Rechazo de dominio sobre la tripulacion. Se lanza cuando:
 * - la tripulacion no esta lista para operar (falta el capitan o los cuatro
 *   tripulantes adicionales), o
 * - se asigna a la nave un tripulante con un id que ya esta a bordo.
 */
public class TripulacionInvalidaException extends NaveException {

    /**
     * Construye la excepcion con el motivo de la falla.
     *
     * Precondiciones:
     * - mensaje describe por que se rechazo la operacion.
     *
     * Postcondiciones:
     * - La excepcion conserva ese mensaje, que es lo que el Asistente de Comando
     *   registra en la bitacora.
     *
     * @param mensaje motivo del rechazo
     */
    public TripulacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
