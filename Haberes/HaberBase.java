public class HaberBase extends Haber {

    private final String nombreCargo;
    private final double remuneracionBase;

    /**
     * Construye el concepto de sueldo base del tripulante.
     *
     * Precondiciones:
     * - tripulante no es nulo.
     * - La remuneración base de su cargo es finita y no negativa.
     *
     * Postcondiciones:
     * - El haber contiene un concepto de sueldo base con el importe del cargo.
     * - El importe almacenado es finito y no negativo.
     *
     * @param tripulante tripulante cuyo sueldo base se calcula
     * @throws IllegalArgumentException si el tripulante es nulo o la remuneración base no es finita o es negativa
     */
    public HaberBase(Tripulante tripulante) {
        if (tripulante == null) {
            throw new IllegalArgumentException("El tripulante no puede ser nulo");
        }

        double importe = tripulante.getCargo().getRemuneracionBase();
        
        if (!Double.isFinite(importe) || importe < 0) {
            throw new IllegalArgumentException("La remuneración base debe ser un importe válido no negativo");
        }

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

    private void validarIndice(int indice) {
        if (indice != 0) {
            throw new IndexOutOfBoundsException("El haber base tiene un único concepto, con índice 0");
        }
    }
}
