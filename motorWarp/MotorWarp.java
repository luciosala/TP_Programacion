public class MotorWarp{
    private Estado estadoActual;

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

    public String getEstadoActual() { return estadoActual.nombre(); }
    
}
