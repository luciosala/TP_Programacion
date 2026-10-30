package ar.edu.unmdp.tpg.elementos;

public class EstacionDeCargaPorcentual extends EstacionDeCarga {

    private final int porcentaje;

    public EstacionDeCargaPorcentual(int porcentaje) {
        if (porcentaje < 1 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 1 y 100");
        }
        this.porcentaje = porcentaje;
    }

    @Override
    protected int calcularRecarga(int valorActual) {
        return valorActual * porcentaje / 100;   // ej: 50% de 40 = 20
    }
}