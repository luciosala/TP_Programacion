package ar.edu.unmdp.tpg.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementación del DAO de usuarios con JDBC.
 *
 * Usa siempre PreparedStatement con parámetros "?": el nombre que escribe el
 * usuario nunca se concatena en el SQL, así se evita la inyección SQL.
 *
 * Toda SQLException se traduce a PersistenciaException, para que las capas de
 * arriba no dependan de java.sql.
 */
public class UsuarioDAOJDBC implements UsuarioDAO {

    private static final String SQL_INSERTAR =
        "INSERT INTO usuario (nombre_usuario, hash_contrasena, sal) VALUES (?, ?, ?)";
    private static final String SQL_BUSCAR =
        "SELECT nombre_usuario, hash_contrasena, sal FROM usuario WHERE nombre_usuario = ?";
    private static final String SQL_EXISTE =
        "SELECT 1 FROM usuario WHERE nombre_usuario = ?";

    private final ConexionBD conexionBD;

    /**
     * @param conexionBD de dónde sacar las conexiones; no puede ser nula
     */
    public UsuarioDAOJDBC(ConexionBD conexionBD) {
        if (conexionBD == null)
            throw new IllegalArgumentException("El DAO necesita una ConexionBD");
        this.conexionBD = conexionBD;
    }

    @Override
    public void insertar(UsuarioDTO usuario) throws PersistenciaException {
        if (usuario == null)
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        try (Connection conexion = conexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {
            sentencia.setString(1, usuario.getNombreUsuario());
            sentencia.setString(2, usuario.getHashContrasena());
            sentencia.setString(3, usuario.getSal());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el usuario " + usuario.getNombreUsuario(), e);
        }
    }

    @Override
    public Optional<UsuarioDTO> buscarPorNombre(String nombreUsuario) throws PersistenciaException {
        try (Connection conexion = conexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {
            sentencia.setString(1, nombreUsuario);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return Optional.empty();
                }
                return Optional.of(new UsuarioDTO(
                    fila.getString("nombre_usuario"),
                    fila.getString("hash_contrasena"),
                    fila.getString("sal")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo buscar el usuario " + nombreUsuario, e);
        }
    }

    @Override
    public boolean existe(String nombreUsuario) throws PersistenciaException {
        try (Connection conexion = conexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_EXISTE)) {
            sentencia.setString(1, nombreUsuario);
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el usuario " + nombreUsuario, e);
        }
    }
}