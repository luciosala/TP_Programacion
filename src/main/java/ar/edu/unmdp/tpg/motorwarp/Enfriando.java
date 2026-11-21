package ar.edu.unmdp.tpg.motorwarp;

/**
 * Estado Enfriamiento: después del salto warp el motor no puede volver a estar
 * disponible de inmediato. El turno siguiente se usa para disipar el calor, lo
 * que le genera a la nave 3 puntos de desgaste, y el motor queda Disponible.
 * Durante ese turno la nave no se mueve, se pida el salto que se pida.
 */
class Enfriando implements Estado {

    private final MotorWarp motor;

    Enfriando(MotorWarp motor) {
        this.motor = motor;
    }

    @Override
    public int saltar() {
        return enfriar();
    }

    @Override
    public int saltarWarp() {
        return enfriar();
    }

    private int enfriar() {
        motor.getRecursos().sufrirDesgaste(MotorWarp.DESGASTE_ENFRIAMIENTO);
        motor.setEstado(new Disponible(motor));
        return 0;
    }

    @Override
    public String nombre() {
        return "Enfriamiento";
    }

    @Override
    public boolean estaDisponible() {
        return false;
    }
}
