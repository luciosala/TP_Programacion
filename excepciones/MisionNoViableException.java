/**
 * Se lanza cuando la nave no esta en condiciones de iniciar una mision.
 */
public class MisionNoViableException extends NaveException {

    public MisionNoViableException(String mensaje) {
        super(mensaje);
    }
}
