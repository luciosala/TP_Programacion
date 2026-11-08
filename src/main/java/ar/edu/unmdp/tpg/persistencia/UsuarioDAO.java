package ar.edu.unmdp.tpg.persistencia;

import java.util.Optional;

/**
 * PATRON DAO (Data Access Object): define QUÉ operaciones de datos existen sobre
 * los usuarios, sin decir CÓMO se hacen.
 *
 * El servicio de acceso depende solo de esta interfaz. Así se puede cambiar la
 * implementación (JDBC con H2, otra base, una versión en memoria para pruebas)
 * sin tocar el servicio ni el controlador.
 *
 * El DAO solo hace consultas: no valida contraseñas ni decide si un login es
 * correcto. Esas reglas son del servicio.
 */
public interface UsuarioDAO {

    /**
     * Guarda un usuario nuevo.
     *
     * Precondiciones:
     * - usuario no es nulo.
     * - No existe otro usuario con el mismo nombre (lo verifica el servicio).
     *
     * Postcondiciones:
     * - El usuario queda guardado y buscarPorNombre lo encuentra, también en
     *   ejecuciones futuras del programa.
     *
     * @throws PersistenciaException si la base falla o el nombre ya estaba usado
     */
    void insertar(UsuarioDTO usuario) throws PersistenciaException;

    /**
     * Busca un usuario por su nombre (distingue mayúsculas de minúsculas).
     *
     * Postcondiciones:
     * - No modifica la base.
     * - Devuelve Optional.empty() si no existe.
     *
     * @throws PersistenciaException si la base falla
     */
    Optional<UsuarioDTO> buscarPorNombre(String nombreUsuario) throws PersistenciaException;

    /**
     * @return true si ya hay un usuario registrado con ese nombre
     * @throws PersistenciaException si la base falla
     */
    boolean existe(String nombreUsuario) throws PersistenciaException;
}