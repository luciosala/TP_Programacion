package ar.edu.unmdp.tpg.test;

import ar.edu.unmdp.tpg.elementos.*;
import ar.edu.unmdp.tpg.naves.*;

/**
 * Main de prueba de los elementos del universo (Parte 2): destruccion de la
 * nave, asteroide, estaciones de carga y tabla de batallas con double dispatch.
 * Compara cada resultado con el esperado usando el Verificador.
 */
public class PruebaElementos {

    private static final Verificador verificador = new Verificador();

    public static void main(String[] args) {

        System.out.println("=== Paso 1: destrucción");
        Nave n = NaveFactory.createNave("T1", "exploradora");   // energía 80
        n.recibirImpacto(50);
        verificador.verificar("80 - 50 = 30 y sigue viva", n.getEnergia() == 30 && !n.estaDestruida());
        n.recibirImpacto(50);
        verificador.verificar("Energía topeada en 0 y destruida", n.getEnergia() == 0 && n.estaDestruida());
        n.recargar(0, 40);
        verificador.verificar("Recarga pero sigue destruida", n.getEnergia() == 40 && n.estaDestruida());

        System.out.println("\n=== Paso 2: asteroide y estaciones");
        Nave m = NaveFactory.createNave("T2", "exploradora");   // combustible 60, energía 80
        ElementoDelUniverso asteroide = new Asteroide(30);
        System.out.println(asteroide.interactuarCon(m));
        verificador.verificar("Asteroide: energía 50", m.getEnergia() == 50);

        ElementoDelUniverso fija = new EstacionDeCargaFija(30);
        System.out.println(fija.interactuarCon(m));
        verificador.verificar("Fija: combustible 90, energía 80", m.getCombustible() == 90 && m.getEnergia() == 80);

        ElementoDelUniverso porcentual = new EstacionDeCargaPorcentual(50);
        System.out.println(porcentual.interactuarCon(m));
        verificador.verificar("Porcentual: ambos topeados en 100", m.getCombustible() == 100 && m.getEnergia() == 100);

        System.out.println("\n=== Paso 3: tabla de batallas");
        String[] tipos = {"exploradora", "carguero", "combate"};
        ResultadoBatalla[][] esperado = {
                {ResultadoBatalla.PACTO_DE_PAZ, ResultadoBatalla.GANA,         ResultadoBatalla.PIERDE},
                {ResultadoBatalla.PIERDE,       ResultadoBatalla.PACTO_DE_PAZ, ResultadoBatalla.GANA},
                {ResultadoBatalla.GANA,         ResultadoBatalla.PIERDE,       ResultadoBatalla.PACTO_DE_PAZ},
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ResultadoBatalla r = NaveFactory.createNave("P", tipos[i])
                        .enfrentar(NaveFactory.createNave("E", tipos[j]));
                verificador.verificar(tipos[i] + " vs " + tipos[j] + " -> " + r, r == esperado[i][j]);
            }
        }

        System.out.println("\n=== Paso 3: encuentros en el tablero");
        Nave yo = NaveFactory.createNave("YO", "exploradora");
        ElementoDelUniverso carguera = NaveFactory.createNave("ENE1", "carguero");
        System.out.println(carguera.interactuarCon(yo));
        verificador.verificar("Gano: yo sigo viva", !yo.estaDestruida());

        ElementoDelUniverso combate = NaveFactory.createNave("ENE2", "combate");
        System.out.println(combate.interactuarCon(yo));
        verificador.verificar("Pierdo: yo quedo destruida", yo.estaDestruida());

        verificador.imprimirResumen();
        System.exit(verificador.fallos() == 0 ? 0 : 1);
    }

}