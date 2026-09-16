public abstract class DecoratorHaber extends Haber {

    protected final Haber haber;

    protected DecoratorHaber(Haber haber) {
        if (haber == null) {
            throw new IllegalArgumentException("El haber a decorar no puede ser nulo");
        }

        this.haber = haber;
    }

    @Override
    public double calcularTotal() {
        double total = haber.calcularTotal() + importeConcepto();
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
