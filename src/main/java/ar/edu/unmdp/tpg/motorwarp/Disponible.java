package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.naves.Recursos;

/**
 * Motor libre: puede hacer un salto normal o empezar a preparar un salto warp.
 */
public class Disponible extends Estado {

    @Override
    String nombre() {
        return "Disponible";
    }

    /**
     * Salto normal: avanza 1 casilla y consume 2 de combustible. El motor sigue Disponible.
     *
     * @throws RecursoInsuficienteException si no hay 2 de combustible (no se consume nada)
     */
    @Override
    int saltar(MotorWarp motor, Recursos recursos) throws RecursoInsuficienteException {
        recursos.consumirCombustible(MotorWarp.COMBUSTIBLE_SALTO);
        return 1;
    }

    /**
     * Empieza el salto warp: pasa a Preparando salto y ese mismo turno cuenta como
     * el primero de preparacion.
     */
    @Override
    int saltarWarp(MotorWarp motor, Recursos recursos)
            throws TransicionInvalidaException, RecursoInsuficienteException {
        PreparandoSalto preparando = new PreparandoSalto(0);
        motor.cambiarEstado(preparando);
        return preparando.saltarWarp(motor, recursos);
    }

    @Override
    boolean estaDisponible() {
        return true;
    }
}
