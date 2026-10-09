package ar.edu.unmdp.tpg.excepciones;

/**
 * Se lanza cuando la tripulacion de la nave no cumple la composicion minima
 * exigida.
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
