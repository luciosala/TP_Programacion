package ar.edu.unmdp.tpg.naves;

import ar.edu.unmdp.tpg.motorwarp.MotorWarp;

/**
 * Nave carguera. Su configuracion inicial es 100 de combustible y 60 de energia.
 *
 * Se crea a traves de NaveFactory: su constructor es package private para que el
 * cliente no arme una nave con recursos que no correspondan a su tipo.
 */
public class Carguera extends Nave {

    Carguera(String id, String tipo, Recursos recursos, MotorWarp motorWarp) {
        super(id, tipo, recursos, motorWarp);
    }
}
