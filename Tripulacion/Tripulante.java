import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Tripulante {

    private final String id;
    private final String nombre;
    private final String apellido;
    private final Cargo cargo;
    private final Origen origen;
    private int antiguedadAnios;
    private final List<Consejo> consejos = new ArrayList<>();

    /**
     * Construye un tripulante.
     *
     * Precondiciones de antigüedad:
     * - antiguedadAnios es mayor o igual a 0.
     * - El cargo no puede ser nulo.
     * - El origen no puede ser nulo.
     * - El id no puede ser nulo ni vacio.
     * - El apellido no puede ser nulo ni vacio.
     * - El nombre no puede ser nulo ni vacio.
     *
     * Postcondiciones de antigüedad:
     * - La antigüedad, el id, el nombre, el apellido, el cargo y el origen queda inicializado con lo ingresado.
     *
     * @param id identificador del tripulante
     * @param nombre nombre del tripulante
     * @param apellido apellido del tripulante
     * @param cargo cargo del tripulante
     * @param origen origen del tripulante
     * @param antiguedadAnios antigüedad en años
     * @throws IllegalArgumentException si incumple alguna precondición.
     */
    public Tripulante(String id, String nombre, String apellido, Cargo cargo, Origen origen, int antiguedadAnios) {

        if (antiguedadAnios < 0) {
            throw new IllegalArgumentException(
                "La antiguedad no puede ser negativa"
            );
        }

        if (cargo == null){
            throw new IllegalArgumentException("El cargo es inválido");
        }

        if (origen == null) {
            throw new IllegalArgumentException("El origen es inválido");
        }

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id no puede ser nulo ni vacío");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo ni vacío");
        }

        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cargo = cargo;
        this.origen = origen;
        this.antiguedadAnios = antiguedadAnios;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public Origen getOrigen() {
        return origen;
    }

    public int getAntiguedadAnios() {
        return antiguedadAnios;
    }

    public void registrarConsejo(LocalDate fecha, String descripcion) {
        if (!(cargo instanceof Consejero)) {
            throw new IllegalStateException("Solo los consejeros pueden registrar consejos");
        }
        consejos.add(new Consejo(fecha, descripcion));
    }

    public int cantidadConsejos() {
        return consejos.size();
    }

    public int cantidadConsejos(YearMonth periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El período no puede ser nulo");
        }

        int cantidad = 0;
        for (Consejo consejo : consejos) {
            if (YearMonth.from(consejo.getFecha()).equals(periodo)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public Consejo consultarConsejo(int indice) {
        return consejos.get(indice);
    }
}
