package ar.edu.unmdp.tpg.persistencia;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del DAO que guarda los usuarios en un Map, sin base de datos.
 *
 * Sirve para probar el servicio de acceso sin H2, y muestra la ventaja del
 * patrón DAO: el servicio funciona igual con esta clase o con UsuarioDAOJDBC.
 *
 * Los datos se pierden al cerrar el programa: NO usar en la aplicación real.
 */
public class UsuarioDAOEnMemoria implements UsuarioDAO {

    private final Map<String, UsuarioDTO> usuarios = new HashMap<>();

    @Override
    public void insertar(UsuarioDTO usuario) throws PersistenciaException {
        if (usuario == null)
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        if (usuarios.containsKey(usuario.getNombreUsuario()))
            throw new PersistenciaException("Nombre de usuario repetido: " + usuario.getNombreUsuario(), null);
        usuarios.put(usuario.getNombreUsuario(), usuario);
    }

    @Override
    public UsuarioDTO buscarPorNombre(String nombreUsuario) {
        return usuarios.get(nombreUsuario);   // null si no existe
    }

    @Override
    public boolean existe(String nombreUsuario) {
        return usuarios.containsKey(nombreUsuario);
    }
}