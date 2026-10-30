package ar.edu.unmdp.tpg.elementos;

import ar.edu.unmdp.tpg.naves.Nave;

public abstract class EstacionDeCarga implements ElementoDelUniverso {

    @Override
    public final String interactuarCon(Nave nave) {
        if (nave == null) {
            throw new IllegalArgumentException("La nave no puede ser nula");
        }
        int combustibleAntes = nave.getCombustible();
        int energiaAntes = nave.getEnergia();

        nave.recargar(calcularRecarga(combustibleAntes), calcularRecarga(energiaAntes));

        // Informo lo que REALMENTE subió (puede ser menos si se topeó en 100)
        return "Estación de carga: +" + (nave.getCombustible() - combustibleAntes)
            + " de combustible, +" + (nave.getEnergia() - energiaAntes) + " de energía";
    }

    // Lo único que cambia entre estaciones: cuánto recargar
    protected abstract int calcularRecarga(int valorActual);

    @Override
    public String toString() {
        return "Estación de carga";
    }
}