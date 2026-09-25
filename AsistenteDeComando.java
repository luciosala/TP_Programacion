import java.util.List;

import java.time.YearMonth;

/**
 * Asistente de comando, unicamente coordina las ordenes dadas por el cliente (el main esta primera parte), maneja errores del dominio e informa en bitacora
 * No implementa el comportamiento de los metodos, unicamente los invoca.
 *
 * Es el unico que conoce a la vez la nave, la bitacora y las misiones. Las misiones
 * no conocen la nave ni la bitacora: todo lo que necesitan se lo piden al asistente.
 */


public class AsistenteDeComando{

    private final Nave nave;
    private final Bitacora bitacora;

    /**
     * Construye el asistente de comando sobre una nave ya creada por el cliente.
     *
     * Precondiciones:
     * - La nave no es nula.
     * - La bitacora no es nula.
     *
     * Poscondiciones:
     * - El asistente queda asociado a la nave y a la bitacora que recibe.
     * - Queda registrada la incorporacion de la nave al sistema.
     *
     * @param nave nave que el asistente va a operar.
     * @param bitacora bitacora donde se registra las ordenes y resultados.
     * @throws IllegalArgumentException si la nave o la bitacora son nulas.
     */
    public AsistenteDeComando (Nave nave, Bitacora bitacora){
        if (nave == null){
            throw new IllegalArgumentException ("El asistente requiere una nave");
        }
        if (bitacora == null){
            throw new IllegalArgumentException ("El asistente requiere una bitácora");
        }

        this.nave = nave;
        this.bitacora = bitacora;

        bitacora.registrar("COMANDO",
            "Nave " + nave.getId() + " (" + nave.getTipo() + ") incorporada al sistema");
    }

    public Bitacora getBitacora(){
        return bitacora;
    }


    // Tripulacion
    /**
     * Ordena asignar un tripulante a la nave.
     *
     * Precondiciones:
     * - tripulante no es nulo.
     * - No existe ya un tripulante con el mismo id en la nave.
     *
     * Postcondiciones:
     * - Si se acepta, el tripulante queda incorporado a la nave y se
     *   registra en la bitácora.
     * - Si se rechaza, la nave no se modifica y se registra el motivo.
     *
     * @param tripulante tripulante a asignar
     * @throws IllegalArgumentException si el tripulante es inválido, o si ya existe un tripulante con ese id en la nave
     */
    public void asignarTripulante(Tripulante tripulante) {
        try {
            nave.agregarTripulante(tripulante);
            bitacora.registrar("COMANDO", "Tripulante " + tripulante.getId() + " asignado a la nave " + nave.getId());
        } catch (IllegalArgumentException error) {
            bitacora.registrar("ERROR", "No se pudo asignar tripulante: " + error.getMessage());
            throw error;
        }
    }

    /**
     * Verifica que la nave cumpla la composicion minima de tripulacion.
     *
     * Postcondiciones:
     * - Si no la cumple, queda registrado el motivo y se propaga la excepcion.
     *
     * @throws TripulacionInvalidaException si falta el capitan o los cuatro tripulantes adicionales
     */
    public void validarTripulacionMinima() throws TripulacionInvalidaException {
        try {
            nave.validarTripulacionMinima();
        } catch (TripulacionInvalidaException error) {
            bitacora.registrar("ERROR", "Nave " + nave.getId() + ": " + error.getMessage());
            throw error;
        }
    }


    // Recursos y mantenimiento
    /**
     * Ordena cargar combustible en la nave.
     *
     * Precondiciones:
     * - cantidad es mayor que 0 y no supera la capacidad restante de la nave.
     *
     * Postcondiciones:
     * - Si se acepta, el combustible aumenta y se registra en la bitácora.
     * - Si se rechaza, los recursos no se modifican y se registra el motivo.
     *
     * @param cantidad cantidad a cargar
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad máxima
     */
    public void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException {
        try {
            nave.getRecursos().cargarCombustible(cantidad);
            bitacora.registrar("RECURSOS",
                "Nave " + nave.getId() + ": combustible cargado (+" + cantidad + ")");
        } catch (LimiteRecursoExcedidoException error) {
            bitacora.registrar("ERROR",
                "Nave " + nave.getId() + ": no se pudo cargar combustible: " + error.getMessage());
            throw error;
        }
    }

    /**
     * @param cantidad cantidad a cargar
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad máxima
     */
    public void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException {
        try {
            nave.getRecursos().cargarEnergia(cantidad);
            bitacora.registrar("RECURSOS",
                "Nave " + nave.getId() + ": energía cargada (+" + cantidad + ")");
        } catch (LimiteRecursoExcedidoException error) {
            bitacora.registrar("ERROR",
                "Nave " + nave.getId() + ": no se pudo cargar energía: " + error.getMessage());
            throw error;
        }
    }

    /**
     * Ordena realizar el mantenimiento de la nave.
     *
     * Postcondiciones:
     * - El desgaste de la nave queda en 0 y se registra en la bitácora.
     */
    public void realizarMantenimiento() {
        nave.getRecursos().realizarMantenimiento();
        bitacora.registrar("RECURSOS", "Nave " + nave.getId() + ": mantenimiento realizado (desgaste reiniciado)");
    }

    /**
     * Comprueba si la nave dispone de los recursos que una operacion va a necesitar,
     * sin modificarlos. Es lo que consultan las misiones antes de iniciarse.
     *
     * Postcondiciones:
     * - Si algo no alcanza, los recursos quedan intactos y se propaga la excepcion.
     *
     * @param combustibleNecesario combustible que la operacion va a consumir
     * @param energiaNecesaria energia que la operacion va a consumir
     * @param desgasteAgregado desgaste que la operacion va a generar
     * @throws RecursoInsuficienteException si falta combustible o energia
     * @throws LimiteRecursoExcedidoException si el desgaste superaria el limite
     */
    public void verificarRecursos(int combustibleNecesario, int energiaNecesaria, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        nave.getRecursos().verificarDisponibilidad(combustibleNecesario, energiaNecesaria, desgasteAgregado);
    }

    /**
     * Aplica sobre la nave el consumo de una operacion controlada.
     *
     * Postcondiciones:
     * - Si algo no alcanza, no se modifica ningun recurso.
     *
     * @param combustibleConsumido combustible a descontar
     * @param energiaConsumida energia a descontar
     * @param desgasteAgregado desgaste a sumar
     * @throws RecursoInsuficienteException si falta combustible o energia
     * @throws LimiteRecursoExcedidoException si el desgaste superaria el limite
     */
    public void consumirParaMision(int combustibleConsumido, int energiaConsumida, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        nave.getRecursos().consumirParaMision(combustibleConsumido, energiaConsumida, desgasteAgregado);
    }

    /**
     * @param cantidad energia a descontar
     * @throws RecursoInsuficienteException si la nave no tiene esa energia disponible
     */
    public void consumirEnergia(int cantidad) throws RecursoInsuficienteException {
        nave.getRecursos().consumirEnergia(cantidad);
    }


    // Motor Warp
    /**
     * Ordena al Motor Warp preparar un salto.
     *
     * Postcondiciones:
     * - Si la transicion es valida, el motor cambia de estado y queda registrado.
     * - Si no lo es, el motor no cambia y queda registrado el rechazo.
     *
     * @throws TransicionInvalidaException si el estado actual no admite preparar el salto
     */
    public void prepararSalto() throws TransicionInvalidaException {
        bitacora.registrar("COMANDO", "Orden recibida: preparar salto");
        try {
            nave.getMotorWarp().prepararSalto();
            bitacora.registrar("MOTOR", "Motor Warp en " + nave.getMotorWarp().getEstadoActual());
        } catch (TransicionInvalidaException error) {
            bitacora.registrar("ERROR", "Motor Warp: " + error.getMessage());
            throw error;
        }
    }

    /**
     * @throws TransicionInvalidaException si el estado actual no admite iniciar el salto
     */
    public void iniciarSalto() throws TransicionInvalidaException {
        bitacora.registrar("COMANDO", "Orden recibida: iniciar salto");
        try {
            nave.getMotorWarp().iniciarSalto();
            bitacora.registrar("MOTOR", "Motor Warp en " + nave.getMotorWarp().getEstadoActual());
        } catch (TransicionInvalidaException error) {
            bitacora.registrar("ERROR", "Motor Warp: " + error.getMessage());
            throw error;
        }
    }

    /**
     * @throws TransicionInvalidaException si el estado actual no admite finalizar el salto
     */
    public void finalizarSalto() throws TransicionInvalidaException {
        bitacora.registrar("COMANDO", "Orden recibida: finalizar salto");
        try {
            nave.getMotorWarp().finalizarSalto();
            bitacora.registrar("MOTOR", "Motor Warp en " + nave.getMotorWarp().getEstadoActual());
        } catch (TransicionInvalidaException error) {
            bitacora.registrar("ERROR", "Motor Warp: " + error.getMessage());
            throw error;
        }
    }

    /**
     * @throws TransicionInvalidaException si el estado actual no admite completar el enfriamiento
     */
    public void completarEnfriamiento() throws TransicionInvalidaException {
        bitacora.registrar("COMANDO", "Orden recibida: completar enfriamiento");
        try {
            nave.getMotorWarp().completarEnfriamiento();
            bitacora.registrar("MOTOR", "Motor Warp en " + nave.getMotorWarp().getEstadoActual());
        } catch (TransicionInvalidaException error) {
            bitacora.registrar("ERROR", "Motor Warp: " + error.getMessage());
            throw error;
        }
    }

    /**
     * Manda el motor a enfriarse al cerrarse una mision.
     *
     * En la Entrega 1 la mision no lleva el motor a warp, asi que la transicion
     * casi siempre no corresponde: en ese caso queda registrada y el cierre de la
     * mision sigue igual. En la Entrega 2, con el motor en warp durante el recorrido,
     * esta orden si se va a completar.
     *
     * Postcondiciones:
     * - El resultado, se haya aplicado o no, queda registrado en la bitacora.
     * - Nunca interrumpe el cierre de la mision.
     */
    public void enfriarMotorTrasMision() {
        try {
            nave.getMotorWarp().finalizarSalto();
            bitacora.registrar("MOTOR",
                "Motor Warp en " + nave.getMotorWarp().getEstadoActual() + " al cerrar la misión");
        } catch (TransicionInvalidaException error) {
            bitacora.registrar("MOTOR",
                "No corresponde enfriar el motor al cerrar la misión: " + error.getMessage());
        }
    }

    /**
     * @return el nombre del estado actual del Motor Warp
     */
    public String estadoDelMotor() {
        return nave.getMotorWarp().getEstadoActual();
    }


    //Misiones (M-01, M-02, M-03)

     /**
     * Ordena la ejecución completa de una misión (preparar, ejecutar, evaluar y cerrar), delegando en el Template Method a la mision.
     * Sirve indistintamente para MisionIntercepcion (M-01), MisionRecoleccion (M-02) o MisionRetornoSeguro (M-03).
     *
     * Precondiciones:
     * - mision no es nula.
     *
     * Postcondiciones:
     * - Si la misión se completa (con éxito o no), devuelve su InformeMision.
     * - Si la misión no puede prepararse o falla, se propaga la excepción y queda registrado en la bitácora que la orden no se completó.
     * @param mision misión a ejecutar (M-01, M-02 o M-03)
     * @return el informe producido al cerrar la misión
     * @throws NaveException si la misión no puede prepararse o falla durante su ejecución
     */
    public InformeMision ejecutarMision(Mision mision) throws NaveException {
        if (mision == null) {
            throw new IllegalArgumentException("No se puede ejecutar una misión nula");
        }

        bitacora.registrar("COMANDO", "Orden recibida: ejecutar " + mision.getClass().getSimpleName());
        try {
            InformeMision informe = mision.realizarMision();
            bitacora.registrar("COMANDO", "Orden resuelta: misión finalizada");
            return informe;
        } catch (NaveException error) {
            bitacora.registrar("ERROR", "Orden no completada: " + error.getMessage());
            throw error;
        }
    }

    /**
     * Arma el informe de una mision combinando lo que la mision acumulo durante su
     * ciclo con la foto de la nave al momento del cierre. La mision no conoce la nave,
     * por eso el informe lo produce el asistente.
     *
     * @param mision nombre de la mision ejecutada
     * @param descripcion descripcion de la mision
     * @param objetivo objetivo evaluado
     * @param acciones acciones realizadas durante la mision
     * @param combustibleConsumido combustible consumido por la mision
     * @param energiaConsumida energia consumida por la mision
     * @param desgasteGenerado desgaste generado por la mision
     * @param exito resultado de la evaluacion
     * @return el informe de la mision
     */
    public InformeMision crearInforme(String mision, String descripcion, String objetivo,
                                      List<String> acciones,
                                      int combustibleConsumido, int energiaConsumida, int desgasteGenerado,
                                      boolean exito) {
        return new InformeMision(mision, descripcion, objetivo, acciones,
            combustibleConsumido, energiaConsumida, desgasteGenerado, exito, nave);
    }


    // Consultas sobre la nave
    /**
     * @return si la nave conserva un estado operativo válido
     */
    public boolean naveEstaOperativa() {
        return nave.estaOperativa();
    }

    public String idNave() {
        return nave.getId();
    }

    public int combustibleDisponible() {
        return nave.getRecursos().getCombustible();
    }

    public int energiaDisponible() {
        return nave.getRecursos().getEnergia();
    }

    public int desgasteActual() {
        return nave.getRecursos().getDesgaste();
    }

    public boolean requiereMantenimiento() {
        return nave.getRecursos().requiereMantenimiento();
    }

    public int cantidadTripulantes() {
        return nave.cantidadTripulantes();
    }


    // Haberes
    /**
     * Ordena liquidar el haber mensual de un tripulante para un período dado.
     *
     * Precondiciones:
     * - periodo y tripulante no son nulos.
     *
     * Postcondiciones:
     * - Devuelve el haber compuesto por Decorator según cargo, antigüedad, origen y (si corresponde) consejos del tripulante.
     * - Queda registrado en la bitácora el total liquidado.
     *
     * @param periodo período de liquidación
     * @param tripulante tripulante a liquidar
     * @return el haber calculado
     */

     public Haber liquidarHaberes(YearMonth periodo, Tripulante tripulante) {
        LiquidacionHaberes liquidacion = new LiquidacionHaberes(periodo);
        Haber haber = liquidacion.liquidar(tripulante);
        bitacora.registrar("HABERES", "Liquidado " + tripulante.getId() + " (" + periodo + "): " + haber.calcularTotal() + " PG");
        return haber;
     }


    // Registro
    /**
     * Registra un evento en la bitacora. Es la unica via por la que las misiones
     * dejan constancia de lo que hacen.
     *
     * Precondiciones:
     * - tipo y descripcion no son nulos ni vacios.
     *
     * @param tipo tipo de evento (MISION, MOTOR, RECURSOS, ERROR, COMANDO)
     * @param descripcion detalle de lo ocurrido
     * @throws IllegalArgumentException si el tipo o la descripcion son nulos o vacios
     */
    public void registrarEvento(String tipo, String descripcion) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de evento no puede ser nulo ni vacío");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del evento no puede ser nula ni vacía");
        }
        bitacora.registrar(tipo, descripcion);
    }

    // Consulta eventos registrados hasta el momento.
    /**
     * @return los eventos de la bitacora, en orden temporal.
     */
     public List<EventoBitacora> consultarBitacora() {
        return bitacora.consultarEventos();
    }
}
