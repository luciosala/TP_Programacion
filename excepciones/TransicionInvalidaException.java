/**
 * Se lanza cuando se pide al Motor Warp una transicion que el estado actual no
 * admite.
 */
public class TransicionInvalidaException extends NaveException {

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
