/**
 * Se lanza cuando la tripulacion de la nave no cumple la composicion minima
 * exigida.
 */
public class TripulacionInvalidaException extends NaveException {

    public TripulacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
