package ar.edu.unmdp.tpg.tripulacion;

/**
 * Capitan de la nave. Sueldo base 1000 PG y 20 por ciento de antiguedad.
 * Toda nave necesita al menos un capitan para operar.
 */
public class Capitan extends Cargo {

    @Override
    public String getNombre() {
        return "Capitán";
    }

    @Override
    public double getRemuneracionBase() {
        return 1000;
    }

    @Override
    public double getPorcentajeAntiguedad() {
        return 0.20;
    }
}
