/**
 * Excepcion base de todas las fallas del dominio de la nave.
 *
 * Al ser checked, obliga a que quien invoca una operacion del dominio decida
 * explicitamente que hacer con el error. El Asistente de Comando puede capturar
 * este tipo para tratar cualquier falla de forma uniforme.
 */
public abstract class NaveException extends Exception {

    protected NaveException(String mensaje) {
        super(mensaje);
    }
}
