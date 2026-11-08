package ar.edu.unmdp.tpg.asistente;

import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;
import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;

import java.util.List;

import java.time.YearMonth;

/**
 * Contrato de un asistente de comando: la unica via para consultar y dar
 * ordenes a una nave. Quien use asistentes depende de esta interfaz y no
 * de una clase concreta, asi una nueva variante se agrega sin modificarlos.
 *
 * Contratos: las precondiciones son obligacion del invocante y se comprueban
 * con assert (java -ea); las excepciones checked se reservan para rechazos del
 * dominio que el cliente debe gestionar (y que el asistente registra).
 *
 * Invariantes que toda implementación debe mantener:
 * - El asistente opera siempre sobre la misma nave y la misma bitácora.
 * - Toda orden aceptada o rechazada queda registrada en la bitácora.
 * - Una orden rechazada no modifica el estado de la nave.
 */
public interface AsistenteDeComando {

    /**
     * Identifica la nave que opera este asistente.
     *
     * Postcondiciones:
     * - Devuelve siempre el mismo id durante toda la vida del asistente.
     * - El id no es nulo ni vacío.
     *
     * @return identificador de la nave operada
     */
    String idNave();

    /**
     * Ordena asignar un tripulante a la nave.
     *
     * Precondiciones (assert):
     * - tripulante no es nulo.
     *
     * Postcondiciones:
     * - Si se acepta, la cantidad de tripulantes aumenta en uno y queda registrado en la bitácora.
     * - Si se rechaza, la tripulación permanece sin cambios y queda registrado el motivo.
     *
     * @param tripulante tripulante a asignar
     * @throws TripulacionInvalidaException rechazo de dominio: ya hay un tripulante con ese id en la nave
     */
    void asignarTripulante(Tripulante tripulante) throws TripulacionInvalidaException;

    /**
     * Consulta si la tripulación de la nave está lista para operar (un capitán y
     * al menos cuatro tripulantes más). Delega en la única operación de la nave
     * que define esa regla.
     *
     * Postcondiciones:
     * - El estado de la nave no se modifica.
     *
     * @return true si la tripulación está lista
     */
    boolean tripulacionLista();

    /**
     * Ordena cargar combustible en la nave.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - Si se acepta, el combustible aumenta exactamente en cantidad y queda entre 0 y 100.
     * - Si se rechaza, ningún recurso se modifica y queda registrado el motivo.
     *
     * @param cantidad cantidad de combustible a cargar
     * @throws LimiteRecursoExcedidoException rechazo de dominio: la carga supera la capacidad máxima
     */
    void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException;

    /**
     * Ordena cargar energía en la nave.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - Si se acepta, la energía aumenta exactamente en cantidad y queda entre 0 y 100.
     * - Si se rechaza, ningún recurso se modifica y queda registrado el motivo.
     *
     * @param cantidad cantidad de energía a cargar
     * @throws LimiteRecursoExcedidoException rechazo de dominio: la carga supera la capacidad máxima
     */
    void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException;

    /**
     * Ordena realizar el mantenimiento de la nave.
     *
     * Postcondiciones:
     * - El desgaste de la nave queda en 0.
     * - La nave deja de requerir mantenimiento.
     * - El combustible y la energía no se modifican.
     *
     */
    void realizarMantenimiento();

    /**
     * Comprueba si la nave dispone de los recursos que una operación va a necesitar.
     * Es lo que consultan las misiones antes de iniciarse.
     *
     * Precondiciones (assert):
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Ningún recurso se modifica, ni cuando la verificación resulta exitosa.
     * - Si no se lanza excepción, un consumo con esas mismas cantidades es aplicable.
     *
     * @param combustibleNecesario combustible que la operación va a consumir
     * @param energiaNecesaria energía que la operación va a consumir
     * @param desgasteAgregado desgaste que la operación va a generar
     * @throws RecursoInsuficienteException rechazo de dominio: falta combustible o energía
     * @throws LimiteRecursoExcedidoException rechazo de dominio: el desgaste superaría el límite de 100
     */
    void verificarRecursos(int combustibleNecesario, int energiaNecesaria, int desgasteAgregado) 
        throws RecursoInsuficienteException, LimiteRecursoExcedidoException;

    /**
     * Aplica sobre la nave el consumo de una operación controlada.
     *
     * Precondiciones (assert):
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Si se acepta, el combustible y la energía bajan y el desgaste sube exactamente
     *   en las cantidades indicadas, y los tres quedan entre 0 y 100.
     * - Si se rechaza, ningún recurso se modifica: el consumo es atómico.
     *
     * @param combustibleConsumido combustible a descontar
     * @param energiaConsumida energía a descontar
     * @param desgasteAgregado desgaste a sumar
     * @throws RecursoInsuficienteException rechazo de dominio: falta combustible o energía
     * @throws LimiteRecursoExcedidoException rechazo de dominio: el desgaste superaría el límite de 100
     */
    void consumirParaMision(int combustibleConsumido, int energiaConsumida, int desgasteAgregado)
        throws RecursoInsuficienteException, LimiteRecursoExcedidoException;

    /**
     * Descuenta energía de la nave. Lo usa la acción final de las misiones.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - Si se acepta, la energía baja exactamente en cantidad y queda entre 0 y 100.
     * - Si se rechaza, la energía permanece sin cambios.
     *
     * @param cantidad energía a descontar
     * @throws RecursoInsuficienteException rechazo de dominio: la nave no tiene esa energía disponible
     */
    void consumirEnergia(int cantidad) throws RecursoInsuficienteException;

    /**
     * Ordena al Motor Warp preparar un salto.
     *
     * Postcondiciones:
     * - Si la transición es válida, el motor queda en Preparando salto y queda registrado.
     * - Si no lo es, el motor no cambia de estado y queda registrado el rechazo.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el estado actual no admite preparar el salto
     */
    void prepararSalto() throws TransicionInvalidaException;

    /**
     * Ordena al Motor Warp iniciar el salto.
     *
     * Postcondiciones:
     * - Si la transición es válida, el motor queda En warp y queda registrado.
     * - Si no lo es, el motor no cambia de estado y queda registrado el rechazo.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el estado actual no admite iniciar el salto
     */
    void iniciarSalto() throws TransicionInvalidaException;

    /**
     * Ordena al Motor Warp finalizar el salto.
     *
     * Postcondiciones:
     * - Si la transición es válida, el motor queda en Enfriamiento y queda registrado.
     * - Si no lo es, el motor no cambia de estado y queda registrado el rechazo.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el estado actual no admite finalizar el salto
     */
    void finalizarSalto() throws TransicionInvalidaException;

    /**
     * Ordena al Motor Warp completar el enfriamiento.
     *
     * Postcondiciones:
     * - Si la transición es válida, el motor queda Disponible y queda registrado.
     * - Si no lo es, el motor no cambia de estado y queda registrado el rechazo.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el estado actual no admite completar el enfriamiento
     */
    void completarEnfriamiento() throws TransicionInvalidaException;

    /**
     * Consulta en qué estado está el Motor Warp.
     *
     * Postcondiciones:
     * - El estado del motor no se modifica.
     * - Devuelve uno de: Disponible, Preparando salto, En warp, Enfriamiento.
     *
     * @return el nombre del estado actual del Motor Warp
     */
    String estadoDelMotor();

    /**
     * Consulta si el Motor Warp puede iniciar una nueva operación.
     *
     * Postcondiciones:
     * - El estado del motor no se modifica.
     * - Devuelve true si y solo si el motor está en estado Disponible.
     *
     * @return si el motor está en condiciones de iniciar una operación
     */
    boolean motorDisponible(); 

    /**
     * Ordena la ejecución completa de una misión: preparar, ejecutar, evaluar y cerrar.
     * El orden de esas cuatro etapas lo fija el Template Method de la misión, no el asistente.
     * Sirve indistintamente para M-01, M-02 o M-03.
     *
     * Precondiciones (assert):
     * - mision no es nula.
     * - La misión fue encomendada a este mismo asistente.
     *
     * Postcondiciones:
     * - Si la misión se completa, con éxito o no, devuelve su informe y queda registrada en la bitácora.
     * - Si no puede prepararse, no se consume ningún recurso y se propaga la excepción.
     *
     * @param mision misión a ejecutar (M-01, M-02 o M-03)
     * @return el informe producido al cerrar la misión
     * @throws NaveException rechazo de dominio: la misión ya se realizó, no puede prepararse o falla durante su ejecución
     */
    InformeMision ejecutarMision(Mision mision) throws NaveException;

    /**
     * Arma el informe de una misión combinando lo que la misión acumuló durante su
     * ciclo con la foto de la nave al momento del cierre. La misión no conoce la nave,
     * por eso el informe lo produce el asistente.
     *
     * Precondiciones (assert):
     * - acciones no es nula.
     * - Las tres cantidades consumidas son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Devuelve un informe con los consumos recibidos y el estado de la nave en este momento.
     * - El informe es inmutable: refleja el cierre y no cambia si la nave cambia después.
     * - La nave y la bitácora no se modifican.
     *
     * @param mision nombre de la misión ejecutada
     * @param descripcion descripción de la misión
     * @param objetivo objetivo evaluado
     * @param acciones acciones realizadas durante la misión, en orden
     * @param combustibleConsumido combustible consumido por la misión
     * @param energiaConsumida energía consumida por la misión
     * @param desgasteGenerado desgaste generado por la misión
     * @param exito resultado de la evaluación
     * @return el informe de la misión
     */
    InformeMision crearInforme(String mision, String descripcion, String objetivo,List<String> acciones,
                                            int combustibleConsumido, int energiaConsumida, int desgasteGenerado,boolean exito);

    /**
     * Consulta si la nave conserva un estado operativo válido.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - Devuelve true si y solo si tiene la tripulación mínima y sus tres
     *   recursos están dentro del rango de 0 a 100.
     *
     * @return si la nave está operativa
     */
    boolean naveEstaOperativa();

    // encuentros en el tablero
    String interactuarCon(ElementoDelUniverso elemento);   // la nave se encuentra con un elemento
    boolean naveDestruida();                                // ¿la nave sigue viva?

    void recolectarGemas(int cantidad);                     // M-02
    int gemasDisponibles();

    /**
     * Consulta el combustible de la nave.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - El valor devuelto está entre 0 y 100.
     *
     * @return combustible disponible
     */
    int combustibleDisponible();

    /**
     * Consulta la energía de la nave.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - El valor devuelto está entre 0 y 100.
     *
     * @return energía disponible
     */
    int energiaDisponible();

    /**
     * Consulta el desgaste de la nave.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - El valor devuelto está entre 0 y 100.
     *
     * @return desgaste acumulado
     */
    int desgasteActual();

    /**
     * Consulta si la nave necesita mantenimiento.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - Devuelve true si y solo si el desgaste es mayor o igual a 80.
     *
     * @return si la nave requiere mantenimiento
     */
    boolean requiereMantenimiento();

    /**
     * Consulta cuántos tripulantes tiene la nave.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - El valor devuelto es mayor o igual a 0.
     *
     * @return cantidad de tripulantes asignados
     */
    int cantidadTripulantes();

    /**
     * Ordena liquidar el haber mensual de un tripulante para un período dado.
     *
     * Precondiciones (assert):
     * - periodo y tripulante no son nulos.
     *
     * Postcondiciones:
     * - Devuelve el haber compuesto por Decorator: sueldo base del cargo, adicional
     *   por antigüedad, subsidio por origen y, si el cargo es Consejero, el adicional
     *   por los consejos registrados en ese período.
     * - El total del haber es finito y no negativo.
     * - El tripulante no se modifica y queda registrado en la bitácora el total liquidado.
     *
     * @param periodo período de liquidación
     * @param tripulante tripulante a liquidar
     * @return el haber calculado
     */
    Haber liquidarHaberes(YearMonth periodo, Tripulante tripulante);

    /**
     * Registra un evento en la bitácora. Es la única vía por la que las misiones
     * dejan constancia de lo que hacen.
     *
     * Precondiciones (assert):
     * - tipo y descripcion no son nulos ni vacíos.
     *
     * Postcondiciones:
     * - Se agrega un evento al final de la bitácora, con su fecha y hora.
     * - Los eventos anteriores permanecen sin cambios y en el mismo orden.
     *
     * @param tipo tipo de evento (MISION, MOTOR, RECURSOS, ERROR, COMANDO, HABERES)
     * @param descripcion detalle de lo ocurrido
     */
    void registrarEvento(String tipo, String descripcion);

    /**
     * Consulta los eventos registrados hasta el momento.
     *
     * Postcondiciones:
     * - Devuelve los eventos del más viejo al más nuevo.
     * - La lista devuelta no se puede modificar: la bitácora solo cambia por registrarEvento.
     *
     * @return los eventos de la bitácora, en orden temporal
     */
    List<EventoBitacora> consultarBitacora();
}
