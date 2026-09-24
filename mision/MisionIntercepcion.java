public class MisionIntercepcion extends Mision {

    private boolean asistenciaCompletada = false;
    private boolean contactoEstablecido = false;
    private final String objetivo;
    private final String estadoObjetivo;

    public MisionIntercepcion(Nave nave, Bitacora bitacora, String objetivo, String estadoObjetivo){
        super("M-01 - Intercepción y asistencia", "Interceptar un objetivo y completar su asistencia", nave, bitacora);

        if (objetivo == null || objetivo.isBlank()) {
            throw new IllegalArgumentException(
                "El objetivo no puede ser nulo ni vacío"
            );
        }

        if (estadoObjetivo == null || estadoObjetivo.isBlank()) {
            throw new IllegalArgumentException(
                "El estado del objetivo no puede ser nulo ni vacío"
            );
        }

        this.objetivo = objetivo;
        this.estadoObjetivo = estadoObjetivo;
    }
   @Override
    protected void ejecutaMision() {
        registrarAccion("Aproximación completada hasta " + objetivo);
        registrarAccion("Estado del objetivo verificado: " + estadoObjetivo);
        contactoEstablecido = true;
        asistenciaCompletada = true;
        registrarAccion("Asistencia completada");
    }

    @Override
    protected boolean condicionDeExito() {
        return contactoEstablecido && asistenciaCompletada;
    }

    @Override
    protected int costoAccionFinal() {
        return 5;
    }

    @Override
    protected String descripcionObjetivo() {
        return "Asistir a " + objetivo + " (estado: " + estadoObjetivo + ")";
    }
}
