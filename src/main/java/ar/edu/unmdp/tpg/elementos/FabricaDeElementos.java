package ar.edu.unmdp.tpg.elementos;

import ar.edu.unmdp.tpg.naves.NaveFactory;

import java.util.ArrayList;
import java.util.Random;

/**
 * Fábrica de los elementos que pueblan el tablero (patrón Factory, igual que
 * NaveFactory): el cliente pide cuántos elementos de cada clase quiere y recibe
 * la lista armada, sin instanciar las clases concretas.
 *
 * Las naves enemigas se crean con NaveFactory, con un tipo al azar.
 */
public class FabricaDeElementos {

    /** Energía que descuenta el impacto de un asteroide (monto fijo, Enunciado). */
    public static final int DANIO_ASTEROIDE = 10;
    /** Recarga de una estación de monto fijo. */
    public static final int RECARGA_FIJA = 20;
    /** Recarga de una estación porcentual, sobre lo que tiene la nave. */
    public static final int RECARGA_PORCENTUAL = 25;

    private static final String[] TIPOS_DE_NAVE = { "exploradora", "carguero", "combate" };
    private static final Random AZAR = new Random();

    /**
     * Crea los elementos de un universo.
     *
     * Precondiciones (assert):
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Devuelve una lista nueva con exactamente asteroides + estaciones + navesEnemigas elementos.
     * - La mitad de las estaciones (redondeando hacia arriba) recarga un monto fijo y el resto un porcentaje.
     *
     * @param asteroides cantidad de asteroides
     * @param estaciones cantidad de estaciones de carga
     * @param navesEnemigas cantidad de naves enemigas, de tipo al azar
     * @return los elementos, listos para entregarle al Tablero
     */
    public static ArrayList<ElementoDelUniverso> crearElementos(int asteroides, int estaciones, int navesEnemigas) {
        assert asteroides >= 0 && estaciones >= 0 && navesEnemigas >= 0 : "Las cantidades no pueden ser negativas";
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        for (int i = 0; i < asteroides; i++) {
            elementos.add(new Asteroide(DANIO_ASTEROIDE));
        }
        for (int i = 0; i < estaciones; i++) {
            if (i % 2 == 0) {
                elementos.add(new EstacionDeCargaFija(RECARGA_FIJA));
            } else {
                elementos.add(new EstacionDeCargaPorcentual(RECARGA_PORCENTUAL));
            }
        }
        for (int i = 0; i < navesEnemigas; i++) {
            String tipo = TIPOS_DE_NAVE[AZAR.nextInt(TIPOS_DE_NAVE.length)];
            elementos.add(NaveFactory.createNave("ENE-" + (i + 1), tipo));
        }
        assert elementos.size() == asteroides + estaciones + navesEnemigas : "Fallo postcondición de crearElementos";
        return elementos;
    }
}
