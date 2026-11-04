package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;

/**
 * M-02: recolectar datos o una muestra en un punto de interes.
 *
 * Es exitosa cuando se obtiene el elemento y queda registrado como obtenido.
 * Su accion final cuesta 5 de energia.
 */
public class MisionRecoleccion extends Mision{

    private final String puntoInteres;
    private final String elemento;

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
    public MisionRecoleccion(AsistenteDeComando asistente, Tablero tablero, String puntoInteres, String elemento) {
        super("M-02 - Recolección", "Recolectar datos o una muestra en un punto de interés", asistente, tablero);

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
    protected int costoAccionFinal() {
        return 5;
    }

    // Las riquezas están en el objetivo que elige el tablero
    @Override
    protected Posicion destinoDeLaMision() {
        return tablero.obtenerObjetivo();
    }

    @Override
    protected String descripcionObjetivo() {
        return "Recolectar " + elemento + " en " + puntoInteres;
    }
}
