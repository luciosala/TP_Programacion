package ar.edu.unmdp.tpg.elementos;
import ar.edu.unmdp.tpg.naves.Nave;

public class Asteroide implements ElementoDelUniverso  {
    private final int danioEnergia;

    public Asteroide(int danioEnergia) {
        if (danioEnergia <= 0) {
            throw new IllegalArgumentException("El daño del asteroide debe ser positivo");
        }
        this.danioEnergia = danioEnergia;
    }

    @Override
    public String interactuarCon(Nave nave) {
        if (nave == null) {
            throw new IllegalArgumentException("La nave no puede ser nula");
        }
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
