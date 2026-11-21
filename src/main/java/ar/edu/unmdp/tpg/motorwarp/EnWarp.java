package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.naves.Recursos;

/**
 * La preparacion termino y el motor esta en condiciones de materializar el salto.
 */
public class EnWarp extends Estado {

    @Override
    String nombre() {
        return "En warp";
    }

    @Override
    int saltar(MotorWarp motor, Recursos recursos) throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede hacer un salto normal con el salto warp listo para ejecutarse");
    }

    /**
     * Ejecuta el salto: consume 5 de energia, la nave avanza 2 casillas y el motor
     * pasa a Enfriamiento.
     *
     * @throws RecursoInsuficienteException si ya no hay 5 de energia: el salto se
     *         aborta y el motor vuelve a Preparando salto (sin consumir nada)
     */
    @Override
    int saltarWarp(MotorWarp motor, Recursos recursos) throws RecursoInsuficienteException {
        if (recursos.getEnergia() < MotorWarp.ENERGIA_WARP) {
            motor.cambiarEstado(new PreparandoSalto(0));
            throw new RecursoInsuficienteException(
                "Energía insuficiente para el salto warp: se aborta y el motor vuelve a preparación");
        }
        recursos.consumirEnergia(MotorWarp.ENERGIA_WARP);
        motor.cambiarEstado(new Enfriando());
        return MotorWarp.CASILLAS_WARP;
    }
}
