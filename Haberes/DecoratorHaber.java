public abstract class DecoratorHaber extends Haber {

    protected final Haber haber;

    /**
     * Construye un decorador sobre un haber existente.
     *
     * Precondiciones:
     * - haber no es nulo.
     *
     * Postcondiciones:
     * - El componente almacenado es el haber recibido y no es nulo.
     *
     * @param haber haber al que se agrega un concepto
     * @throws IllegalArgumentException si el haber es nulo
     */
    protected DecoratorHaber(Haber haber) {
        if (haber == null) {
            throw new IllegalArgumentException("El haber a decorar no puede ser nulo");
        }

        this.haber = haber;
    }

    /**
     * Calcula el total del haber incluyendo el concepto adicional.
     *
     * Precondiciones:
     * - El importe del concepto adicional es finito y no negativo.
     * - El haber envuelto cumple su contrato de cálculo de importes válidos.
     *
     * Postcondiciones:
     * - Devuelve el total del haber envuelto más el importe del concepto adicional.
     * - El total devuelto es finito y no negativo.
     *
     * @return total del haber en PG
     * @throws IllegalStateException si el importe adicional o el total
     *         no es finito o es negativo
     */
    @Override
    public double calcularTotal() {
        double importe = importeConcepto();
        if (!Double.isFinite(importe) || importe < 0) {
            throw new IllegalStateException("El concepto debe tener un importe válido no negativo");
        }

        double total = haber.calcularTotal() + importe;
        if (!Double.isFinite(total) || total < 0) {
            throw new IllegalStateException("El total del haber debe ser un importe válido no negativo");
        }
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

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= cantidadConceptos()) {
            throw new IndexOutOfBoundsException("El índice no corresponde a un concepto del haber");
        }
    }
}
