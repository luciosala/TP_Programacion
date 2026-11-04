package ar.edu.unmdp.tpg.haberes;

import ar.edu.unmdp.tpg.tripulacion.Tripulante;

/**
 * Componente concreto del Decorator: el sueldo base que corresponde al cargo del
 * tripulante. Es el unico concepto que todo haber tiene, y sobre el se encadenan
 * los adicionales.
 */
public class HaberBase extends Haber {

    private final String nombreCargo;
    private final double remuneracionBase;

    /**
     * Construye el concepto de sueldo base del tripulante.
     *
     * Precondiciones (assert):
     * - tripulante no es nulo.
     * - La remuneración base de su cargo es finita y no negativa.
     *
     * Postcondiciones:
     * - El haber contiene un concepto de sueldo base con el importe del cargo.
     * - El importe almacenado es finito y no negativo.
     *
     * @param tripulante tripulante cuyo sueldo base se calcula
     */
    public HaberBase(Tripulante tripulante) {
        assert tripulante != null : "El tripulante no puede ser nulo";

        double importe = tripulante.getCargo().getRemuneracionBase();
        assert Double.isFinite(importe) && importe >= 0 : "La remuneración base debe ser un importe válido no negativo";

        this.nombreCargo = tripulante.getCargo().getNombre();
        this.remuneracionBase = importe;
    }

    @Override
    public double calcularTotal() {
        return remuneracionBase;
    }

    @Override
    public int cantidadConceptos() {
        return 1;
    }

    @Override
    public String consultarNombreConcepto(int indice) {
        validarIndice(indice);
        return "Sueldo base de " + nombreCargo;
    }

    @Override
    public double consultarImporteConcepto(int indice) {
        validarIndice(indice);
        return remuneracionBase;
    }

    // Precondicion de las consultas por indice (assert).
    private void validarIndice(int indice) {
        assert indice == 0 : "El haber base tiene un único concepto, con índice 0";
    }
}
