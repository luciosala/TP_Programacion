package ar.edu.unmdp.tpg.mision;

import ar.edu.unmdp.tpg.destino.Posicion;

import java.util.ArrayList;
import java.util.List;

public class RadarLargoAlcance {

    // Ruta desde origen hasta destino, de a una casilla por salto.
    // No incluye el origen; la última posición es el destino.
    public List<Posicion> calcularRuta(Posicion origen, Posicion destino) {
        List<Posicion> ruta = new ArrayList<>();
        int x = origen.getX();
        int y = origen.getY();
        while (x != destino.getX() || y != destino.getY()) {
            x += Integer.signum(destino.getX() - x);   // da -1, 0 o +1
            y += Integer.signum(destino.getY() - y);
            ruta.add(new Posicion(x, y));
        }
        return ruta;
    }
}
