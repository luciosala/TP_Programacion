package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;

/**
 * Estado Preparando salto: el motor dedica 2 turnos a preparar el salto warp.
 * En cada turno evalúa si la nave tiene las 5 unidades de energía; si no las
 * tiene, la maniobra no avanza y el motor se queda en preparación (Anexo).
 */
class PrepararSalto implements Estado {

    private final MotorWarp motor;
    private int turnosPreparados = 0;

    PrepararSalto(MotorWarp motor) {
        this.motor = motor;
    }

    /**
     * Se cancela la preparación y se hace un salto simple. Si no alcanza el
     * combustible, el salto se rechaza, pero la maniobra warp ya quedó cancelada
     * (el motor queda Disponible y no queda trabado en preparación).
     */
    @Override
    public int saltar() throws RecursoInsuficienteException {
        motor.setEstado(new Disponible(motor));
        motor.getRecursos().consumirCombustible(MotorWarp.COMBUSTIBLE_SALTO);
        return 1;
    }

    /** Un turno de preparación. Al completar los 2 turnos el motor queda En warp. */
    @Override
    public int saltarWarp() {
        if (motor.getRecursos().getEnergia() >= MotorWarp.ENERGIA_SALTO_WARP) {
            turnosPreparados++;
            if (turnosPreparados == MotorWarp.TURNOS_PREPARACION) {
                motor.setEstado(new EnWarp(motor));
            }
        }
        return 0;
    }

    @Override
    public String nombre() {
        return "Preparando salto";
    }

    @Override
    public boolean estaDisponible() {
        return false;
    }
}
