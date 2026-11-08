package ar.edu.unmdp.tpg.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Sabe cómo abrir conexiones JDBC a la base de datos y crear el esquema.
 *
 * Cada operación del DAO abre su propia conexión y la cierra al terminar
 * (try-with-resources). Para una base H2 embebida y una aplicación de
 * escritorio esto es suficiente y evita dejar conexiones abiertas.
 *
 * Por defecto usa H2 en modo archivo: los datos quedan en ./datos/tpg.mv.db,
 * relativo a la carpeta desde donde se ejecuta el programa.
 */
public final class ConexionBD {

    /** Base en archivo: los usuarios sobreviven entre ejecuciones. */
    public static final String URL_POR_DEFECTO = "jdbc:h2:./datos/tpg";

    private static final String RECURSO_ESQUEMA = "/schema.sql";

    private final String url;
    private final String usuario;
    private final String contrasena;

    /**
     * Precondiciones:
     * - url no es nula ni vacía.
     *
     * @param url URL JDBC (por ejemplo jdbc:h2:./datos/tpg o jdbc:h2:mem:prueba)
     * @param usuario usuario de la base (en H2 por defecto "sa")
     * @param contrasena contraseña de la base (en H2 por defecto vacía)
     */
    public ConexionBD(String url, String usuario, String contrasena) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("La URL de la base no puede ser nula ni vacía");
        this.url = url;
        this.usuario = usuario == null ? "" : usuario;
        this.contrasena = contrasena == null ? "" : contrasena;
    }

    /** Conexión a la base H2 en archivo que usa el sistema normalmente. */
    public static ConexionBD porDefecto() {
        return new ConexionBD(URL_POR_DEFECTO, "sa", "");
    }

    /**
     * Abre una conexión nueva. Quien la pide es responsable de cerrarla.
     *
     * @throws SQLException si no se puede conectar (por ejemplo, falta el driver de H2)
     */
    Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    /**
     * Ejecuta database/schema.sql (copiado al classpath como /schema.sql).
     * Se puede llamar en cada arranque: el script usa CREATE TABLE IF NOT EXISTS.
     *
     * Postcondiciones:
     * - La tabla usuario existe. Los datos que ya había se conservan.
     *
     * @throws PersistenciaException si no se encuentra el script o la base falla
     */
    public void inicializarEsquema() throws PersistenciaException {
        String script = leerScript();
        try (Connection conexion = abrir();
             Statement sentencia = conexion.createStatement()) {
            for (String instruccion : separarInstrucciones(script)) {
                sentencia.execute(instruccion);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo crear el esquema de la base: " + e.getMessage(), e);
        }
    }

    private String leerScript() throws PersistenciaException {
        try (InputStream entrada = ConexionBD.class.getResourceAsStream(RECURSO_ESQUEMA)) {
            if (entrada == null) {
                throw new PersistenciaException(
                    "No se encontró " + RECURSO_ESQUEMA + " en el classpath (revisar <resources> del pom.xml)", null);
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer el esquema: " + e.getMessage(), e);
        }
    }

    // Saca los comentarios "--" y separa por ";"
    private static String[] separarInstrucciones(String script) {
        StringBuilder sinComentarios = new StringBuilder();
        for (String linea : script.split("\\R")) {
            int comentario = linea.indexOf("--");
            sinComentarios.append(comentario >= 0 ? linea.substring(0, comentario) : linea).append('\n');
        }
        return java.util.Arrays.stream(sinComentarios.toString().split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }
}