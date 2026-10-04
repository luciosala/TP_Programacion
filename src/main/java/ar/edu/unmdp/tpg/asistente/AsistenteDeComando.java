package ar.edu.unmdp.tpg.asistente;

import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.haberes.LiquidacionHaberes;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.naves.Nave;
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
 */
public interface AsistenteDeComando {

    String idNave();


    void asignarTripulante(Tripulante tripulante);
    void validarTripulacionMinima() throws TripulacionInvalidaException ;
    void cargarCombustible(int cantidad) throws LimiteRecursoExcedidoException;
    void cargarEnergia(int cantidad) throws LimiteRecursoExcedidoException;
    void realizarMantenimiento();
    void verificarRecursos(int combustibleNecesario, int energiaNecesaria, int desgasteAgregado) 
        throws RecursoInsuficienteException, LimiteRecursoExcedidoException;
    void consumirParaMision(int combustibleConsumido, int energiaConsumida, int desgasteAgregado)
        throws RecursoInsuficienteException, LimiteRecursoExcedidoException;
    void consumirEnergia(int cantidad) throws RecursoInsuficienteException;
    void prepararSalto() throws TransicionInvalidaException;
    void iniciarSalto() throws TransicionInvalidaException;
    void finalizarSalto() throws TransicionInvalidaException;
    void completarEnfriamiento() throws TransicionInvalidaException;
    String estadoDelMotor();
    boolean motorDisponible(); 
    InformeMision ejecutarMision(Mision mision) throws NaveException;
    InformeMision crearInforme(String mision, String descripcion, String objetivo,List<String> acciones,
                                            int combustibleConsumido, int energiaConsumida, int desgasteGenerado,boolean exito);
    boolean naveEstaOperativa();
    int combustibleDisponible();
    int energiaDisponible();
    int desgasteActual();
    boolean requiereMantenimiento();
    int cantidadTripulantes();
    Haber liquidarHaberes(YearMonth periodo, Tripulante tripulante);
    void registrarEvento(String tipo, String descripcion);
    List<EventoBitacora> consultarBitacora();
}

