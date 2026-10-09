package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;

/**
 * M-02: recolectar datos o una muestra en un punto de interes.
 *
 * Es exitosa cuando se obtiene el elemento y queda registrado como obtenido.
 * Su accion final cuesta 5 de energia.
 */
public class MisionRecoleccion extends Mision{

    private final String puntoInteres;
    private final String elemento;

    private boolean elementoObtenido;
    private boolean elementoRegistrado;

    /**
     * Construye la mision de recoleccion.
     *
     * Precondiciones:
     * - asistente no es nulo.
     * - puntoInteres no es nulo, vacio ni contiene solo espacios.
     * - elemento no es nulo, vacio ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La mision queda lista para ejecutarse una vez, sobre la nave de ese asistente.
     * - La mision conserva el punto de interes y el elemento para el informe.
     * - La nave no se modifica al construir la mision.
     *
     * @param asistente asistente que coordina la mision
     * @param puntoInteres punto donde se realiza la recoleccion
     * @param elemento dato o muestra a recolectar
     * @throws IllegalArgumentException si el asistente es nulo o si el punto de interes o el elemento son nulos o vacios
     */
    public MisionRecoleccion(AsistenteDeComando asistente, String puntoInteres, String elemento) {

        super("M-02 - Recolección", "Recolectar datos o una muestra en un punto de interés", asistente);

        if (puntoInteres == null || puntoInteres.isBlank()) {
            throw new IllegalArgumentException("El punto de interés no puede ser nulo ni vacío");
        }

        if (elemento == null || elemento.isBlank()) {
            throw new IllegalArgumentException("El elemento no puede ser nulo ni vacío");
        }

        this.puntoInteres = puntoInteres;
        this.elemento = elemento;
    }

    @Override
    protected void ejecutaMision() {
        registrarAccion("Aproximación completada al punto " + puntoInteres);

        elementoObtenido = true;
        registrarAccion("Elemento obtenido: " + elemento);

        elementoRegistrado = true;
        registrarAccion("Elemento registrado como obtenido");
    }

    @Override
    protected boolean condicionDeExito() {
        return elementoObtenido && elementoRegistrado;
    }

    @Override
    protected int costoAccionFinal() {
        return 5;
    }

    @Override
    protected String descripcionObjetivo() {
        return "Recolectar " + elemento + " en " + puntoInteres;
    }
}
