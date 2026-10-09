package ar.edu.unmdp.tpg.haberes;

import ar.edu.unmdp.tpg.tripulacion.Consejero;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;

import java.time.YearMonth;

/**
 * Arma el haber de un tripulante para un periodo, encadenando los decoradores que
 * le corresponden segun su cargo, su antiguedad y su origen.
 *
 * Es el unico lugar que decide que conceptos entran en una liquidacion: quien pide
 * un haber no necesita conocer el orden ni la combinacion de decoradores.
 */
public final class LiquidacionHaberes {

    private final YearMonth periodo;

    /**
     * Construye una liquidacion para un periodo determinado.
     *
     * Precondiciones:
     * - periodo no es nulo.
     *
     * Postcondiciones:
     * - La liquidacion queda asociada a ese periodo y no cambia despues.
     *
     * @param periodo periodo mensual a liquidar
     * @throws IllegalArgumentException si el periodo es nulo
     */
    public LiquidacionHaberes(YearMonth periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El período de liquidación no puede ser nulo");
        }
        this.periodo = periodo;
    }

    /**
     * Informa el periodo de esta liquidacion.
     *
     * Postcondiciones:
     * - Devuelve siempre el mismo periodo: la liquidacion no se modifica.
     *
     * @return periodo mensual que se liquida
     */
    public YearMonth getPeriodo() {
        return periodo;
    }

    /**
     * Arma el haber del tripulante para el periodo de esta liquidacion.
     *
     * Precondiciones:
     * - tripulante no es nulo.
     *
     * Postcondiciones:
     * - El haber devuelto incluye, en este orden: el sueldo base del cargo, el
     *   adicional por antiguedad, el subsidio por origen y, solo si el cargo es
     *   Consejero, el adicional por los consejos del periodo.
     * - La cantidad de conceptos es 3 para los cargos que no son Consejero y 4 para Consejero.
     * - El total del haber es finito y no negativo, e igual a la suma de sus conceptos.
     * - El tripulante no se modifica.
     *
     * @param tripulante tripulante a liquidar
     * @return el haber compuesto por los conceptos que le corresponden
     * @throws IllegalArgumentException si el tripulante es nulo
     */
    public Haber liquidar(Tripulante tripulante) {
        Haber haber = new HaberBase(tripulante);
        haber = new AdicionalAntiguedad(haber, tripulante);
        haber = new SubsidioPorOrigen(haber, tripulante.getOrigen());

        if (tripulante.getCargo() instanceof Consejero) {
            haber = new AdicionalConsejos(haber, tripulante, periodo);
        }
        return haber;
    }
}
