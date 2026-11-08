package ar.edu.unmdp.tpg.elementos;
import ar.edu.unmdp.tpg.naves.Nave;

public class Asteroide implements ElementoDelUniverso  {
    private final int danioEnergia;

    public Asteroide(int danioEnergia) {
        assert danioEnergia > 0 : "El daño del asteroide debe ser positivo";
        this.danioEnergia = danioEnergia;
    }

    @Override
    public String interactuarCon(Nave nave) {
        assert nave != null : "La nave no puede ser nula";
        nave.recibirImpacto(danioEnergia);   // ← lo del paso 1
        String resultado = "Impacto de asteroide: -" + danioEnergia + " de energía";
        if (nave.estaDestruida()) {
            resultado += ". La nave quedó sin energía y fue destruida";
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Asteroide";
    }
    
}
