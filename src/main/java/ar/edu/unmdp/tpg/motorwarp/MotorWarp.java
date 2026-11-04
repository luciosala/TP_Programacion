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
 * Contratos:
 * - Que el estado actual no admita una transicion es un rechazo del dominio:
 *   se informa con TransicionInvalidaException (nunca en silencio) y el
 *   asistente lo registra en la bitacora.
 * - Las postcondiciones de cada transicion se comprueban con assert: si se
 *   acepta, el motor cambia de estado; si se rechaza, conserva el mismo.
 *
 * Invariantes (metodo invariante()):
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
        assert estadoActual.estaDisponible() : "El motor debe comenzar Disponible";
        assert invariante() : "Fallo invariante de MotorWarp";
    }

    /**
     * Invariante de clase del motor.
     *
     * @return true si el motor tiene un estado actual
     */
    private boolean invariante() {
        return estadoActual != null;
    }
    /**
     * Prepara el motor para realizar un salto.
     *
     * Postcondiciones:
     * - El motor queda en estado Preparando salto.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no está en estado Disponible
     */
    public void prepararSalto() throws TransicionInvalidaException {
        Estado anterior = estadoActual;
        try {
            estadoActual = estadoActual.prepararSalto();
        } catch (TransicionInvalidaException rechazo) {
            assert estadoActual == anterior : "Una transición rechazada no debe cambiar el estado";
            throw rechazo;
        }
        assert estadoActual != anterior : "Una transición aceptada debe cambiar el estado";
        assert invariante() : "Fallo invariante de MotorWarp";
    }

    /**
     * Inicia el salto del motor.
     *
     * Postcondiciones:
     * - El motor queda en estado En warp.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no está en estado Preparando salto
     */
    public void iniciarSalto() throws TransicionInvalidaException {
        Estado anterior = estadoActual;
        try {
            estadoActual = estadoActual.iniciarSalto();
        } catch (TransicionInvalidaException rechazo) {
            assert estadoActual == anterior : "Una transición rechazada no debe cambiar el estado";
            throw rechazo;
        }
        assert estadoActual != anterior : "Una transición aceptada debe cambiar el estado";
        assert invariante() : "Fallo invariante de MotorWarp";
    }

    /**
     * Finaliza el salto e inicia el enfriamiento del motor.
     *
     * Postcondiciones:
     * - El motor queda en estado Enfriamiento.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no está en estado En warp
     */
    public void finalizarSalto() throws TransicionInvalidaException {
        Estado anterior = estadoActual;
        try {
            estadoActual = estadoActual.finalizarSalto();
        } catch (TransicionInvalidaException rechazo) {
            assert estadoActual == anterior : "Una transición rechazada no debe cambiar el estado";
            throw rechazo;
        }
        assert estadoActual != anterior : "Una transición aceptada debe cambiar el estado";
        assert invariante() : "Fallo invariante de MotorWarp";
    }

    /**
     * Completa el enfriamiento del motor.
     *
     * Postcondiciones:
     * - El motor queda en estado Disponible.
     * - Si la operación se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no está en estado Enfriamiento
     */
    public void completarEnfriamiento() throws TransicionInvalidaException {
        Estado anterior = estadoActual;
        try {
            estadoActual = estadoActual.completarEnfriamiento();
        } catch (TransicionInvalidaException rechazo) {
            assert estadoActual == anterior : "Una transición rechazada no debe cambiar el estado";
            throw rechazo;
        }
        assert estadoActual != anterior : "Una transición aceptada debe cambiar el estado";
        assert invariante() : "Fallo invariante de MotorWarp";
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
