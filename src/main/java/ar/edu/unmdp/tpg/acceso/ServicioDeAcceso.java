package ar.edu.unmdp.tpg.acceso;

import ar.edu.unmdp.tpg.persistencia.PersistenciaException;
import ar.edu.unmdp.tpg.persistencia.UsuarioDAO;
import ar.edu.unmdp.tpg.persistencia.UsuarioDTO;

import java.util.Optional;

/**
 * Reglas del acceso al sistema: registrar usuarios e iniciar sesión.
 *
 * Reparto de responsabilidades (Guía general, sección 4.2):
 * - Vista: validaciones de formato (campos vacíos, largo, caracteres permitidos).
 * - Controlador: toma los datos de la vista, llama a este servicio y muestra el resultado.
 * - Servicio (esta clase): reglas y excepciones (nombre repetido, credenciales incorrectas)
 *   y el hash de la contraseña.
 * - DAO: solo las consultas a la base.
 *
 * El servicio depende de la interfaz UsuarioDAO, no de JDBC: se le puede pasar
 * UsuarioDAOJDBC (aplicación real) o UsuarioDAOEnMemoria (pruebas).
 * Tampoco usa consola ni Swing.
 */
public class ServicioDeAcceso {

    private final UsuarioDAO usuarioDAO;

    /**
     * @param usuarioDAO acceso a los datos de usuarios; no puede ser nulo
     */
    public ServicioDeAcceso(UsuarioDAO usuarioDAO) {
        if (usuarioDAO == null)
            throw new IllegalArgumentException("El servicio de acceso necesita un UsuarioDAO");
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Registra un usuario nuevo.
     *
     * Precondiciones:
     * - nombreUsuario y contrasena no son nulos ni vacíos (el formato fino lo valida la vista).
     *
     * Postcondiciones:
     * - Si no existía, el usuario queda guardado con el hash de su contraseña y
     *   puede iniciar sesión en esta y en futuras ejecuciones.
     * - Si ya existía, no se modifica nada.
     *
     * @throws IllegalArgumentException si algún dato es nulo o vacío
     * @throws UsuarioExistenteException si el nombre ya está registrado
     * @throws PersistenciaException si la base de datos falla
     */
    public void registrar(String nombreUsuario, String contrasena)
            throws UsuarioExistenteException, PersistenciaException {
        validarNoVacio(nombreUsuario, "El nombre de usuario");
        validarNoVacio(contrasena, "La contraseña");

        if (usuarioDAO.existe(nombreUsuario)) {
            throw new UsuarioExistenteException(nombreUsuario);
        }
        String sal = HashContrasena.generarSal();
        String hash = HashContrasena.calcular(contrasena, sal);
        usuarioDAO.insertar(new UsuarioDTO(nombreUsuario, hash, sal));
    }

    /**
     * Inicia la sesión de un usuario registrado.
     *
     * Precondiciones:
     * - nombreUsuario y contrasena no son nulos ni vacíos.
     *
     * Postcondiciones:
     * - No modifica la base.
     * - Si las credenciales son correctas, devuelve la sesión del operador.
     *
     * @return la sesión iniciada
     * @throws IllegalArgumentException si algún dato es nulo o vacío
     * @throws CredencialesInvalidasException si el usuario no existe o la contraseña no coincide
     * @throws PersistenciaException si la base de datos falla
     */
    public Sesion iniciarSesion(String nombreUsuario, String contrasena)
            throws CredencialesInvalidasException, PersistenciaException {
        validarNoVacio(nombreUsuario, "El nombre de usuario");
        validarNoVacio(contrasena, "La contraseña");

        Optional<UsuarioDTO> encontrado = usuarioDAO.buscarPorNombre(nombreUsuario);
        if (encontrado.isEmpty()) {
            throw new CredencialesInvalidasException();
        }
        UsuarioDTO usuario = encontrado.get();
        if (!HashContrasena.coincide(contrasena, usuario.getSal(), usuario.getHashContrasena())) {
            throw new CredencialesInvalidasException();
        }
        return new Sesion(usuario.getNombreUsuario());
    }

    private static void validarNoVacio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede ser nulo ni vacío");
        }
    }
}
