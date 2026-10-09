package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;

/**
 * Estado del Motor Warp. Cada subclase sabe a que estado puede pasar y rechaza
 * el resto de las transiciones.
 *
 * PATRON STATE: el motor no decide con ifs que transicion es valida; delega en su
 * estado actual, y cada transicion devuelve el estado siguiente.
 *
 * Contrato comun de las cuatro transiciones:
 * - No modifican el estado receptor: los estados son inmutables.
 * - Si la transicion es valida, devuelven el estado siguiente, que no es nulo.
 * - Si no lo es, lanzan TransicionInvalidaException y el motor conserva su estado.
 *
 * Ciclo valido: Disponible -> Preparando salto -> En warp -> Enfriamiento -> Disponible.
 */
public abstract class Estado {

    abstract Estado prepararSalto() throws TransicionInvalidaException;

    abstract Estado iniciarSalto() throws TransicionInvalidaException;

    abstract Estado finalizarSalto() throws TransicionInvalidaException;

    abstract Estado completarEnfriamiento() throws TransicionInvalidaException;

    abstract String nombre();
    
    boolean estaDisponible() {
        return false;
    }
}
