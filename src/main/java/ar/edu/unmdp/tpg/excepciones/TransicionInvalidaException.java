package ar.edu.unmdp.tpg.excepciones;

/**
 * Se lanza cuando se pide al Motor Warp una transicion que el estado actual no
 * admite.
 */
public class TransicionInvalidaException extends NaveException {

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
    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
