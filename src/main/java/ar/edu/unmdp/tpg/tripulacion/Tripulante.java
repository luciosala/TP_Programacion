package ar.edu.unmdp.tpg.tripulacion;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Integrante de la tripulacion: su identidad, su cargo, su origen, su antiguedad y
 * los consejos que brindo si es consejero.
 *
 * Invariantes:
 * - El id, el nombre, el apellido, el cargo y el origen no cambian.
 * - La antiguedad nunca es negativa.
 * - Solo un tripulante con cargo Consejero puede tener consejos registrados.
 */
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

    /**
     * Registra un consejo brindado por el tripulante.
     *
     * Precondiciones:
     * - El cargo del tripulante es Consejero.
     * - fecha no es nula.
     * - descripcion no es nula, vacia ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La cantidad de consejos aumenta exactamente en uno y los anteriores
     *   permanecen en el mismo orden.
     * - El consejo queda contado en la cantidad del periodo al que pertenece su fecha.
     * - Si se rechaza, la lista de consejos permanece sin cambios.
     *
     * @param fecha fecha en que se brindo el consejo
     * @param descripcion detalle del consejo
     * @throws IllegalStateException si el tripulante no es consejero
     * @throws IllegalArgumentException si la fecha es nula o la descripcion es nula o vacia
     */
    public void registrarConsejo(LocalDate fecha, String descripcion) {
        if (!(cargo instanceof Consejero)) {
            throw new IllegalStateException("Solo los consejeros pueden registrar consejos");
        }
        consejos.add(new Consejo(fecha, descripcion));
    }

    /**
     * Cuenta todos los consejos registrados por el tripulante, de cualquier periodo.
     *
     * Postcondiciones:
     * - El tripulante no se modifica.
     * - Devuelve un valor mayor o igual a 0; es 0 para quien no es consejero.
     *
     * @return cantidad total de consejos registrados
     */
    public int cantidadConsejos() {
        return consejos.size();
    }

    /**
     * Cuenta los consejos que el tripulante registro dentro de un periodo mensual.
     *
     * Precondiciones:
     * - periodo no es nulo.
     *
     * Postcondiciones:
     * - El tripulante no se modifica.
     * - Devuelve cuantos consejos tienen fecha dentro de ese periodo; los de otros
     *   periodos no se cuentan.
     * - El valor devuelto no supera cantidadConsejos().
     *
     * @param periodo periodo mensual a contar
     * @return cantidad de consejos de ese periodo
     * @throws IllegalArgumentException si el periodo es nulo
     */
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

    /**
     * Consulta un consejo del tripulante por su indice, empezando en cero.
     *
     * Precondiciones:
     * - indice esta entre 0 y cantidadConsejos()-1.
     *
     * Postcondiciones:
     * - El tripulante no se modifica.
     * - Devuelve el consejo registrado en esa posicion, en orden de registro.
     *
     * @param indice posicion del consejo
     * @return el consejo pedido
     * @throws IndexOutOfBoundsException si el indice esta fuera de rango
     */
    public Consejo consultarConsejo(int indice) {
        return consejos.get(indice);
    }
}
