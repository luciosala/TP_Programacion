/**
 * Se lanza cuando una operacion necesita mas recurso del que la nave tiene
 * disponible.
 */
public class RecursoInsuficienteException extends NaveException {

    public RecursoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
