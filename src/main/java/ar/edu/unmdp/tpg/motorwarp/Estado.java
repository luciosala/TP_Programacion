package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.naves.Recursos;

/**
 * Estado del Motor Warp (patron State).
 *
 * El motor solo expone saltar() y saltarWarp() (Anexo de la catedra). Lo que pasa
 * en cada turno depende del estado en que este el motor, y eso lo decide cada
 * subclase de Estado, no el motor con ifs:
 *
 * - Disponible: saltar() avanza 1 casilla (2 de combustible); saltarWarp() empieza
 *   la preparacion.
 * - Preparando salto: cada saltarWarp() es un turno de preparacion (hacen falta 2),
 *   y en cada uno se controla que haya 5 de energia. saltar() no esta permitido.
 * - En warp: el motor esta listo; saltarWarp() consume 5 de energia y avanza
 *   2 casillas. Si ya no hay energia, se aborta y se vuelve a preparar.
 * - Enfriamiento: el siguiente turno (saltar() o saltarWarp()) es de enfriamiento,
 *   suma 3 de desgaste y el motor vuelve a Disponible. La nave no se mueve.
 *
 * Contrato comun de saltar() y saltarWarp():
 * - Devuelven cuantas casillas avanza la nave en este turno (0, 1 o 2).
 * - Si cambian de estado, lo hacen con motor.cambiarEstado(...).
 * - Si rechazan la orden, lanzan una excepcion y el motor no cambia de estado
 *   (salvo el aborto del salto, que segun el Anexo vuelve a preparacion).
 */
public abstract class Estado {

    abstract int saltar(MotorWarp motor, Recursos recursos)
            throws TransicionInvalidaException, RecursoInsuficienteException;

    abstract int saltarWarp(MotorWarp motor, Recursos recursos)
            throws TransicionInvalidaException, RecursoInsuficienteException;

    abstract String nombre();

    boolean estaDisponible() {
        return false;
    }
}
