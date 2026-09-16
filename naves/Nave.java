import java.util.ArrayList;
import java.util.List;


abstract public class Nave {
    private final String id;
    private final String tipo;
    private final Recursos recursos;
    private final MotorWarp motorWarp;
    private final List<Tripulante> tripulacion = new ArrayList<>();

    public Nave(String id, String tipo, Recursos recursos, MotorWarp motorWarp) {
        this.id = id;
        this.tipo = tipo;
        this.recursos = recursos;
        this.motorWarp = motorWarp;
    }

    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public Recursos getRecursos() { return recursos; }
    public MotorWarp getMotorWarp() { return motorWarp; }
    
    public void agregarTripulante(Tripulante t) {
        if (t == null) {
            throw new IllegalArgumentException("El tripulante no puede ser nulo");
        }

        if (buscarTripulantePorId(t.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un tripulante con ese id en la nave");
        }

        tripulacion.add(t);
    }

    // Devuelve el tripulante encontrado, o null si no está en la nave.
    public Tripulante buscarTripulantePorId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id no puede ser nulo ni vacío");
        }

        for (Tripulante tripulante : tripulacion) {
            if (tripulante.getId().equals(id)) {
                return tripulante;
            }
        }

        return null;
    }

    public int cantidadTripulantes() {
        return tripulacion.size();
    }

    // se fija que haya un capitan y 4 integrantes mas
    public boolean tieneTripulacionMinima() {
        if (cantidadTripulantes() < 5) {
            return false;
        }

        for (Tripulante tripulante : tripulacion) {
            if (tripulante.getCargo() instanceof Capitan) {
                return true;
            }
        }

        return false;
    }

    public void validarTripulacionMinima() {
        if (!tieneTripulacionMinima()) {
            throw new IllegalStateException(
                "La nave requiere al menos un capitán y cuatro tripulantes adicionales"
            );
        }
    }
}
