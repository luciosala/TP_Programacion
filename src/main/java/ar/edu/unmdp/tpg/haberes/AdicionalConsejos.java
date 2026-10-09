package ar.edu.unmdp.tpg.haberes;

import ar.edu.unmdp.tpg.tripulacion.Consejero;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;

import java.time.YearMonth;

/**
 * Concepto adicional de los consejeros: 2 PG por cada consejo registrado en el
 * periodo que se liquida.
 *
 * Solo cuenta los consejos cuya fecha cae dentro del periodo: los de otros meses
 * no se pagan en esta liquidacion.
 */
public class AdicionalConsejos extends DecoratorHaber {

    private final YearMonth periodo; //preguntar esto de YearMonth
    private final int cantidadConsejos;

    /**
     * Construye el concepto adicional por consejos del periodo.
     *
     * Precondiciones:
     * - haber no es nulo.
     * - tripulante no es nulo y su cargo es Consejero.
     * - periodo no es nulo.
     *
     * Postcondiciones:
     * - El adicional queda fijado en 2 PG por cada consejo del tripulante cuya fecha
     *   cae dentro del periodo; los de otros periodos no se cuentan.
     * - Si el consejero no registro consejos en el periodo, el adicional es 0.
     * - El importe del adicional es finito y no negativo, y no cambia despues.
     *
     * @param haber haber al que se agrega el adicional
     * @param tripulante consejero cuyos consejos se liquidan
     * @param periodo periodo de liquidacion
     * @throws IllegalArgumentException si el haber o el periodo son nulos, o si el tripulante no es consejero
     */
    public AdicionalConsejos(Haber haber, Tripulante tripulante, YearMonth periodo) {
        super(haber);
        if (tripulante == null || !(tripulante.getCargo() instanceof Consejero)) {
            throw new IllegalArgumentException("El adicional de consejos requiere un tripulante consejero");
        }
        if (periodo == null) {
            throw new IllegalArgumentException("El período de liquidación no puede ser nulo");
        }

        this.periodo = periodo;
        this.cantidadConsejos = tripulante.cantidadConsejos(periodo);
    }

    @Override
    protected String nombreConcepto() {
        return "Consejos de " + periodo + " (" + cantidadConsejos + ")";
    }

    @Override
    protected double importeConcepto() {
        return cantidadConsejos * 2.0;
    }
}
