package ar.edu.unmdp.tpg.tripulacion;

/**
 * Fábrica de cargos (patrón Factory, igual que NaveFactory): crea el cargo a
 * partir de su nombre, así la vista y el controlador trabajan con el texto que
 * elige el usuario y no instancian las clases concretas.
 */
public class FabricaDeCargos {

    /** Nombres de los cargos admitidos (E1-04), en el orden en que se ofrecen. */
    public static final String[] NOMBRES = { "Capitán", "Consejero", "Teniente", "Alférez" };

    /**
     * Crea el cargo con ese nombre.
     *
     * Precondiciones (assert):
     * - nombre es uno de NOMBRES (sin distinguir mayúsculas).
     *
     * @param nombre nombre del cargo
     * @return el cargo correspondiente
     * @throws IllegalArgumentException si el nombre no corresponde a ningún cargo
     */
    public static Cargo crear(String nombre) {
        assert nombre != null : "El nombre del cargo no puede ser nulo";
        if (nombre.equalsIgnoreCase("Capitán")) {
            return new Capitan();
        } else if (nombre.equalsIgnoreCase("Consejero")) {
            return new Consejero();
        } else if (nombre.equalsIgnoreCase("Teniente")) {
            return new Teniente();
        } else if (nombre.equalsIgnoreCase("Alférez")) {
            return new Alferez();
        }
        throw new IllegalArgumentException("Cargo desconocido: " + nombre);
    }
}
