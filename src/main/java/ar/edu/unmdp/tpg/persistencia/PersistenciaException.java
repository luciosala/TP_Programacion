package ar.edu.unmdp.tpg.persistencia;

/**
 * Falla técnica al acceder a la base de datos (no se pudo conectar, la consulta
 * falló, no se encontró el esquema, etc.).
 *
 * Envuelve a SQLException para que las capas de arriba (servicio, controlador)
 * no dependan de java.sql: solo saben que "la persistencia falló".
 *
 * Es checked: quien usa un DAO tiene que decidir qué hacer si la base no responde.
 */
public class PersistenciaException extends Exception {

    /**
     * @param mensaje descripción de la operación que falló
     * @param causa excepción original (normalmente una SQLException)
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
