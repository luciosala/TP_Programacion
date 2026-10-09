package ar.edu.unmdp.tpg.tripulacion;

/**
 * Alferez. Sueldo base 200 PG y 0,5 por ciento de antiguedad.
 */
public class Alferez extends Cargo {

    @Override
    public String getNombre() {
        return "Alférez";
    }

    @Override
    public double getRemuneracionBase() {
        return 200;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.005;
    }
}
