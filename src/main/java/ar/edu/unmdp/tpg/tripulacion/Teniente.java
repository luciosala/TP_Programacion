package ar.edu.unmdp.tpg.tripulacion;

/**
 * Teniente. Sueldo base 400 PG y 3 por ciento de antiguedad.
 */
public class Teniente extends Cargo {

    @Override
    public String getNombre() {
        return "Teniente";
    }

    @Override
    public double getRemuneracionBase() {
        return 400;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.03;
    }
}
