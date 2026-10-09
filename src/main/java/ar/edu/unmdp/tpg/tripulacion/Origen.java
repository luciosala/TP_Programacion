package ar.edu.unmdp.tpg.tripulacion;

/**
 * Planeta de origen de un tripulante. Cada origen tiene asociado su subsidio
 * mensual fijo, que se suma al haber como un concepto mas.
 */
public enum Origen {
    TERRICOLA(20),
    VULCANO(30),
    MARCIANO(18);

    private final int subsidioMensual;

    Origen(int subsidioMensual) {
        this.subsidioMensual = subsidioMensual;
    }

    /**
     * Informa el subsidio mensual que corresponde a este origen.
     *
     * Postcondiciones:
     * - Devuelve un importe mayor o igual a 0, en PG, siempre el mismo para un origen dado.
     *
     * @return subsidio mensual en PG
     */
    public int getSubsidioMensual() {
        return subsidioMensual;
    }
}
