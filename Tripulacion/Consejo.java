import java.time.LocalDate;

public final class Consejo {

    private final LocalDate fecha;
    private final String descripcion;

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
