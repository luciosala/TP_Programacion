package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;

/**
 * M-01: interceptar un objetivo y completar su asistencia.
 *
 * Es exitosa cuando se establece contacto con el objetivo y se completa la
 * asistencia. Su accion final cuesta 5 de energia.
 */
public class MisionIntercepcion extends Mision {

    private boolean asistenciaCompletada = false;
    private boolean contactoEstablecido = false;
    private final String objetivo;
    private final String estadoObjetivo;

    /**
     * Construye la mision de intercepcion y asistencia.
     *
     * Precondiciones:
     * - asistente no es nulo.
     * - objetivo no es nulo, vacio ni contiene solo espacios.
     * - estadoObjetivo no es nulo, vacio ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La mision queda lista para ejecutarse una vez, sobre la nave de ese asistente.
     * - La mision conserva el objetivo y su estado para el informe.
     * - La nave no se modifica al construir la mision.
     *
     * @param asistente asistente que coordina la mision
     * @param objetivo nave o punto a interceptar
     * @param estadoObjetivo estado en que se encuentra el objetivo
     * @throws IllegalArgumentException si el asistente es nulo o si el objetivo o su estado son nulos o vacios
     */
    public MisionIntercepcion(AsistenteDeComando asistente, String objetivo, String estadoObjetivo){
        super("M-01 - Intercepción y asistencia", "Interceptar un objetivo y completar su asistencia", asistente);

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
