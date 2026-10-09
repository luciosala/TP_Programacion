package ar.edu.unmdp.tpg.motorwarp;

import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;

/**
 * Motor Warp de una nave. Conoce en que estado esta y delega en el las cuatro
 * transiciones del ciclo de salto.
 *
 * PATRON STATE: el motor no consulta su estado con ifs ni con un enum; le pide al
 * estado actual la transicion y se queda con el estado que este le devuelve. Para
 * agregar un estado nuevo alcanza con una subclase de Estado.
 *
 * Invariantes:
 * - El estado actual nunca es nulo.
 * - Una transicion rechazada no cambia el estado del motor.
 */
public class MotorWarp{
    private Estado estadoActual;

    /**
     * Construye el Motor Warp de una nave.
     *
     * Postcondiciones:
     * - El motor queda en estado Disponible.
     * - El motor esta en condiciones de preparar un salto.
     */
    public MotorWarp(){
        estadoActual=new Disponible();
    }
    /**
     * Prepara el motor para realizar un salto.
     *
     * Precondiciones:
     * - El motor está en estado Disponible.
     *
     * Postcondiciones:
     * - El motor queda en estado Preparando salto.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no está en estado Disponible
     */
    public void prepararSalto() throws TransicionInvalidaException {
        estadoActual = estadoActual.prepararSalto();
    }

    /**
     * Inicia el salto del motor.
     *
     * Precondiciones:
     * - El motor está en estado Preparando salto.
     *
     * Postcondiciones:
     * - El motor queda en estado En warp.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no está en estado Preparando salto
     */
    public void iniciarSalto() throws TransicionInvalidaException {
        estadoActual = estadoActual.iniciarSalto();
    }

    /**
     * Finaliza el salto e inicia el enfriamiento del motor.
     *
     * Precondiciones:
     * - El motor está en estado En warp.
     *
     * Postcondiciones:
     * - El motor queda en estado Enfriamiento.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no está en estado En warp
     */
    public void finalizarSalto() throws TransicionInvalidaException {
        estadoActual = estadoActual.finalizarSalto();
    }

    /**
     * Completa el enfriamiento del motor.
     *
     * Precondiciones:
     * - El motor está en estado Enfriamiento.
     *
     * Postcondiciones:
     * - El motor queda en estado Disponible.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no está en estado Enfriamiento
     */
    public void completarEnfriamiento() throws TransicionInvalidaException {
        estadoActual = estadoActual.completarEnfriamiento();
    }

    /**
     * Consulta el nombre del estado actual.
     *
     * Postcondiciones:
     * - El motor no se modifica.
     * - Devuelve uno de: Disponible, Preparando salto, En warp, Enfriamiento.
     *
     * @return nombre del estado actual del motor
     */
    public String getEstadoActual() { 
        return estadoActual.nombre(); 
    }
    
    /** @return si el motor está en condiciones de iniciar una operación. */
    public boolean estaDisponible() {
        return estadoActual.estaDisponible();
    }
    
}
