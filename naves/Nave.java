
abstract public class Nave {
    private final String id;
    private final String tipo;
    private final Recursos recursos;
    private final MotorWarp motorWarp;
    private int desgasteInicial = 0;
    private final List<Tripulante> tripulacion = new ArrayList<>();

    public Nave(String id, String tipo, Recursos recursos, MotorWarp motorWarp) {
        this.id = id;
        this.tipo = tipo;
        this.recursos = recursos;
        this.motorWarp = motorWarp;
    }

    public String getId() { return id; }
    public tipo getTipo() { return tipo; }
    public Recursos getRecursos() { return recursos; }
    public MotorWarp getMotorWarp() { return motorWarp; }
    
    public void agregarTripulante(Tripulante t) {
        tripulacion.add(t);
    }






}