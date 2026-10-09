package ar.edu.unmdp.tpg.tripulacion;

import java.time.LocalDate;

/**
 * Consejo brindado por un consejero en una fecha determinada. Es inmutable: se
 * registra una vez y no cambia.
 */
public final class Consejo {

    private final LocalDate fecha;
    private final String descripcion;

    /**
     * Construye un consejo.
     *
     * Precondiciones:
     * - fecha no es nula.
     * - descripcion no es nula, vacia ni contiene solo espacios.
     *
     * Postcondiciones:
     * - El consejo conserva la fecha y la descripcion recibidas, y no cambia despues.
     *
     * @param fecha fecha en que se brindo el consejo
     * @param descripcion detalle del consejo
     * @throws IllegalArgumentException si la fecha es nula o la descripcion es nula o vacia
     */
    public Consejo(LocalDate fecha, String descripcion) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del consejo no puede ser nula");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del consejo no puede ser nula ni vacía");
        }

        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
