package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.naves.Recursos;

/**
 * Motor Warp de una nave
 *
 * Hacia afuera solo expone dos operaciones, una por turno de navegacion:
 * - saltar(): salto normal, avanza 1 casilla y consume 2 de combustible.
 * - saltarWarp(): avanza la maniobra de salto warp un turno. La maniobra completa
 *   lleva 2 turnos de preparacion, el turno del salto (5 de energia, avanza
 *   2 casillas) y 1 turno de enfriamiento (3 de desgaste).
 *
 * PATRON STATE: el motor no decide con ifs; le pasa cada orden a su estado actual
 * (Disponible, PreparandoSalto, EnWarp o Enfriando), y el estado hace lo que
 * corresponde y, si hace falta, cambia el estado del motor.
 *
 * El motor trabaja sobre los recursos de la nave que lo tiene, que recibe al crearse.
 *
 * Invariante: estadoActual y recursos nunca son nulos.
 */
public class MotorWarp {

    public static final int COMBUSTIBLE_SALTO = 2;
    public static final int ENERGIA_WARP = 5;
    public static final int DESGASTE_ENFRIAMIENTO = 3;
    public static final int TURNOS_PREPARACION = 2;
    public static final int CASILLAS_WARP = 2;

    private final Recursos recursos;
    private Estado estadoActual;

    /**
     * Precondiciones (assert):
     * - recursos no es nulo.
     *
     * Postcondiciones:
     * - El motor comienza Disponible.
     *
     * @param recursos recursos de la nave que tiene este motor
     */
    public MotorWarp(Recursos recursos) {
        assert recursos != null : "El motor necesita los recursos de la nave";
        this.recursos = recursos;
        this.estadoActual = new Disponible();
        assert estadoActual.estaDisponible() : "El motor debe comenzar Disponible";
        assert invariante() : "Fallo invariante de MotorWarp";
    }

    private boolean invariante() {
        return estadoActual != null && recursos != null;
    }

    /** Lo usan los estados para pasar al siguiente. */
    void cambiarEstado(Estado nuevo) {
        assert nuevo != null : "El nuevo estado no puede ser nulo";
        estadoActual = nuevo;
    }

    /**
     * Turno de salto normal.
     *
     * Postcondiciones:
     * - Disponible: avanza 1 casilla y consume 2 de combustible.
     * - Enfriamiento: turno de enfriamiento (0 casillas, +3 de desgaste, vuelve a Disponible).
     *
     * @return casillas que avanza la nave en este turno
     * @throws TransicionInvalidaException si hay un salto warp en preparacion o listo
     * @throws RecursoInsuficienteException si no hay combustible para el salto
     */
    public int saltar() throws TransicionInvalidaException, RecursoInsuficienteException {
        int casillas = estadoActual.saltar(this, recursos);
        assert casillas >= 0 && casillas <= 1 : "Un salto normal avanza a lo sumo 1 casilla";
        assert invariante() : "Fallo invariante de MotorWarp";
        return casillas;
    }

    /**
     * Turno de salto warp: avanza un paso la maniobra.
     *
     * Postcondiciones:
     * - Disponible o Preparando salto: turno de preparacion (0 casillas).
     * - En warp: consume 5 de energia y avanza 2 casillas; pasa a Enfriamiento.
     * - Enfriamiento: turno de enfriamiento (0 casillas, +3 de desgaste, vuelve a Disponible).
     *
     * @return casillas que avanza la nave en este turno
     * @throws RecursoInsuficienteException si falta energia para preparar o para saltar
     */
    public int saltarWarp() throws TransicionInvalidaException, RecursoInsuficienteException {
        int casillas = estadoActual.saltarWarp(this, recursos);
        assert casillas == 0 || casillas == CASILLAS_WARP : "Un turno de warp avanza 0 o 2 casillas";
        assert invariante() : "Fallo invariante de MotorWarp";
        return casillas;
    }

    /** @return nombre del estado actual */
    public String getEstadoActual() {
        return estadoActual.nombre();
    }

    /** @return true si el motor esta Disponible (sin ninguna maniobra en curso) */
    public boolean estaDisponible() {
        return estadoActual.estaDisponible();
    }
}
