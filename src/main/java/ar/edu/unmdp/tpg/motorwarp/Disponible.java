package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;

/**
 * Estado Disponible: el motor puede hacer un salto simple o empezar a preparar
 * un salto warp.
 */
class Disponible implements Estado {

    private final MotorWarp motor;

    Disponible(MotorWarp motor) {
        this.motor = motor;
    }

    /** Salto simple: consume 2 de combustible y avanza 1 casillero. Sigue Disponible. */
    @Override
    public int saltar() throws RecursoInsuficienteException {
        motor.getRecursos().consumirCombustible(MotorWarp.COMBUSTIBLE_SALTO);
        return 1;
    }

    /**
     * Empieza la maniobra warp: pasa a Preparando salto y este llamado ya cuenta
     * como el primer turno de preparación.
     */
    @Override
    public int saltarWarp() {
        PrepararSalto preparando = new PrepararSalto(motor);
        motor.setEstado(preparando);
        return preparando.saltarWarp();
    }

    @Override
    public String nombre() {
        return "Disponible";
    }

    @Override
    public boolean estaDisponible() {
        return true;
    }
}
