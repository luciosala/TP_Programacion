package ar.edu.unmdp.tpg.naves;

import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.motorwarp.MotorWarp;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;

import java.util.ArrayList;
import java.util.List;


/**
 * Nave de la flota: su identidad, su tripulacion, sus recursos y su Motor Warp.
 *
 * Encapsula recursos y motor: nadie los alcanza por fuera de la nave, todas las
 * operaciones sobre ellos pasan por los metodos de esta clase. Las subclases
 * (Combate, Carguera, Exploradora) solo se diferencian en la configuracion inicial
 * con que las crea NaveFactory.
 *
 * Invariantes:
 * - El id y el tipo no cambian durante la vida de la nave.
 * - Los tres recursos se mantienen siempre entre 0 y 100.
 * - No hay dos tripulantes con el mismo id a bordo.
 */
abstract public class Nave {
    private final String id;
    private final String tipo;
    private final Recursos recursos;
    private final MotorWarp motorWarp;
    private final List<Tripulante> tripulacion = new ArrayList<>();

    /**
     * Construye una nave con identidad, tipo, recursos y motor Warp.
     *
     * Precondiciones:
     * - id y tipo no son nulos, vacíos ni contienen solo espacios.
     * - recursos y motorWarp no son nulos.
     *
     * Postcondiciones:
     * - La nave conserva el id y el tipo recibidos.
     * - La nave utiliza los recursos y el motorWarp recibidos.
     * - La tripulación comienza vacía.
     *
     * @param id identificador de la nave
     * @param tipo tipo de nave
     * @param recursos recursos que utilizará la nave
     * @param motorWarp motor Warp que utilizará la nave
     * @throws IllegalArgumentException si se incumple alguna precondición
     */
    public Nave(String id, String tipo, Recursos recursos, MotorWarp motorWarp) {
        
        if (id == null || id.isBlank()){
            throw new IllegalArgumentException("El id no puede ser nulo ni vacio");
        }

        if (tipo == null || tipo.isBlank()){
            throw new IllegalArgumentException("El tipo no puede ser nulo ni vacio");
        }

        if (recursos == null){
            throw new IllegalArgumentException("Los recursos no pueden ser nulos");
        }

        if (motorWarp == null){
            throw new IllegalArgumentException("El motor no puede ser nulo");
        }

        this.id = id;
        this.tipo = tipo;
        this.recursos = recursos;
        this.motorWarp = motorWarp;
    }

    /**
     * Identifica la nave.
     *
     * Postcondiciones:
     * - Devuelve siempre el mismo id: la identidad de la nave no cambia.
     * - El id no es nulo ni vacio.
     *
     * @return identificador de la nave
     */
    public String getId() { return id; }
    /**
     * Informa el tipo de nave.
     *
     * Postcondiciones:
     * - Devuelve siempre el mismo tipo: no cambia durante la vida de la nave.
     * - El tipo no es nulo ni vacio.
     *
     * @return tipo de nave (combate, carguero o exploradora)
     */
    public String getTipo() { return tipo; }    
    
    /**
     * Incorpora un tripulante a la nave.
     *
     * Precondiciones:
     * - t no es nulo.
     * - No hay ya un tripulante con el mismo id en la nave.
     *
     * Postcondiciones:
     * - La cantidad de tripulantes aumenta exactamente en uno.
     * - El tripulante queda consultable por su id.
     * - Si se rechaza, la tripulacion permanece sin cambios.
     *
     * @param t tripulante a incorporar
     * @throws IllegalArgumentException si el tripulante es nulo o su id ya esta en la nave
     */
    public void agregarTripulante(Tripulante t) {
        if (t == null) {
            throw new IllegalArgumentException("El tripulante no puede ser nulo");
        }

        if (buscarTripulantePorId(t.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un tripulante con ese id en la nave");
        }

        tripulacion.add(t);
    }

    // Devuelve el tripulante encontrado, o null si no está en la nave.
    /**
     * Busca un tripulante de la nave por su id.
     *
     * Precondiciones:
     * - id no es nulo, vacio ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La tripulacion no se modifica.
     * - Devuelve el tripulante con ese id, o null si no esta en la nave.
     *
     * @param id id del tripulante buscado
     * @return el tripulante encontrado, o null si no esta en la nave
     * @throws IllegalArgumentException si el id es nulo o vacio
     */
    public Tripulante buscarTripulantePorId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id no puede ser nulo ni vacío");
        }

        for (Tripulante tripulante : tripulacion) {
            if (tripulante.getId().equals(id)) {
                return tripulante;
            }
        }

        return null;
    }

    /**
     * Cuenta los tripulantes asignados a la nave.
     *
     * Postcondiciones:
     * - La tripulacion no se modifica.
     * - El valor devuelto es mayor o igual a 0.
     *
     * @return cantidad de tripulantes
     */
    public int cantidadTripulantes() {
        return tripulacion.size();
    }

    // se fija que haya un capitan y 4 integrantes mas
    /**
     * Consulta si la nave cumple la composicion minima exigida: un capitan y
     * al menos cuatro tripulantes adicionales.
     *
     * Postcondiciones:
     * - La tripulacion no se modifica.
     * - Devuelve true si y solo si hay 5 tripulantes o mas y al menos uno tiene cargo Capitan.
     *
     * @return si la nave tiene la tripulacion minima
     */
    public boolean tieneTripulacionMinima() {
        if (cantidadTripulantes() < 5) {
            return false;
        }

        for (Tripulante tripulante : tripulacion) {
            if (tripulante.getCargo() instanceof Capitan) {
                return true;
            }
        }

        return false;
    }

    /**
     * Exige que la nave cumpla la composicion minima de tripulacion.
     *
     * Postcondiciones:
     * - La nave no se modifica en ningun caso.
     * - No lanza excepcion si y solo si tieneTripulacionMinima() devuelve true.
     *
     * @throws TripulacionInvalidaException si falta el capitan o los cuatro tripulantes adicionales
     */
    public void validarTripulacionMinima() throws TripulacionInvalidaException {
        if (!tieneTripulacionMinima()) {
            throw new TripulacionInvalidaException(
                "La nave requiere al menos un capitán y cuatro tripulantes adicionales"
            );
        }
    }

    /**
     * Indica si la nave conserva un estado operativo válido para E1.
     *
     * @return {@code true} si tiene la tripulación mínima y sus recursos
     *         permanecen dentro de los rangos permitidos
     */
    public boolean estaOperativa() {
        return tieneTripulacionMinima()
            && recursos.getCombustible() >= 0
            && recursos.getCombustible() <= 100
            && recursos.getEnergia() >= 0
            && recursos.getEnergia() <= 100
            && recursos.getDesgaste() >= 0
            && recursos.getDesgaste() <= 100;
    }

        // ---------- Recursos: la nave delega en su objeto Recursos ----------

    /**
     * Consulta el combustible de la nave.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - El valor devuelto esta entre 0 y 100.
     *
     * @return combustible disponible
     */
    public int getCombustible() { return recursos.getCombustible(); }
    /**
     * Consulta la energia de la nave.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - El valor devuelto esta entre 0 y 100.
     *
     * @return energia disponible
     */
    public int getEnergia() { return recursos.getEnergia(); }
    /**
     * Consulta el desgaste acumulado de la nave.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - El valor devuelto esta entre 0 y 100.
     *
     * @return desgaste acumulado
     */
    public int getDesgaste() { return recursos.getDesgaste(); }
    /**
     * Consulta si la nave necesita mantenimiento.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     * - Devuelve true si y solo si el desgaste es mayor o igual a 80.
     *
     * @return si la nave requiere mantenimiento
     */
    public boolean requiereMantenimiento() { return recursos.requiereMantenimiento(); }

    /**
     * Carga combustible delegando en los recursos de la nave.
     *
     * Precondiciones:
     * - cantidad es mayor que 0 y no supera la capacidad restante (100 - combustible).
     *
     * Postcondiciones:
     * - El combustible aumenta exactamente en cantidad y queda entre 0 y 100.
     * - Si la carga se rechaza, ningun recurso se modifica.
     *
     * @param cantidad cantidad de combustible a cargar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad maxima de 100
     */
    public void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException {
        recursos.cargarCombustible(cantidad);
    }

    /**
     * Carga energia delegando en los recursos de la nave.
     *
     * Precondiciones:
     * - cantidad es mayor que 0 y no supera la capacidad restante (100 - energia).
     *
     * Postcondiciones:
     * - La energia aumenta exactamente en cantidad y queda entre 0 y 100.
     * - Si la carga se rechaza, ningun recurso se modifica.
     *
     * @param cantidad cantidad de energia a cargar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws LimiteRecursoExcedidoException si la carga supera la capacidad maxima de 100
     */
    public void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException {
        recursos.cargarEnergia(cantidad);
    }

    /**
     * Realiza el mantenimiento de la nave.
     *
     * Postcondiciones:
     * - El desgaste queda en 0 y la nave deja de requerir mantenimiento.
     * - El combustible y la energia no se modifican.
     */
    public void realizarMantenimiento() {
        recursos.realizarMantenimiento();
    }

    /**
     * Comprueba si la nave dispone de los recursos que una operacion va a necesitar,
     * sin modificarlos.
     *
     * Precondiciones:
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Ningun recurso se modifica, ni cuando la verificacion resulta exitosa.
     * - Si no lanza excepcion, un consumo con esas mismas cantidades es aplicable.
     *
     * @param combustibleNecesario combustible que la operacion va a consumir
     * @param energiaNecesaria energia que la operacion va a consumir
     * @param desgasteAgregado desgaste que la operacion va a generar
     * @throws IllegalArgumentException si alguna cantidad es negativa
     * @throws RecursoInsuficienteException si falta combustible o energia
     * @throws LimiteRecursoExcedidoException si el desgaste superaria el limite de 100
     */
    public void verificarDisponibilidad(int combustibleNecesario, int energiaNecesaria, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        recursos.verificarDisponibilidad(combustibleNecesario, energiaNecesaria, desgasteAgregado);
    }

    /**
     * Aplica el consumo de una operacion sobre los recursos de la nave.
     *
     * Precondiciones:
     * - Las tres cantidades son mayores o iguales a 0.
     * - La nave dispone de esos recursos.
     *
     * Postcondiciones:
     * - El combustible y la energia bajan y el desgaste sube exactamente en las
     *   cantidades indicadas, y los tres quedan entre 0 y 100.
     * - Si se rechaza, ningun recurso se modifica: el consumo es atomico.
     *
     * @param combustibleConsumido combustible a descontar
     * @param energiaConsumida energia a descontar
     * @param desgasteAgregado desgaste a sumar
     * @throws IllegalArgumentException si alguna cantidad es negativa
     * @throws RecursoInsuficienteException si falta combustible o energia
     * @throws LimiteRecursoExcedidoException si el desgaste superaria el limite de 100
     */
    public void consumirParaMision(int combustibleConsumido, int energiaConsumida, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        recursos.consumirParaMision(combustibleConsumido, energiaConsumida, desgasteAgregado);
    }

    /**
     * Descuenta energia de la nave.
     *
     * Precondiciones:
     * - cantidad es mayor que 0 y no supera la energia disponible.
     *
     * Postcondiciones:
     * - La energia baja exactamente en cantidad y queda entre 0 y 100.
     * - Si se rechaza, la energia permanece sin cambios.
     *
     * @param cantidad energia a descontar
     * @throws IllegalArgumentException si cantidad es menor o igual a 0
     * @throws RecursoInsuficienteException si la nave no tiene esa energia disponible
     */
    public void consumirEnergia(int cantidad) throws RecursoInsuficienteException {
        recursos.consumirEnergia(cantidad);
    }
    
    // ---------- Motor Warp: la nave delega en su MotorWarp ----------

    /**
     * Ordena al Motor Warp preparar un salto.
     *
     * Precondiciones:
     * - El motor esta en estado Disponible.
     *
     * Postcondiciones:
     * - El motor queda en estado Preparando salto.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no esta en estado Disponible
     */
    public void prepararSalto() throws TransicionInvalidaException {
        motorWarp.prepararSalto();
    }

    /**
     * Ordena al Motor Warp iniciar el salto.
     *
     * Precondiciones:
     * - El motor esta en estado Preparando salto.
     *
     * Postcondiciones:
     * - El motor queda en estado En warp.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no esta en estado Preparando salto
     */
    public void iniciarSalto() throws TransicionInvalidaException {
        motorWarp.iniciarSalto();
    }

    /**
     * Ordena al Motor Warp finalizar el salto.
     *
     * Precondiciones:
     * - El motor esta en estado En warp.
     *
     * Postcondiciones:
     * - El motor queda en estado Enfriamiento.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no esta en estado En warp
     */
    public void finalizarSalto() throws TransicionInvalidaException {
        motorWarp.finalizarSalto();
    }

    /**
     * Ordena al Motor Warp completar el enfriamiento.
     *
     * Precondiciones:
     * - El motor esta en estado Enfriamiento.
     *
     * Postcondiciones:
     * - El motor queda en estado Disponible.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException si el motor no esta en estado Enfriamiento
     */
    public void completarEnfriamiento() throws TransicionInvalidaException {
        motorWarp.completarEnfriamiento();
    }

    /**
     * Consulta en que estado esta el Motor Warp.
     *
     * Postcondiciones:
     * - El motor no se modifica.
     * - Devuelve uno de: Disponible, Preparando salto, En warp, Enfriamiento.
     *
     * @return nombre del estado actual del motor
     */
    public String getEstadoMotor() {
        return motorWarp.getEstadoActual();
    }

    /**
     * Consulta si el Motor Warp puede iniciar una nueva operacion.
     *
     * Postcondiciones:
     * - El motor no se modifica.
     * - Devuelve true si y solo si el motor esta en estado Disponible.
     *
     * @return si el motor esta disponible
     */
    public boolean motorDisponible() {
        return motorWarp.estaDisponible();
    }
}
