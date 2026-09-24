/**
 * Se lanza cuando una operacion dejaria un recurso por encima de su maximo
 * permitido.
 */
public class LimiteRecursoExcedidoException extends NaveException {

    public LimiteRecursoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
