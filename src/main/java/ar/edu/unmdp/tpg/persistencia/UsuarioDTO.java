package ar.edu.unmdp.tpg.persistencia;

/**
 * PATRON DTO (Data Transfer Object): transporta los datos de un usuario entre
 * la base de datos y el servicio. No tiene comportamiento de negocio.
 *
 * Contiene exactamente lo que se guarda en la tabla "usuario". La contraseña
 * en texto plano NUNCA viaja en este objeto: solo su hash y la sal.
 *
 * Invariantes:
 * - nombreUsuario, hashContrasena y sal no son nulos ni vacíos.
 * - El objeto es inmutable.
 */
public final class UsuarioDTO {

    private final String nombreUsuario;
    private final String hashContrasena;
    private final String sal;

    /**
     * Precondiciones:
     * - Ningún parámetro es nulo ni vacío.
     *
     * @throws IllegalArgumentException si algún dato es nulo o vacío
     */
    public UsuarioDTO(String nombreUsuario, String hashContrasena, String sal) {
        if (nombreUsuario == null || nombreUsuario.isBlank())
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo ni vacío");
        if (hashContrasena == null || hashContrasena.isBlank())
            throw new IllegalArgumentException("El hash de la contraseña no puede ser nulo ni vacío");
        if (sal == null || sal.isBlank())
            throw new IllegalArgumentException("La sal no puede ser nula ni vacía");

        this.nombreUsuario = nombreUsuario;
        this.hashContrasena = hashContrasena;
        this.sal = sal;
    }

    public String getNombreUsuario() { return nombreUsuario; }

    public String getHashContrasena() { return hashContrasena; }

    public String getSal() { return sal; }

    @Override
    public String toString() {
        // No se muestra el hash ni la sal a propósito
        return "UsuarioDTO[" + nombreUsuario + "]";
    }
}