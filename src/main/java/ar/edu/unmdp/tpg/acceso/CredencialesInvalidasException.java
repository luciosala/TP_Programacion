package ar.edu.unmdp.tpg.acceso;

/**
 * El inicio de sesión falló.
 *
 * El mensaje es el mismo si el usuario no existe o si la contraseña está mal:
 * así no se le revela a un extraño qué nombres de usuario están registrados.
 */
public class CredencialesInvalidasException extends AccesoException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos");
    }
}

