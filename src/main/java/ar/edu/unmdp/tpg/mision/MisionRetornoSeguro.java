package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.destino.Posicion;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.excepciones.NaveException;
/**
 * M-03: regresar a la base de origen (punto de partida del recorrido, el centro
 * del tablero) para recargar recursos y reparar el desgaste.
 *
 * Según el Anexo:
 * - Despegue: 4 de combustible, 0 de energía y 4 de desgaste.
 * - Al llegar a la base no hay consumos adicionales; como recompensa se recargan
 *   al 100% la energía y el combustible y el mantenimiento deja el desgaste en 0.
 */
public class MisionRetornoSeguro extends Mision {

    private final String zonaDestino;

    /**
     * Construye la mision de retorno seguro.
     *
     * Precondiciones (assert):
     * - asistente no es nulo.
     * - zonaDestino no es nula, vacia ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La mision queda lista para ejecutarse una vez, sobre la nave de ese asistente.
     * - La mision conserva la zona de destino para el informe.
     * - La nave no se modifica al construir la mision.
     *
     * @param asistente asistente que coordina la mision
     * @param zonaDestino zona a la que debe regresar la nave
     */
    public MisionRetornoSeguro(AsistenteDeComando asistente, Tablero tablero, String zonaDestino) {
        super("M-03 - Retorno seguro", "Regresar a la base de origen para recargar y reparar la nave", asistente, tablero);
        assert zonaDestino != null && !zonaDestino.isBlank() : "La zona de destino no puede ser nula ni vacía";

        this.zonaDestino = zonaDestino;
    }

    // Anexo: el despegue de M-03 no consume energía
    @Override
    protected int energiaDespegue() {
        return 0;
    }

    // La base de origen es el punto de partida de la nave: el centro del tablero
    @Override
    protected Posicion destinoDeLaMision() {
        return new Posicion(tablero.getX() / 2, tablero.getY() / 2);
    }

    @Override
    protected String descripcionObjetivo() {
        return "Regresar de forma segura a " + zonaDestino;
    }

    @Override
    protected void alLlegar() {
        balance().registrarRecarga(100 - asistente.combustibleDisponible(),
                                   100 - asistente.energiaDisponible(),
                                   asistente.desgasteActual());
        registrarAccion("Base alcanzada: se recargarán los recursos al 100% y se reparará el desgaste");
    }

    @Override
    protected void impactarAlCerrar() throws NaveException {
        int faltaCombustible = 100 - asistente.combustibleDisponible();
        if (faltaCombustible > 0) {
            asistente.cargarCombustible(faltaCombustible);
        }
        int faltaEnergia = 100 - asistente.energiaDisponible();
        if (faltaEnergia > 0) {
            asistente.cargarEnergia(faltaEnergia);
        }
        asistente.realizarMantenimiento();
    }
}
