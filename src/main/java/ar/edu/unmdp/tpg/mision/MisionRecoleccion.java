package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;

import java.util.Random;

/**
 * M-02: llegar a una coordenada donde hay riquezas espaciales y recolectar gemas.
 *
 * Según el Anexo:
 * - Despegue: 4 de combustible, 10 de energía y 4 de desgaste.
 * - Al arribar al destino, la nave extrae una recompensa de entre 20 y 100 gemas.
 */
public class MisionRecoleccion extends Mision {

    /** Mínimo de gemas que se extraen al llegar. */
    public static final int GEMAS_MINIMAS = 20;
    /** Máximo de gemas que se extraen al llegar. */
    public static final int GEMAS_MAXIMAS = 100;

    private final String puntoInteres;
    private final Random azar = new Random();
    private int gemasEncontradas = 0;

    /**
     * Construye la misión de recolección.
     *
     * Precondiciones (assert):
     * - asistente y tablero no son nulos.
     * - puntoInteres no es nulo ni vacío.
     *
     * @param asistente asistente que coordina la misión
     * @param tablero tablero de la travesía
     * @param puntoInteres nombre del lugar donde están las riquezas
     */
    public MisionRecoleccion(AsistenteDeComando asistente, Tablero tablero, String puntoInteres) {
        super("M-02 - Recolección", "Llegar a las riquezas espaciales y recolectar gemas", asistente, tablero);
        assert puntoInteres != null && !puntoInteres.isBlank() : "El punto de interés no puede ser nulo ni vacío";
        this.puntoInteres = puntoInteres;
    }

    @Override
    protected int energiaDespegue() {
        return 10;
    }

    // Las riquezas están en el objetivo que elige el tablero
    @Override
    protected Posicion destinoDeLaMision() {
        return tablero.obtenerObjetivo();
    }

    @Override
    protected String descripcionObjetivo() {
        return "Recolectar gemas en " + puntoInteres;
    }

    @Override
    protected void alLlegar() {
        gemasEncontradas = GEMAS_MINIMAS + azar.nextInt(GEMAS_MAXIMAS - GEMAS_MINIMAS + 1);   // entre 20 y 100
        balance().registrarGemas(gemasEncontradas);
        registrarAccion("Gemas extraídas en " + puntoInteres + ": " + gemasEncontradas);
    }

    @Override
    protected void impactarAlCerrar() {
        asistente.recolectarGemas(gemasEncontradas);
    }
}
