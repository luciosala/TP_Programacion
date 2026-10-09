package ar.edu.unmdp.tpg.haberes;

import ar.edu.unmdp.tpg.tripulacion.Origen;

/**
 * Concepto adicional fijo segun el planeta de origen del tripulante.
 */
public class SubsidioPorOrigen extends DecoratorHaber {

    private final Origen origen;

    /**
     * Construye el concepto de subsidio por origen.
     *
     * Precondiciones:
     * - haber no es nulo.
     * - origen no es nulo.
     *
     * Postcondiciones:
     * - El adicional queda fijado en el subsidio mensual que corresponde a ese origen.
     * - El importe del adicional es finito y no negativo, y no cambia despues.
     *
     * @param haber haber al que se agrega el subsidio
     * @param origen origen del tripulante
     * @throws IllegalArgumentException si el haber o el origen son nulos
     */
    public SubsidioPorOrigen(Haber haber, Origen origen) {
        super(haber);
        if (origen == null) {
            throw new IllegalArgumentException("El origen no puede ser nulo");
        }
        this.origen = origen;
    }

    @Override
    protected String nombreConcepto() {
        return "Subsidio por origen " + origen;
    }

    @Override
    protected double importeConcepto() {
        return origen.getSubsidioMensual();
    }
}
