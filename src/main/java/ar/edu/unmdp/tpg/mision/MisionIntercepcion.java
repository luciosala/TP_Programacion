package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;

import java.util.Random;

/**
 * M-01: alcanzar a una nave aliada que solicita auxilio y entregarle recursos
 * (energía y/o combustible) para que pueda sobrevivir.
 *
 * Según el Anexo:
 * - Despegue: 4 de combustible, 5 de energía y 4 de desgaste.
 * - Al coincidir en la misma coordenada con la nave aliada se transfieren los
 *   recursos solicitados, entre 0 y 50 de energía y entre 0 y 50 de combustible.
 *   Si la nave los gastó en el trayecto, la misión falla.
 */
public class MisionIntercepcion extends Mision {

    /** Máximo de cada recurso que puede solicitar la nave aliada. */
    public static final int MAXIMO_SOLICITADO = 50;

    private final String naveAliada;
    private final String estadoAliada;
    private final int energiaSolicitada;
    private final int combustibleSolicitado;

    /**
     * Construye la misión con una solicitud de auxilio al azar (entre 0 y 50 de
     * energía y entre 0 y 50 de combustible).
     *
     * Precondiciones (assert):
     * - asistente y tablero no son nulos.
     * - naveAliada y estadoAliada no son nulos ni vacíos.
     *
     * @param asistente asistente que coordina la misión
     * @param tablero tablero de la travesía
     * @param naveAliada nombre de la nave que pide auxilio
     * @param estadoAliada estado en que se encuentra la nave aliada
     */
    public MisionIntercepcion(AsistenteDeComando asistente, Tablero tablero, String naveAliada, String estadoAliada) {
        this(asistente, tablero, naveAliada, estadoAliada,
             new Random().nextInt(MAXIMO_SOLICITADO + 1), new Random().nextInt(MAXIMO_SOLICITADO + 1));
    }

    /**
     * Construye la misión con una solicitud de auxilio conocida (para pruebas reproducibles).
     *
     * Precondiciones (assert):
     * - las de la otra versión del constructor.
     * - energiaSolicitada y combustibleSolicitado están entre 0 y 50.
     *
     * @param asistente asistente que coordina la misión
     * @param tablero tablero de la travesía
     * @param naveAliada nombre de la nave que pide auxilio
     * @param estadoAliada estado en que se encuentra la nave aliada
     * @param energiaSolicitada energía que solicita la nave aliada
     * @param combustibleSolicitado combustible que solicita la nave aliada
     */
    public MisionIntercepcion(AsistenteDeComando asistente, Tablero tablero, String naveAliada, String estadoAliada,
                              int energiaSolicitada, int combustibleSolicitado) {
        super("M-01 - Intercepción y asistencia", "Alcanzar a una nave aliada y transferirle recursos", asistente, tablero);
        assert naveAliada != null && !naveAliada.isBlank() : "La nave aliada no puede ser nula ni vacía";
        assert estadoAliada != null && !estadoAliada.isBlank() : "El estado de la nave aliada no puede ser nulo ni vacío";
        assert energiaSolicitada >= 0 && energiaSolicitada <= MAXIMO_SOLICITADO : "La energía solicitada debe estar entre 0 y 50";
        assert combustibleSolicitado >= 0 && combustibleSolicitado <= MAXIMO_SOLICITADO : "El combustible solicitado debe estar entre 0 y 50";

        this.naveAliada = naveAliada;
        this.estadoAliada = estadoAliada;
        this.energiaSolicitada = energiaSolicitada;
        this.combustibleSolicitado = combustibleSolicitado;
    }

    @Override
    protected int energiaDespegue() {
        return 5;
    }

    // La nave aliada espera en el objetivo que elige el tablero
    @Override
    protected Posicion destinoDeLaMision() {
        return tablero.obtenerObjetivo();
    }

    @Override
    protected String descripcionObjetivo() {
        return "Asistir a " + naveAliada + " (estado: " + estadoAliada + "), que solicita "
            + energiaSolicitada + " de energía y " + combustibleSolicitado + " de combustible";
    }

    @Override
    protected int energiaAEntregar() {
        return energiaSolicitada;
    }

    @Override
    protected int combustibleAEntregar() {
        return combustibleSolicitado;
    }

    @Override
    protected void alLlegar() {
        registrarAccion("Contacto con " + naveAliada + " en la misma coordenada: se le transferirán "
            + energiaSolicitada + " de energía y " + combustibleSolicitado + " de combustible");
    }
}
