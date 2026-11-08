package ar.edu.unmdp.tpg.acceso;

import java.time.Instant;

/**
 * Identifica al operador que inició sesión. Es lo que el controlador recibe
 * tras un login correcto (en lugar del DTO, que trae el hash y la sal).
 *
 * Invariantes:
 * - nombreUsuario no es nulo ni vacío; inicio no es nulo.
 * - Es inmutable.
 */
public final class Sesion {

    private final String nombreUsuario;
    private final Instant inicio;

    Sesion(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.inicio = Instant.now();
    }

    public String getNombreUsuario() { return nombreUsuario; }

    public Instant getInicio() { return inicio; }

    @Override
    public String toString() {
        return "Sesión de " + nombreUsuario + " (desde " + inicio + ")";
    }
}
