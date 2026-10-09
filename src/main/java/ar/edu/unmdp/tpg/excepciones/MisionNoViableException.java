package ar.edu.unmdp.tpg.excepciones;

/**
 * Se lanza cuando la nave no esta en condiciones de iniciar una mision.
 */
public class MisionNoViableException extends NaveException {

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
    public MisionNoViableException(String mensaje) {
        super(mensaje);
    }
}
