package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;

/**
 * Estado En warp: la preparación terminó y el motor está en condiciones de
 * materializar el salto warp.
 */
class EnWarp implements Estado {

    private final MotorWarp motor;

    EnWarp(MotorWarp motor) {
        this.motor = motor;
    }

    /**
     * Se cancela el salto warp y se hace un salto simple. Si no alcanza el
     * combustible, el salto se rechaza, pero la maniobra warp ya quedó cancelada
     * (el motor queda Disponible).
     */
    @Override
    public int saltar() throws RecursoInsuficienteException {
        motor.setEstado(new Disponible(motor));
        motor.getRecursos().consumirCombustible(MotorWarp.COMBUSTIBLE_SALTO);
        return 1;
    }

    /**
     * Ejecuta el salto: consume 5 de energía, avanza 2 casilleros y pasa a
     * Enfriamiento. Si la energía ya no está, la maniobra se aborta y el motor
     * vuelve a Preparando salto (Anexo).
     */
    @Override
    public int saltarWarp() {
        try {
            motor.getRecursos().consumirEnergia(MotorWarp.ENERGIA_SALTO_WARP);
            motor.setEstado(new Enfriando(motor));
            return 2;
        } catch (RecursoInsuficienteException sinEnergia) {
            motor.setEstado(new PrepararSalto(motor));
            return 0;
        }
    }

    @Override
    public String nombre() {
        return "En warp";
    }

    @Override
    public boolean estaDisponible() {
        return false;
    }
}
