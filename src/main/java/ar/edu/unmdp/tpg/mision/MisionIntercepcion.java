package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;

import java.util.Random;

/**
 * M-01: llegar hasta una nave aliada y transferirle los recursos de auxilio que pide.
 *
 * Costos (Anexo de la catedra):
 * - Al despegar: 4 de combustible, 5 de energia y 4 de desgaste.
 * - Al llegar: transfiere a la aliada entre 0 y 50 de energia y entre 0 y 50 de
 *   combustible. Lo que pide la aliada se sortea al crear la mision.
 */
public class MisionIntercepcion extends Mision {

    public static final int ENERGIA_INICIAL = 5;
    public static final int MAXIMO_AUXILIO = 50;

    private static final Random azar = new Random();

    private final String objetivo;
    private final String estadoObjetivo;
    private final int energiaAEntregar;
    private final int combustibleAEntregar;

    /**
     * Construye la mision con un pedido de auxilio al azar (entre 0 y 50 de cada recurso).
     *
     * Precondiciones (assert):
     * - asistente y tablero no son nulos.
     * - objetivo y estadoObjetivo no son nulos ni vacios.
     */
    public MisionIntercepcion(AsistenteDeComando asistente, Tablero tablero, String objetivo, String estadoObjetivo) {
        this(asistente, tablero, objetivo, estadoObjetivo,
             azar.nextInt(MAXIMO_AUXILIO + 1), azar.nextInt(MAXIMO_AUXILIO + 1));
    }

    /**
     * Construye la mision con un pedido de auxilio fijo (sirve para las pruebas).
     *
     * Precondiciones (assert):
     * - energiaAEntregar y combustibleAEntregar estan entre 0 y 50.
     */
    public MisionIntercepcion(AsistenteDeComando asistente, Tablero tablero, String objetivo, String estadoObjetivo,
                              int energiaAEntregar, int combustibleAEntregar) {
        super("M-01 - Intercepción y asistencia", "Interceptar un objetivo y completar su asistencia", asistente, tablero);
        assert objetivo != null && !objetivo.isBlank() : "El objetivo no puede ser nulo ni vacío";
        assert estadoObjetivo != null && !estadoObjetivo.isBlank() : "El estado del objetivo no puede ser nulo ni vacío";
        assert energiaAEntregar >= 0 && energiaAEntregar <= MAXIMO_AUXILIO : "La energía de auxilio va de 0 a 50";
        assert combustibleAEntregar >= 0 && combustibleAEntregar <= MAXIMO_AUXILIO : "El combustible de auxilio va de 0 a 50";
        this.objetivo = objetivo;
        this.estadoObjetivo = estadoObjetivo;
        this.energiaAEntregar = energiaAEntregar;
        this.combustibleAEntregar = combustibleAEntregar;
    }

    @Override
    protected int energiaInicial() {
        return ENERGIA_INICIAL;
    }

    // La accion final es entregar el auxilio: esta energia se descuenta en el cierre.
    @Override
    protected int costoAccionFinal() {
        return energiaAEntregar;
    }

    @Override
    protected int combustibleAEntregar() {
        return combustibleAEntregar;
    }

    @Override
    protected Posicion destinoDeLaMision() {
        return tablero.obtenerObjetivo();
    }

    @Override
    protected String descripcionObjetivo() {
        return "Asistir a " + objetivo + " (estado: " + estadoObjetivo + "), que pide "
            + energiaAEntregar + " de energía y " + combustibleAEntregar + " de combustible";
    }

    @Override
    protected void alLlegar() {
        registrarAccion("Contacto con " + objetivo + " en la misma posición: se transfieren "
            + energiaAEntregar + " de energía y " + combustibleAEntregar + " de combustible");
    }
}
