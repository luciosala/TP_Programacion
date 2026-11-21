package ar.edu.unmdp.tpg.universo;

import ar.edu.unmdp.tpg.destino.CantidadDeElementosSuperaCasillaException;
import ar.edu.unmdp.tpg.destino.DimensionesDeTableroInconsistentesException;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.elementos.Asteroide;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;
import ar.edu.unmdp.tpg.elementos.EstacionDeCargaFija;
import ar.edu.unmdp.tpg.elementos.EstacionDeCargaPorcentual;
import ar.edu.unmdp.tpg.naves.NaveFactory;

import java.util.ArrayList;

/**
 * Arma el tablero con el que arranca el sistema: asteroides, estaciones de carga y
 * naves desconocidas repartidos al azar por el Tablero de la catedra.
 *
 * Los valores (tamanio, cantidades, danio de los asteroides, recarga de las
 * estaciones) son decision del grupo: el enunciado no los fija.
 */
public final class TableroInicial {

    public static final int FILAS = 10;
    public static final int COLUMNAS = 10;
    public static final int ASTEROIDES = 8;
    public static final int ESTACIONES = 3;
    public static final int NAVES_DESCONOCIDAS = 3;
    public static final int DANIO_ASTEROIDE = 15;

    private TableroInicial() {
    }

    public static Tablero crear() {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        for (int i = 0; i < ASTEROIDES; i++) {
            elementos.add(new Asteroide(DANIO_ASTEROIDE));
        }
        elementos.add(new EstacionDeCargaFija(20));
        elementos.add(new EstacionDeCargaPorcentual(50));
        elementos.add(new EstacionDeCargaFija(10));
        String[] tipos = {"exploradora", "carguero", "combate"};
        for (int i = 0; i < NAVES_DESCONOCIDAS; i++) {
            elementos.add(NaveFactory.createNave("DESCONOCIDA-" + (i + 1), tipos[i % tipos.length]));
        }
        try {
            return new Tablero(FILAS, COLUMNAS, elementos);
        } catch (DimensionesDeTableroInconsistentesException | CantidadDeElementosSuperaCasillaException e) {
            // Con las constantes de arriba no puede pasar: 10x10 admite hasta 75 elementos
            throw new IllegalStateException("No se pudo armar el tablero inicial: " + e.getMessage(), e);
        }
    }
}
