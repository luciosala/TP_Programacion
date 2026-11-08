package ar.edu.unmdp.tpg.elementos;

public class EstacionDeCargaFija extends EstacionDeCarga {

    private final int monto;

    public EstacionDeCargaFija(int monto) {
        assert monto > 0 : "El monto de recarga debe ser positivo";
        this.monto = monto;
    }

    @Override
    protected int calcularRecarga(int valorActual) {
        return monto;   // siempre lo mismo, no importa cuánto tenga
    }
}