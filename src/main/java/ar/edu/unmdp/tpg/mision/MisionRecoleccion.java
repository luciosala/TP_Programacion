package ar.edu.unmdp.tpg.mision;

import java.util.Random;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;

/**
 * M-02: recolectar datos o una muestra en un punto de interes.
 *
 * Costos (Anexo de la catedra):
 * - Al despegar: 4 de combustible, 10 de energia y 4 de desgaste.
 * - Al llegar: la nave extrae una recompensa de entre 20 y 100 gemas, al azar.
 */
public class MisionRecoleccion extends Mision{

    private final String puntoInteres;
    private final String elemento;

    /**
     * Construye la mision de recoleccion.
     *
     * Precondiciones (assert):
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
     */
    public MisionRecoleccion(AsistenteDeComando asistente, Tablero tablero, String puntoInteres, String elemento) {
        super("M-02 - Recolección", "Recolectar datos o una muestra en un punto de interés", asistente, tablero);

        assert puntoInteres != null && !puntoInteres.isBlank() : "El punto de interés no puede ser nulo ni vacío";
        assert elemento != null && !elemento.isBlank() : "El elemento no puede ser nulo ni vacío";

        this.puntoInteres = puntoInteres;
        this.elemento = elemento;
    }


    public static final int ENERGIA_INICIAL = 10;
    public static final int GEMAS_MINIMO = 20;
    public static final int GEMAS_MAXIMO = 100;

    @Override
    protected int energiaInicial() {
        return ENERGIA_INICIAL;
    }

    // La recoleccion no tiene costo al final: la energia se paga al despegar.
    @Override
    protected int costoAccionFinal() {
        return 0;
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

    private final Random random = new Random();
    private int gemasEncontradas = 0;

    @Override
    protected void alLlegar() {
        gemasEncontradas = GEMAS_MINIMO + random.nextInt(GEMAS_MAXIMO - GEMAS_MINIMO + 1);   // entre 20 y 100
        registrarAccion("Gemas encontradas en " + puntoInteres + ": " + gemasEncontradas);
    }

    @Override
    protected void impactarAlCerrar() {
        asistente.recolectarGemas(gemasEncontradas);
    }
}
