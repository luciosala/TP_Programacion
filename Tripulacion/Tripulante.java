public class Tripulante {

    private final String id;
    private final String nombre;
    private final String apellido;
    private final String rango;
    private final Cargo cargo;
    private final Origen origen;
    private int antiguedadAnios;

    public Tripulante(String id, String nombre, String apellido,
                      String rango, Cargo cargo, Origen origen,
                      int antiguedadAnios) {

        if (antiguedadAnios < 0) {
            throw new IllegalArgumentException(
                "La antiguedad no puede ser negativa"
            );
        }

        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rango = rango;
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

    public String getRango() {
        return rango;
    }

    public Tripulante crear(Cargo cargo, String nombre, String apellido, Origen origen, int antiguedadAnios) {

        switch (cargo) {
            case CAPITAN:
                return new Capitan(nombre, apellido, origen, antiguedadAnios);

            case CONSEJERO:
                return new Consejero(nombre, apellido, origen, antiguedadAnios);

            case TENIENTE:
                return new Teniente(nombre, apellido, origen, antiguedadAnios);

            case ALFEREZ:
                return new Alferez(nombre, apellido, origen, antiguedadAnios);

            default:
                throw new IllegalArgumentException(
                    "Cargo desconocido: " + cargo
                );
        }
    }
}