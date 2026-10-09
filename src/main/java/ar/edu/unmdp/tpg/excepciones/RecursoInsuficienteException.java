package ar.edu.unmdp.tpg.excepciones;

/**
 * Se lanza cuando una operacion necesita mas recurso del que la nave tiene
 * disponible.
 */
public class RecursoInsuficienteException extends NaveException {

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
    public RecursoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
