package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.naves.Recursos;

/**
 * El motor prepara el salto warp. Hacen falta 2 turnos de preparacion y en cada
 * uno se controla que la nave tenga las 5 de energia que va a necesitar el salto.
 * Si no las tiene, la preparacion no avanza y el motor se queda en este estado.
 *
 * Es inmutable: cada turno completado pasa a un PreparandoSalto nuevo con un
 * turno mas, o a En warp cuando se completan los 2.
 */
public class PreparandoSalto extends Estado {

    private final int turnosCompletados;

    PreparandoSalto(int turnosCompletados) {
        assert turnosCompletados >= 0 && turnosCompletados < MotorWarp.TURNOS_PREPARACION
            : "Turnos de preparacion fuera de rango";
        this.turnosCompletados = turnosCompletados;
    }

    @Override
    String nombre() {
        return "Preparando salto";
    }

    /** Durante la preparacion no se puede hacer un salto normal. */
    @Override
    int saltar(MotorWarp motor, Recursos recursos) throws TransicionInvalidaException {
        throw new TransicionInvalidaException(
            "No se puede hacer un salto normal mientras se prepara el salto warp");
    }

    /**
     * Un turno de preparacion. La nave no se mueve.
     *
     * @throws RecursoInsuficienteException si no hay 5 de energia: el motor se queda
     *         en este mismo estado, sin sumar el turno
     */
    @Override
    int saltarWarp(MotorWarp motor, Recursos recursos) throws RecursoInsuficienteException {
        if (recursos.getEnergia() < MotorWarp.ENERGIA_WARP) {
            throw new RecursoInsuficienteException(
                "Energía insuficiente para preparar el salto warp (hacen falta "
                + MotorWarp.ENERGIA_WARP + "): el motor queda en preparación");
        }
        int turnos = turnosCompletados + 1;
        if (turnos == MotorWarp.TURNOS_PREPARACION) {
            motor.cambiarEstado(new EnWarp());
        } else {
            motor.cambiarEstado(new PreparandoSalto(turnos));
        }
        return 0;
    }

    /** @return cuantos turnos de preparacion se completaron */
    int getTurnosCompletados() {
        return turnosCompletados;
    }
}
