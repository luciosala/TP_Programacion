import java.util.List;

import java.time.YearMonth; 

/**
 * Asistente de comando, unicamente coordina las ordenes dadas por el cliente (el main esta primera parte), maneja errores del dominio e informa en bitacora
 * No implementa el comportamiento de los metodos, unicamente los invoca.
 */


public class AsistenteDeComando{

    private final Bitacora bitacora;

    /**
     * Construye el asistente de comando.
     * 
     * Precondiciones:
     * - La bitacora no es nula.
     * 
     * Poscondiciones:
     * - El asistente queda asociado a la bitacora que recibe.
     * 
     * @param bitacora bitacora donde se registra las ordenes y resultados.
     * @throws IllegalArgumentException si la bitacora es nula.
     */



    public AsistenteDeComando (Bitacora bitacora){
        if (bitacora == null){
            throw new IllegalArgumentException ("El asistente requiere una bitácora");
        }
        this.bitacora=bitacora;

    }

    public Bitacora getBitacora(){
        return bitacora;
    }

    
    // Naves
    /**
     * Ordena la creación de una nave mediante la fábrica.
     *
     * Precondiciones:
     * - id y tipo no son nulos ni vacíos (los valida naveFactory).
     * - tipo corresponde a un tipo de nave admitido.
     *
     * Postcondiciones:
     * - Si la creación tiene éxito, devuelve una nave nueva en estado válido
     *   y queda un evento "COMANDO" en la bitácora.
     * - Si se rechaza, no se crea ninguna nave, queda un evento "ERROR"
     *   registrado y se propaga la excepción.
     *
     * @param id identificador de la nave
     * @param tipo tipo de nave solicitado
     * @return la nave creada
     * @throws IllegalArgumentException si el tipo o el id son inválidos
     */

    public Nave crearNave (String id, String tipo){
        try {
            Nave nave = naveFactory.createNave(id, tipo);
            bitacora.registrar("COMANDO", "Nave creada: " + id + " (" + tipo + ")");
            return nave;
        } catch (IllegalArgumentException error) {
            bitacora.registrar("ERROR", "No se pudo crear la nave " + id + ": " + error.getMessage());
            throw error;
        }  
    }  

     /**
     * Ordena asignar un tripulante a una nave.
     *
     * Precondiciones:
     * - nave y tripulante no son nulos.
     * - No existe ya un tripulante con el mismo id en la nave.
     *
     * Postcondiciones:
     * - Si se acepta, el tripulante queda incorporado a la nave y se
     *   registra en la bitácora.
     * - Si se rechaza, la nave no se modifica y se registra el motivo.
     *
     * @param nave nave que recibe al tripulante
     * @param tripulante tripulante a asignar
     * @throws IllegalArgumentException si la nave o el tripulante son inválidos, o si ya existe un tripulante con ese id en la nave
     */


    public void asignarTripulante(Nave nave, Tripulante tripulante) {
        try {
            nave.agregarTripulante(tripulante);
            bitacora.registrar("COMANDO", "Tripulante " + tripulante.getId() + " asignado a la nave " + nave.getId());
        } catch (IllegalArgumentException error) {
            bitacora.registrar("ERROR", "No se pudo asignar tripulante: " + error.getMessage());
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
     * @param nave nave a la que se carga combustible
     * @param cantidad cantidad a cargar
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad máxima
     */
    public void cargarCombustible(Nave nave, int cantidad) throws LimiteRecursoExcedidoException {
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
     * @param nave nave a la que se carga energía
     * @param cantidad cantidad a cargar
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad máxima
     */
    public void cargarEnergia(Nave nave, int cantidad) throws LimiteRecursoExcedidoException {
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
     *
     * @param nave nave a la que se realiza mantenimiento
     */
    public void realizarMantenimiento(Nave nave) {
        nave.getRecursos().realizarMantenimiento();
        bitacora.registrar("RECURSOS", "Nave " + nave.getId() + ": mantenimiento realizado (desgaste reiniciado)");
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

    // Consulta eventos registrados hasta el momento.
    /**
     * @return los eventos de la bitacora, en orden temporal.
     */
     public List<EventoBitacora> consultarBitacora() {
        return bitacora.consultarEventos();
    }
}


