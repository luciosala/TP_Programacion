package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.naves.Recursos;

/**
 * Motor Warp de una nave (patrón State, Clase 11).
 *
 * El motor es la clase de contexto: guarda su estado actual y le delega las dos
 * únicas operaciones que expone hacia el exterior, saltar() y saltarWarp()
 * (Anexo: "La clase del motor solo deberá exponer públicamente dos operaciones").
 * Cada estado decide qué hacer y, si corresponde, le setea al motor el estado
 * siguiente. El motor no usa ningún if sobre el estado.
 *
 * Estados: Disponible, Preparando salto, En warp y Enfriamiento.
 *
 * Costos (Anexo de Misiones y Motor Warp):
 * - Salto simple: 2 de combustible, avanza 1 casillero.
 * - Salto warp: 2 turnos de preparación (en cada uno se evalúa que haya 5 de
 *   energía), luego el salto consume 5 de energía y avanza 2 casilleros, y por
 *   último 1 turno de enfriamiento que genera 3 de desgaste.
 *
 * Además de las dos operaciones, el motor ofrece dos consultas de solo lectura
 * (getEstadoActual y estaDisponible), que no cambian nada y se usan para la
 * bitácora y la vista.
 *
 * Invariante: el estado actual y los recursos no son nulos.
 */
public class MotorWarp {

    /** Combustible que consume un salto simple. */
    public static final int COMBUSTIBLE_SALTO = 2;
    /** Energía que consume el salto warp. */
    public static final int ENERGIA_SALTO_WARP = 5;
    /** Desgaste que genera el enfriamiento posterior al salto warp. */
    public static final int DESGASTE_ENFRIAMIENTO = 3;
    /** Turnos de preparación antes del salto warp. */
    public static final int TURNOS_PREPARACION = 2;

    private final Recursos recursos;
    private Estado estado;

    /**
     * Crea el motor en estado Disponible, asociado a los recursos de su nave.
     *
     * Precondiciones (assert):
     * - recursos no es nulo.
     *
     * Postcondiciones:
     * - El motor queda Disponible.
     *
     * @param recursos recursos de la nave, que el motor consume al saltar
     */
    public MotorWarp(Recursos recursos) {
        assert recursos != null : "El motor necesita los recursos de la nave";
        this.recursos = recursos;
        this.estado = new Disponible(this);
        assert estaDisponible() : "El motor debe comenzar Disponible";
    }

    private boolean invariante() {
        return estado != null && recursos != null;
    }

    /**
     * Salto simple: el comportamiento depende del estado actual.
     * - Disponible: consume 2 de combustible y avanza 1 casillero.
     * - Preparando salto / En warp: cancela la maniobra warp y hace un salto simple.
     * - Enfriamiento: el turno se usa para enfriar (+3 de desgaste); no avanza.
     *
     * Postcondiciones:
     * - Si no hay combustible para el salto, se lanza la excepción y no cambian
     *   los recursos. Si había una maniobra warp en curso, queda cancelada.
     *
     * @return casilleros que avanza la nave en este turno (0 o 1)
     * @throws RecursoInsuficienteException si no alcanza el combustible del salto
     */
    public int saltar() throws RecursoInsuficienteException {
        int casilleros = estado.saltar();
        assert casilleros == 0 || casilleros == 1 : "Un salto simple avanza 0 o 1 casillero";
        assert invariante() : "Fallo invariante de MotorWarp";
        return casilleros;
    }

    /**
     * Salto warp: cada llamada es un turno de la maniobra.
     * - Disponible: empieza la preparación (primer turno de preparación).
     * - Preparando salto: si hay 5 de energía cuenta un turno; al completar 2
     *   queda En warp. Si no hay energía, sigue en preparación.
     * - En warp: si hay 5 de energía, la consume y avanza 2 casilleros, y pasa a
     *   Enfriamiento; si no, aborta y vuelve a Preparando salto.
     * - Enfriamiento: el turno se usa para enfriar (+3 de desgaste); no avanza.
     *
     * @return casilleros que avanza la nave en este turno (0 o 2)
     */
    public int saltarWarp() {
        int casilleros = estado.saltarWarp();
        assert casilleros == 0 || casilleros == 2 : "Un turno de warp avanza 0 o 2 casilleros";
        assert invariante() : "Fallo invariante de MotorWarp";
        return casilleros;
    }

    /**
     * Consulta de solo lectura.
     *
     * @return nombre del estado actual del motor
     */
    public String getEstadoActual() {
        return estado.nombre();
    }

    /**
     * Consulta de solo lectura.
     *
     * @return true si el motor está Disponible
     */
    public boolean estaDisponible() {
        return estado.estaDisponible();
    }

    // ---------- Uso interno de los estados (visibilidad de paquete) ----------

    /** Los estados realizan la transición seteando el estado siguiente. */
    void setEstado(Estado nuevo) {
        assert nuevo != null : "El estado del motor no puede ser nulo";
        this.estado = nuevo;
    }

    /** Los estados consumen los recursos de la nave a través del motor. */
    Recursos getRecursos() {
        return recursos;
    }
}
