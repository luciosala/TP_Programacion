package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;

/**
 * Interfaz común de los estados del Motor Warp (patrón State, Clase 11).
 *
 * Cada estado implementa las dos operaciones del motor a su manera. Igual que en
 * el ejemplo del vehículo de la teoría, cada estado recibe en su constructor la
 * referencia al motor (el contexto) y, cuando corresponde, le setea el estado
 * siguiente.
 *
 * Contrato de las operaciones: devuelven cuántos casilleros avanza la nave en
 * ese turno. La nave (y no el motor) es la que se mueve en el tablero.
 */
interface Estado {

    /**
     * Turno de salto simple.
     *
     * @return casilleros que avanza la nave (0 o 1)
     * @throws RecursoInsuficienteException si no hay combustible para el salto;
     *         en ese caso no cambian los recursos
     */
    int saltar() throws RecursoInsuficienteException;

    /**
     * Turno de la maniobra warp.
     *
     * @return casilleros que avanza la nave (0 o 2)
     */
    int saltarWarp();

    /** @return nombre del estado, para la bitácora y la vista */
    String nombre();

    /** @return true solo en el estado Disponible */
    boolean estaDisponible();
}
