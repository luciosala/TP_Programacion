package ar.edu.unmdp.tpg.naves;

import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.motorwarp.MotorWarp;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.excepciones.RecursoInsuficienteException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.elementos.ElementoDelUniverso;


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
 * Contratos:
 * - Las precondiciones son obligacion del invocante y se comprueban con assert
 *   (se activan con java -ea).
 * - Las situaciones del dominio que el programa debe gestionar (tripulante
 *   repetido, falta de recursos, transicion invalida del motor) se informan con
 *   excepciones del paquete excepciones.
 *
 * Invariantes (metodo invariante()):
 * - El id y el tipo no son nulos ni vacios, y no cambian durante la vida de la nave.
 * - Los recursos y el motor no son nulos (sus rangos los garantiza Recursos).
 * - La tripulacion no contiene nulos ni dos tripulantes con el mismo id.
 * - La cantidad de gemas no es negativa.
 */
abstract public class Nave implements ElementoDelUniverso {
    private final String id;
    private final String tipo;
    private final Recursos recursos;
    private final MotorWarp motorWarp;
    /** Cantidad minima de tripulantes (un capitan y cuatro mas). */
    public static final int DOTACION_MINIMA = 5;

    private final List<Tripulante> tripulacion = new ArrayList<>();
    private boolean destruida = false;
    private int gemas = 0;

    /**
     * Construye una nave con identidad, tipo, recursos y motor Warp.
     *
     * Precondiciones (assert):
     * - id y tipo no son nulos, vacíos ni contienen solo espacios.
     * - recursos y motorWarp no son nulos.
     *
     * Postcondiciones:
     * - La nave conserva el id y el tipo recibidos.
     * - La nave utiliza los recursos y el motorWarp recibidos.
     * - La tripulación comienza vacía.
     * - Se cumple el invariante.
     *
     * @param id identificador de la nave
     * @param tipo tipo de nave
     * @param recursos recursos que utilizará la nave
     * @param motorWarp motor Warp que utilizará la nave
     */
    protected Nave(String id, String tipo, Recursos recursos, MotorWarp motorWarp) {
        assert id != null && !id.isBlank() : "El id no puede ser nulo ni vacio";
        assert tipo != null && !tipo.isBlank() : "El tipo no puede ser nulo ni vacio";
        assert recursos != null : "Los recursos no pueden ser nulos";
        assert motorWarp != null : "El motor no puede ser nulo";

        this.id = id;
        this.tipo = tipo;
        this.recursos = recursos;
        this.motorWarp = motorWarp;

        assert tripulacion.isEmpty() : "La tripulación debe comenzar vacía";
        assert invariante() : "Fallo invariante de Nave al construir";
    }

    /**
     * Invariante de clase de la nave.
     *
     * @return true si la nave esta en un estado valido
     */
    private boolean invariante() {
        if (id == null || id.isBlank() || tipo == null || tipo.isBlank()) {
            return false;
        }
        if (recursos == null || motorWarp == null || gemas < 0) {
            return false;
        }
        for (int i = 0; i < tripulacion.size(); i++) {
            Tripulante tripulante = tripulacion.get(i);
            if (tripulante == null) {
                return false;
            }
            for (int j = i + 1; j < tripulacion.size(); j++) {
                if (tripulante.getId().equals(tripulacion.get(j).getId())) {
                    return false;
                }
            }
        }
        return true;
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
     * Precondiciones (assert):
     * - t no es nulo.
     *
     * Postcondiciones:
     * - La cantidad de tripulantes aumenta exactamente en uno.
     * - El tripulante queda consultable por su id.
     * - Si se rechaza, la tripulacion permanece sin cambios.
     *
     * @param t tripulante a incorporar
     * @throws TripulacionInvalidaException rechazo de dominio: ya hay un
     *         tripulante con ese id en la nave
     */
    public void agregarTripulante(Tripulante t) throws TripulacionInvalidaException {
        assert t != null : "El tripulante no puede ser nulo";

        if (buscarTripulantePorId(t.getId()) != null) {
            throw new TripulacionInvalidaException(
                "Ya existe un tripulante con id " + t.getId() + " en la nave " + id);
        }

        int cantidadAnterior = cantidadTripulantes();
        tripulacion.add(t);

        assert cantidadTripulantes() == cantidadAnterior + 1 : "Fallo postcondición de agregarTripulante";
        assert buscarTripulantePorId(t.getId()) == t : "El tripulante agregado debe poder consultarse";
        assert invariante() : "Fallo invariante de Nave";
    }

    /**
     * Busca un tripulante de la nave por su id.
     *
     * Precondiciones (assert):
     * - id no es nulo, vacio ni contiene solo espacios.
     *
     * Postcondiciones:
     * - La tripulacion no se modifica.
     * - Devuelve el tripulante con ese id, o null si no esta en la nave.
     *
     * @param id id del tripulante buscado
     * @return el tripulante encontrado, o null si no esta en la nave
     */
    public Tripulante buscarTripulantePorId(String id) {
        assert id != null && !id.isBlank() : "El id no puede ser nulo ni vacío";

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

    /**
     * Unica operacion que determina si la tripulacion esta lista para operar:
     * un capitan y al menos cuatro tripulantes adicionales.
     *
     * La regla de dotacion minima vive solo aca. El asistente, la mision y
     * estaOperativa() consultan este metodo en lugar de repetir la regla.
     *
     * Postcondiciones:
     * - La tripulacion no se modifica.
     * - Devuelve true si y solo si hay 5 tripulantes o mas y al menos uno tiene cargo Capitan.
     *
     * @return si la tripulacion esta lista
     */
    public boolean tripulacionLista() {
        if (cantidadTripulantes() < DOTACION_MINIMA) {
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
     * Indica si la nave puede operar: no fue destruida y su tripulacion esta
     * lista. Los rangos de los recursos no se vuelven a preguntar porque son
     * invariante de Recursos.
     *
     * Postcondiciones:
     * - La nave no se modifica.
     *
     * @return {@code true} si la nave no esta destruida y tripulacionLista() es true
     */
    public boolean estaOperativa() {
        return !destruida && tripulacionLista();
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
     * Consulta si la nave fue destruida.
     *
     * @return true si la nave fue destruida
     */
    public boolean estaDestruida() {
        return destruida;
    }

    // protected: solo la propia nave (y su paquete) puede destruirse.
    // La usa la batalla del double dispatch.
    protected void destruir() {
        destruida = true;
    }

    /**
     * Recibe el golpe de un asteroide y se fija si la nave quedo destruida.
     *
     * Precondiciones (assert):
     * - danioEnergia es mayor que 0.
     *
     * Postcondiciones:
     * - La energia baja en danioEnergia sin quedar negativa.
     * - Si la nave queda en nivel letal, queda destruida.
     *
     * @param danioEnergia energia que se pierde por el impacto
     */
    public void recibirImpacto(int danioEnergia) {
        assert danioEnergia > 0 : "El daño debe ser positivo";
        recursos.perderEnergia(danioEnergia);
        controlarDestruccion();
        assert !recursos.enNivelLetal() || destruida : "Una nave en nivel letal debe quedar destruida";
    }

    /**
     * Recarga en una estacion de carga, topeando en la capacidad maxima.
     *
     * Precondiciones (assert):
     * - Las dos cantidades son mayores o iguales a 0.
     *
     * @param combustibleExtra combustible a sumar
     * @param energiaExtra energia a sumar
     */
    public void recargar(int combustibleExtra, int energiaExtra) {
        assert combustibleExtra >= 0 && energiaExtra >= 0 : "Las cantidades a recargar no pueden ser negativas";
        recursos.recargar(combustibleExtra, energiaExtra);
    }

    // Se llama después de todo lo que resta recursos.
    private void controlarDestruccion() {
        if (recursos.enNivelLetal()) {
            destruir();
        }
    }

    /**
     * Carga combustible delegando en los recursos de la nave.
     *
     * Precondiciones (assert, en Recursos):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - El combustible aumenta exactamente en cantidad y queda entre 0 y 100.
     * - Si la carga se rechaza, ningun recurso se modifica.
     *
     * @param cantidad cantidad de combustible a cargar
     * @throws LimiteRecursoExcedidoException rechazo de dominio: la carga supera la capacidad maxima de 100
     */
    public void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException {
        recursos.cargarCombustible(cantidad);
    }

    /**
     * Carga energia delegando en los recursos de la nave.
     *
     * Precondiciones (assert, en Recursos):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - La energia aumenta exactamente en cantidad y queda entre 0 y 100.
     * - Si la carga se rechaza, ningun recurso se modifica.
     *
     * @param cantidad cantidad de energia a cargar
     * @throws LimiteRecursoExcedidoException rechazo de dominio: la carga supera la capacidad maxima de 100
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
     * Consulta las gemas recolectadas por la nave.
     *
     * @return cantidad de gemas, mayor o igual a 0
     */
    public int getGemas() {
        return gemas;
    }

    /**
     * Suma gemas recolectadas en una mision.
     *
     * Precondiciones (assert):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - Las gemas aumentan exactamente en cantidad.
     *
     * @param cantidad gemas recolectadas
     */
    public void agregarGemas(int cantidad) {
        assert cantidad > 0 : "La cantidad de gemas debe ser positiva";
        int gemasAnteriores = gemas;
        gemas += cantidad;
        assert gemas == gemasAnteriores + cantidad : "Fallo postcondición de agregarGemas";
        assert invariante() : "Fallo invariante de Nave";
    }

    /**
     * Comprueba si la nave dispone de los recursos que una operacion va a necesitar,
     * sin modificarlos.
     *
     * Precondiciones (assert, en Recursos):
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - Ningun recurso se modifica, ni cuando la verificacion resulta exitosa.
     * - Si no lanza excepcion, un consumo con esas mismas cantidades es aplicable.
     *
     * @param combustibleNecesario combustible que la operacion va a consumir
     * @param energiaNecesaria energia que la operacion va a consumir
     * @param desgasteAgregado desgaste que la operacion va a generar
     * @throws RecursoInsuficienteException rechazo de dominio: falta combustible o energia
     * @throws LimiteRecursoExcedidoException rechazo de dominio: el desgaste superaria el limite de 100
     */
    public void verificarDisponibilidad(int combustibleNecesario, int energiaNecesaria, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        recursos.verificarDisponibilidad(combustibleNecesario, energiaNecesaria, desgasteAgregado);
    }

    /**
     * Aplica el consumo de una operacion sobre los recursos de la nave.
     *
     * Precondiciones (assert, en Recursos):
     * - Las tres cantidades son mayores o iguales a 0.
     *
     * Postcondiciones:
     * - El combustible y la energia bajan y el desgaste sube exactamente en las
     *   cantidades indicadas, y los tres quedan entre 0 y 100.
     * - Si se rechaza, ningun recurso se modifica: el consumo es atomico.
     *
     * @param combustibleConsumido combustible a descontar
     * @param energiaConsumida energia a descontar
     * @param desgasteAgregado desgaste a sumar
     * @throws RecursoInsuficienteException rechazo de dominio: falta combustible o energia
     * @throws LimiteRecursoExcedidoException rechazo de dominio: el desgaste superaria el limite de 100
     */
    public void consumirParaMision(int combustibleConsumido, int energiaConsumida, int desgasteAgregado)
            throws RecursoInsuficienteException, LimiteRecursoExcedidoException {
        recursos.consumirParaMision(combustibleConsumido, energiaConsumida, desgasteAgregado);
        controlarDestruccion();
    }

    /**
     * Descuenta energia de la nave.
     *
     * Precondiciones (assert, en Recursos):
     * - cantidad es mayor que 0.
     *
     * Postcondiciones:
     * - La energia baja exactamente en cantidad y queda entre 0 y 100.
     * - Si se rechaza, la energia permanece sin cambios.
     *
     * @param cantidad energia a descontar
     * @throws RecursoInsuficienteException rechazo de dominio: la nave no tiene esa energia disponible
     */
    public void consumirEnergia(int cantidad) throws RecursoInsuficienteException {
        recursos.consumirEnergia(cantidad);
        controlarDestruccion();
    }
    
    // ---------- Motor Warp: la nave delega en su MotorWarp ----------

    /**
     * Ordena al Motor Warp preparar un salto.
     *
     * Postcondiciones:
     * - El motor queda en estado Preparando salto.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no esta en estado Disponible
     */
    public void prepararSalto() throws TransicionInvalidaException {
        motorWarp.prepararSalto();
    }

    /**
     * Ordena al Motor Warp iniciar el salto.
     *
     * Postcondiciones:
     * - El motor queda en estado En warp.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no esta en estado Preparando salto
     */
    public void iniciarSalto() throws TransicionInvalidaException {
        motorWarp.iniciarSalto();
    }

    /**
     * Ordena al Motor Warp finalizar el salto.
     *
     * Postcondiciones:
     * - El motor queda en estado Enfriamiento.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no esta en estado En warp
     */
    public void finalizarSalto() throws TransicionInvalidaException {
        motorWarp.finalizarSalto();
    }

    /**
     * Ordena al Motor Warp completar el enfriamiento.
     *
     * Postcondiciones:
     * - El motor queda en estado Disponible.
     * - Si la transicion se rechaza, el estado del motor permanece sin cambios.
     *
     * @throws TransicionInvalidaException rechazo de dominio: el motor no esta en estado Enfriamiento
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
    /**
     * Paso 1 del double dispatch: le paso el desafio a la enemiga.
     *
     * Precondiciones (assert):
     * - enemiga no es nula.
     *
     * @param enemiga nave contra la que se enfrenta esta
     * @return resultado desde el punto de vista de esta nave
     */
    public final ResultadoBatalla enfrentar(Nave enemiga) {
        assert enemiga != null : "La nave enemiga no puede ser nula";
        return enemiga.recibirDesafioDe(this);
    }

    // Paso 2: cada subclase lo implementa llamando al contraXxx de SU tipo
    protected abstract ResultadoBatalla recibirDesafioDe(Nave retadora);

    // La fila de la tabla: cómo le va a ESTA nave contra cada tipo
    protected abstract ResultadoBatalla contraExploradora();
    protected abstract ResultadoBatalla contraCarguera();
    protected abstract ResultadoBatalla contraCombate();
    
    //Punto 3: encuentro en el tablero
    @Override
    public String interactuarCon(Nave naveJugador) {
        assert naveJugador != null : "La nave no puede ser nula";
        if (estaDestruida()) {
            return "Restos de " + this + ": sin efecto";
        }
        String encuentro = "Encuentro con " + this + ": ";
        switch (naveJugador.enfrentar(this)) {
            case GANA:
                destruir();                 // muere la enemiga
                return encuentro + "batalla ganada, la nave enemiga fue destruida";
            case PIERDE:
                naveJugador.destruir();     // muere la mia
                return encuentro + "batalla perdida, la nave y su tripulación fueron destruidas";
            default:
                return encuentro + "pacto de paz";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + id;   
    }
    
}
