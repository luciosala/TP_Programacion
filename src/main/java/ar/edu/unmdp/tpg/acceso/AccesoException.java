package ar.edu.unmdp.tpg.acceso;

/**
 * Rechazo de una regla del acceso al sistema (usuario repetido, credenciales
 * incorrectas). No es una falla técnica: el controlador muestra el mensaje
 * al usuario tal cual y le deja volver a intentar.
 */
public abstract class AccesoException extends Exception {

    protected AccesoException(String mensaje) {
        super(mensaje);
    }
}