import java.util.ArrayList;
import java.util.List;


abstract public class Nave {
    private final String id;
    private final String tipo;
    private final Recursos recursos;
    private final MotorWarp motorWarp;
    private final List<Tripulante> tripulacion = new ArrayList<>();

    /**
     * Construye una nave con identidad, tipo, recursos y motor Warp.
     *
     * Precondiciones:
     * - id y tipo no son nulos, vacíos ni contienen solo espacios.
     * - recursos y motorWarp no son nulos.
     *
     * Postcondiciones:
     * - La nave conserva el id y el tipo recibidos.
     * - La nave utiliza los recursos y el motorWarp recibidos.
     * - La tripulación comienza vacía.
     *
     * @param id identificador de la nave
     * @param tipo tipo de nave
     * @param recursos recursos que utilizará la nave
     * @param motorWarp motor Warp que utilizará la nave
     * @throws IllegalArgumentException si se incumple alguna precondición
     */
    public Nave(String id, String tipo, Recursos recursos, MotorWarp motorWarp) {
        
        if (id == null || id.isBlank()){
            throw new IllegalArgumentException("El id no puede ser nulo ni vacio");
        }

        if (tipo == null || tipo.isBlank()){
            throw new IllegalArgumentException("El tipo no puede ser nulo ni vacio");
        }

        if (recursos == null){
            throw new IllegalArgumentException("Los recursos no pueden ser nulos");
        }

        if (motorWarp == null){
            throw new IllegalArgumentException("El motor no puede ser nulo");
        }

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

    public void validarTripulacionMinima() throws TripulacionInvalidaException {
        if (!tieneTripulacionMinima()) {
            throw new TripulacionInvalidaException(
                "La nave requiere al menos un capitán y cuatro tripulantes adicionales"
            );
        }
    }

    /**
     * Indica si la nave conserva un estado operativo válido para E1.
     *
     * @return {@code true} si tiene la tripulación mínima y sus recursos
     *         permanecen dentro de los rangos permitidos
     */
    public boolean estaOperativa() {
        return tieneTripulacionMinima()
            && recursos.getCombustible() >= 0
            && recursos.getCombustible() <= 100
            && recursos.getEnergia() >= 0
            && recursos.getEnergia() <= 100
            && recursos.getDesgaste() >= 0
            && recursos.getDesgaste() <= 100;
    }
}
