package ar.edu.unmdp.tpg.haberes;

/**
 * Base de los conceptos que se agregan a un haber.
 *
 * PATRON DECORATOR: cada decorador es a la vez un Haber y envuelve a otro Haber.
 * Al calcular, pide el total al haber envuelto y le suma su propio concepto, asi
 * los adicionales se encadenan en cualquier combinacion.
 *
 * Las subclases solo definen como se llama su concepto y cuanto vale.
 */
public abstract class DecoratorHaber extends Haber {

    protected final Haber haber;

    /**
     * Construye un decorador sobre un haber existente.
     *
     * Precondiciones (assert):
     * - haber no es nulo.
     *
     * Postcondiciones:
     * - El componente almacenado es el haber recibido y no es nulo.
     *
     * @param haber haber al que se agrega un concepto
     */
    protected DecoratorHaber(Haber haber) {
        assert haber != null : "El haber a decorar no puede ser nulo";

        this.haber = haber;
    }

    /**
     * Calcula el total del haber incluyendo el concepto adicional.
     *
     * Precondiciones (assert):
     * - El importe del concepto adicional es finito y no negativo.
     * - El haber envuelto cumple su contrato de cálculo de importes válidos.
     *
     * Postcondiciones:
     * - Devuelve el total del haber envuelto más el importe del concepto adicional.
     * - El total devuelto es finito y no negativo.
     *
     * @return total del haber en PG
     */
    @Override
    public double calcularTotal() {
        double importe = importeConcepto();
        assert Double.isFinite(importe) && importe >= 0 : "El concepto debe tener un importe válido no negativo";

        double total = haber.calcularTotal() + importe;
        assert Double.isFinite(total) && total >= 0 : "El total del haber debe ser un importe válido no negativo";
        return total;
    }

    @Override
    public int cantidadConceptos() {
        return haber.cantidadConceptos() + 1;
    }

    @Override
    public String consultarNombreConcepto(int indice) {
        validarIndice(indice);
        if (indice == haber.cantidadConceptos()) {
            return nombreConcepto();
        }
        return haber.consultarNombreConcepto(indice);
    }

    @Override
    public double consultarImporteConcepto(int indice) {
        validarIndice(indice);
        if (indice == haber.cantidadConceptos()) {
            return importeConcepto();
        }
        return haber.consultarImporteConcepto(indice);
    }

    protected abstract String nombreConcepto();

    protected abstract double importeConcepto();

    // Precondicion de las consultas por indice (assert).
    private void validarIndice(int indice) {
        assert indice >= 0 && indice < cantidadConceptos() : "El índice no corresponde a un concepto del haber: " + indice;
    }
}
