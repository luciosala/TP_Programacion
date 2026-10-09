package ar.edu.unmdp.tpg.tripulacion;

/**
 * Cargo que ocupa un tripulante dentro de la nave. Cada cargo fija su nombre, su
 * remuneracion base y el porcentaje con que se calcula su adicional por antiguedad.
 *
 * Las implementaciones no tienen estado: dos instancias del mismo cargo responden
 * siempre lo mismo.
 */
public abstract class Cargo {

    /**
     * Informa como se llama el cargo.
     *
     * Postcondiciones:
     * - Devuelve un texto no nulo ni vacio, siempre el mismo para un cargo dado.
     *
     * @return nombre del cargo
     */
    public abstract String getNombre();

    /**
     * Informa el sueldo base del cargo, en PG.
     *
     * Postcondiciones:
     * - Devuelve un importe finito y no negativo, siempre el mismo para un cargo dado.
     *
     * @return remuneracion base del cargo en PG
     */
    public abstract double getRemuneracionBase();

    /**
     * Informa el porcentaje con que se calcula el adicional por antiguedad del cargo.
     *
     * Postcondiciones:
     * - Devuelve un valor finito y no negativo, expresado en tanto por uno
     *   (por ejemplo, 0.20 representa el 20 por ciento).
     * - Es siempre el mismo para un cargo dado.
     *
     * @return porcentaje de antiguedad del cargo, en tanto por uno
     */
    public abstract double getPorcentajeAntiguedad();
}
