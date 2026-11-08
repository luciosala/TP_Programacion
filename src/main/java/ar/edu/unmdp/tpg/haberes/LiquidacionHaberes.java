package ar.edu.unmdp.tpg.haberes;

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
     * Precondiciones (assert):
     * - periodo no es nulo.
     *
     * Postcondiciones:
     * - La liquidacion queda asociada a ese periodo y no cambia despues.
     *
     * @param periodo periodo mensual a liquidar
     */
    public LiquidacionHaberes(YearMonth periodo) {
        assert periodo != null : "El período de liquidación no puede ser nulo";
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
     * Precondiciones (assert):
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
     */
    public Haber liquidar(Tripulante tripulante) {
        assert tripulante != null : "El tripulante no puede ser nulo";

        Haber haber = new HaberBase(tripulante);
        haber = new AdicionalAntiguedad(haber, tripulante);
        haber = new SubsidioPorOrigen(haber, tripulante.getOrigen());

        if (tripulante.esConsejero()) {
            haber = new AdicionalConsejos(haber, tripulante, periodo);
        }

        assert haber.cantidadConceptos() == (tripulante.esConsejero() ? 4 : 3)
            : "Fallo postcondición: cantidad de conceptos incorrecta";
        assert Math.abs(haber.calcularTotal() - sumaDeConceptos(haber)) < 1e-9
            : "Fallo postcondición: el total debe ser la suma de sus conceptos";
        return haber;
    }

    // Suma los importes de cada concepto, para verificar la postcondicion del total.
    private static double sumaDeConceptos(Haber haber) {
        double suma = 0;
        for (int i = 0; i < haber.cantidadConceptos(); i++) {
            suma += haber.consultarImporteConcepto(i);
        }
        return suma;
    }
}
