package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.naves.Recursos;

/**
 * Despues del salto warp el motor necesita un turno de enfriamiento. Ese turno
 * suma 3 de desgaste a la nave, que no se mueve, y el motor vuelve a Disponible.
 * Da lo mismo si la orden del turno es saltar() o saltarWarp().
 */
public class Enfriando extends Estado {

    @Override
    String nombre() {
        return "Enfriamiento";
    }

    @Override
    int saltar(MotorWarp motor, Recursos recursos) {
        return enfriar(motor, recursos);
    }

    @Override
    int saltarWarp(MotorWarp motor, Recursos recursos) {
        return enfriar(motor, recursos);
    }

    private int enfriar(MotorWarp motor, Recursos recursos) {
        recursos.sufrirDesgaste(MotorWarp.DESGASTE_ENFRIAMIENTO);
        motor.cambiarEstado(new Disponible());
        return 0;
    }
}
