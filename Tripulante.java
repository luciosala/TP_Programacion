public class Tripulante {
    private final String id;
    private final String nombre;
    private final String apellido;
    private final String rango;
    private final Cargo cargo;
    private final Origen origen;
    private int antiguedadAnios;

    public Tripulante(String nombre, Cargo cargo, Origen origen, int antiguedadAnios) {
        if (antiguedadAnios < 0) {
            throw new IllegalArgumentException("La antiguedad no puede ser negativa");
        }
        this.nombre = nombre;
        this.cargo = cargo;
        this.origen = origen;
        this.antiguedadAnios = antiguedadAnios;
    }
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRango() { return rango; }
}