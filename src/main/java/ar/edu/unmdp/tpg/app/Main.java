package ar.edu.unmdp.tpg.app;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComandoBasico;
import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.naves.Nave;
import ar.edu.unmdp.tpg.naves.NaveFactory;
import ar.edu.unmdp.tpg.tripulacion.Alferez;
import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.tripulacion.Consejero;
import ar.edu.unmdp.tpg.tripulacion.Origen;
import ar.edu.unmdp.tpg.tripulacion.Teniente;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.universo.Universo;

import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.mision.MisionIntercepcion;
import ar.edu.unmdp.tpg.mision.MisionRecoleccion;
import ar.edu.unmdp.tpg.mision.MisionRetornoSeguro;

import java.util.List;

/**
 * Programa de demostracion: simula al usuario del sistema (R6).
 *
 * Es la unica clase que usa la consola. El modelo no sabe que existe:
 * en la Entrega 2 se reemplaza por pantallas sin cambiar ninguna clase del modelo.
 */
public class Main {

    public static void main(String[] args) {
        Universo universo = crearUniverso();

        titulo("Naves registradas en el universo");
        mostrarEstado(universo.obtener("EXP-1"));
        mostrarEstado(universo.obtener("CAR-1"));
        mostrarEstado(universo.obtener("COM-1"));
    }
        // ---------- Escenario A: ejecucion correcta ----------

    private static void escenarioA(Universo universo) {
        titulo("ESCENARIO A - Ejecucion correcta de M-01, M-02 y M-03");

        universo.seleccionarNave("EXP-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave());
        mostrarEstado(nave);

        ejecutarYMostrar(nave, new MisionIntercepcion(nave, "Carguero Orion", "sin propulsion"));
        ejecutarYMostrar(nave, new MisionRecoleccion(nave, "Nebulosa K-7", "muestra de gas"));
        ejecutarYMostrar(nave, new MisionRetornoSeguro(nave, "Base Andromeda"));
    }

    private static void ejecutarYMostrar(AsistenteDeComando nave, Mision mision) {
        int eventosAntes = nave.consultarBitacora().size();
        try {
            InformeMision informe = nave.ejecutarMision(mision);
            System.out.println();
            System.out.println(informe);
        } catch (NaveException error) {
            System.out.println("Mision rechazada: " + error.getMessage());
        }
        mostrarEstado(nave);
        mostrarBitacoraDesde(nave, eventosAntes);
    }

    // ---------- Escenario B: recursos insuficientes ----------

    private static void escenarioB(Universo universo) {
        titulo("ESCENARIO B - Recursos insuficientes");

        universo.seleccionarNave("CAR-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave());
        mostrarEstado(nave);

        // Se encomiendan recolecciones hasta que la nave ya no pueda hacer otra.
        int realizadas = 0;
        boolean rechazada = false;

        while (!rechazada) {
            int combustibleAntes = nave.combustibleDisponible();
            int energiaAntes = nave.energiaDisponible();
            int desgasteAntes = nave.desgasteActual();
            int eventosAntes = nave.consultarBitacora().size();

            try {
                nave.ejecutarMision(new MisionRecoleccion(nave, "Asteroide " + (realizadas + 1), "mineral"));
                realizadas++;
            } catch (NaveException error) {
                rechazada = true;

                System.out.println("Recolecciones completadas antes del rechazo: " + realizadas);
                System.out.println("Mision rechazada: " + error.getMessage());
                mostrarEstado(nave);
                mostrarBitacoraDesde(nave, eventosAntes);

                boolean sinCambios = nave.combustibleDisponible() == combustibleAntes
                    && nave.energiaDisponible() == energiaAntes
                    && nave.desgasteActual() == desgasteAntes;
                System.out.println("Recursos sin cambios parciales: " + (sinCambios ? "SI" : "NO"));
            }
        }
    }
    
    // ---------- Escenario C: Motor Warp ----------

    private static void escenarioC(Universo universo) {
        titulo("ESCENARIO C - Motor Warp");

        universo.seleccionarNave("COM-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave());
        int eventosAntes = nave.consultarBitacora().size();

        // 1. Secuencia valida completa
        System.out.println("Secuencia valida:");
        System.out.println("  estado inicial        -> " + nave.estadoDelMotor());
        try {
            nave.prepararSalto();
            System.out.println("  prepararSalto         -> " + nave.estadoDelMotor());
            nave.iniciarSalto();
            System.out.println("  iniciarSalto          -> " + nave.estadoDelMotor());
            nave.finalizarSalto();
            System.out.println("  finalizarSalto        -> " + nave.estadoDelMotor());
            nave.completarEnfriamiento();
            System.out.println("  completarEnfriamiento -> " + nave.estadoDelMotor());
        } catch (TransicionInvalidaException error) {
            System.out.println("  Rechazo inesperado: " + error.getMessage());
        }

        // 2. Transiciones invalidas: se rechazan y el estado no cambia
        System.out.println("Transiciones invalidas:");

        String estadoAntes = nave.estadoDelMotor();
        try {
            nave.iniciarSalto();
            System.out.println("  ERROR: se acepto iniciar el salto sin prepararlo");
        } catch (TransicionInvalidaException error) {
            System.out.println("  iniciarSalto desde " + estadoAntes + " -> rechazada: " + error.getMessage());
            System.out.println("  estado despues del rechazo: " + nave.estadoDelMotor());
        }

        estadoAntes = nave.estadoDelMotor();
        try {
            nave.finalizarSalto();
            System.out.println("  ERROR: se acepto finalizar un salto que no ocurrio");
        } catch (TransicionInvalidaException error) {
            System.out.println("  finalizarSalto desde " + estadoAntes + " -> rechazada: " + error.getMessage());
            System.out.println("  estado despues del rechazo: " + nave.estadoDelMotor());
        }

        mostrarBitacoraDesde(nave, eventosAntes);
    }

    // ---------- Escenario D: contrato invalido y mantenimiento ----------

    private static void escenarioD(Universo universo) {
        titulo("ESCENARIO D - Contrato invalido y mantenimiento");

        universo.seleccionarNave("CAR-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave() + " (quedo sin energia en el escenario B)");
        mostrarEstado(nave);
        int eventosAntes = nave.consultarBitacora().size();

        // 1. Cargas validas
        System.out.println("Cargas validas:");
        try {
            nave.cargarEnergia(60);
            System.out.println("  cargarEnergia(60)     -> energia " + nave.energiaDisponible());
            nave.cargarCombustible(40);
            System.out.println("  cargarCombustible(40) -> combustible " + nave.combustibleDisponible());
        } catch (LimiteRecursoExcedidoException error) {
            System.out.println("  Rechazo inesperado: " + error.getMessage());
        }

        // 2. Carga que excede la capacidad maxima: se rechaza y el recurso no cambia
        System.out.println("Carga invalida:");
        int combustibleAntes = nave.combustibleDisponible();
        try {
            nave.cargarCombustible(20);
            System.out.println("  ERROR: se acepto una carga por encima de la capacidad maxima");
        } catch (LimiteRecursoExcedidoException error) {
            System.out.println("  cargarCombustible(20) con " + combustibleAntes + " -> rechazada: " + error.getMessage());
            boolean sinCambios = nave.combustibleDisponible() == combustibleAntes;
            System.out.println("  combustible despues del rechazo: " + nave.combustibleDisponible()
                + (sinCambios ? " (sin cambios)" : " (CAMBIO: contrato violado)"));
        }

        // 3. Mantenimiento: el desgaste vuelve a 0
        System.out.println("Mantenimiento:");
        System.out.println("  desgaste antes:   " + nave.desgasteActual());
        nave.realizarMantenimiento();
        System.out.println("  desgaste despues: " + nave.desgasteActual());

        mostrarEstado(nave);
        mostrarBitacoraDesde(nave, eventosAntes);
    }

    // ---------- Armado del universo ----------

    private static Universo crearUniverso() {
        Universo universo = new Universo();
        registrarNave(universo, "EXP-1", "exploradora");
        registrarNave(universo, "CAR-1", "carguero");
        registrarNave(universo, "COM-1", "combate");
        return universo;
    }

    private static void registrarNave(Universo universo, String id, String tipo) {
        Nave nave = NaveFactory.createNave(id, tipo);
        AsistenteDeComando asistente = new AsistenteDeComandoBasico(nave, new Bitacora());
        asignarTripulacion(asistente, id);
        universo.registrar(asistente);
    }

    // Capitan + 4 tripulantes, cubriendo los 4 cargos y los 3 origenes.
    private static void asignarTripulacion(AsistenteDeComando asistente, String idNave) {
        asistente.asignarTripulante(new Tripulante(idNave + "-T1", "Ana", "Rojas", new Capitan(), Origen.TERRICOLA, 10));
        asistente.asignarTripulante(new Tripulante(idNave + "-T2", "Spok", "Sarek", new Consejero(), Origen.VULCANO, 5));
        asistente.asignarTripulante(new Tripulante(idNave + "-T3", "Leo", "Marte", new Teniente(), Origen.MARCIANO, 3));
        asistente.asignarTripulante(new Tripulante(idNave + "-T4", "Sol", "Diaz", new Alferez(), Origen.TERRICOLA, 1));
        asistente.asignarTripulante(new Tripulante(idNave + "-T5", "Tom", "Vala", new Alferez(), Origen.VULCANO, 0));
    }

    // ---------- Salida por consola ----------

    private static void mostrarEstado(AsistenteDeComando asistente) {
        System.out.println(asistente.idNave()
            + " | combustible " + asistente.combustibleDisponible()
            + " | energia " + asistente.energiaDisponible()
            + " | desgaste " + asistente.desgasteActual()
            + " | motor " + asistente.estadoDelMotor()
            + " | tripulantes " + asistente.cantidadTripulantes());
    }
    // Muestra solo los eventos registrados a partir de la posicion indicada.     ← NUEVO
    private static void mostrarBitacoraDesde(AsistenteDeComando nave, int desde) {
        List<EventoBitacora> eventos = nave.consultarBitacora();
        System.out.println("Bitacora:");
        for (int i = desde; i < eventos.size(); i++) {
            EventoBitacora evento = eventos.get(i);
            System.out.println("  [" + evento.getTimestamp() + "] " + evento.getCategoria() + " | " + evento.getDescripcion());
        }
    } 

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }
    public static void main(String[] args) {
        Universo universo = crearUniverso();

        titulo("Naves registradas en el universo");
        mostrarEstado(universo.obtener("EXP-1"));
        mostrarEstado(universo.obtener("CAR-1"));
        mostrarEstado(universo.obtener("COM-1"));

        escenarioA(universo);
        escenarioB(universo);
        escenarioC(universo);
        escenarioD(universo);
    }
}