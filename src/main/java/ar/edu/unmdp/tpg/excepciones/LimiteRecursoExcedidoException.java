package ar.edu.unmdp.tpg.excepciones;

/**
 * Se lanza cuando una operacion dejaria un recurso por encima de su maximo
 * permitido.
 */
public class LimiteRecursoExcedidoException extends NaveException {

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
    public LimiteRecursoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
