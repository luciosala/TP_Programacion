package ar.edu.unmdp.tpg.excepciones;

/**
 * Excepcion base de todas las fallas del dominio de la nave.
 *
 * Al ser checked, obliga a que quien invoca una operacion del dominio decida
 * explicitamente que hacer con el error. El Asistente de Comando puede capturar
 * este tipo para tratar cualquier falla de forma uniforme.
 */
public abstract class NaveException extends Exception {

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
    protected NaveException(String mensaje) {
        super(mensaje);
    }
}
