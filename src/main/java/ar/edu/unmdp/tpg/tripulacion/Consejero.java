package ar.edu.unmdp.tpg.tripulacion;

/**
 * Consejero. Sueldo base 600 PG y 5 por ciento de antiguedad. Es el unico cargo
 * que registra consejos y cobra el adicional por ellos.
 */
public class Consejero extends Cargo {

    @Override
    public String getNombre() {
        return "Consejero";
    }

    @Override
    public double getRemuneracionBase() {
        return 600;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.05;
    }
}
