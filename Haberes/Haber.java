public abstract class Haber {

    public abstract double calcularTotal();

    public abstract int cantidadConceptos();

    /**
     *Consulta el nombre de un concepto por su índice, empezando en cero.
     * Las implementaciones deben rechazar índices fuera del rango válido.
     */
    public abstract String consultarNombreConcepto(int indice);

    /**
     * Consulta el importe en PG de un concepto por su índice, empezando en cero.
     * Las implementaciones deben rechazar índices fuera del rango válido.
     */
    public abstract double consultarImporteConcepto(int indice);
}
