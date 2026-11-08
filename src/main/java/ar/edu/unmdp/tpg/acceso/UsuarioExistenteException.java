package ar.edu.unmdp.tpg.acceso;

/** Se intentó registrar un nombre de usuario que ya está tomado. */
public class UsuarioExistenteException extends AccesoException {

    public UsuarioExistenteException(String nombreUsuario) {
        super("El usuario \"" + nombreUsuario + "\" ya está registrado");
    }
}

