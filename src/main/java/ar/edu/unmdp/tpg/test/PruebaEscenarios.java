package ar.edu.unmdp.tpg.test;

import ar.edu.unmdp.tpg.asistente.AsistenteDeComando;
import ar.edu.unmdp.tpg.asistente.AsistenteDeComandoBasico;
import ar.edu.unmdp.tpg.bitacora.Bitacora;
import ar.edu.unmdp.tpg.bitacora.EventoBitacora;
import ar.edu.unmdp.tpg.destino.CantidadDeElementosSuperaCasillaException;
import ar.edu.unmdp.tpg.destino.DimensionesDeTableroInconsistentesException;
import ar.edu.unmdp.tpg.destino.Tablero;
import ar.edu.unmdp.tpg.excepciones.LimiteRecursoExcedidoException;
import ar.edu.unmdp.tpg.excepciones.MisionNoViableException;
import ar.edu.unmdp.tpg.excepciones.NaveException;
import ar.edu.unmdp.tpg.excepciones.NaveNoRegistradaException;
import ar.edu.unmdp.tpg.excepciones.NaveYaRegistradaException;
import ar.edu.unmdp.tpg.excepciones.TransicionInvalidaException;
import ar.edu.unmdp.tpg.excepciones.TripulacionInvalidaException;
import ar.edu.unmdp.tpg.haberes.Haber;
import ar.edu.unmdp.tpg.mision.InformeMision;
import ar.edu.unmdp.tpg.mision.Mision;
import ar.edu.unmdp.tpg.mision.MisionIntercepcion;
import ar.edu.unmdp.tpg.mision.MisionRecoleccion;
import ar.edu.unmdp.tpg.mision.MisionRetornoSeguro;
import ar.edu.unmdp.tpg.naves.Nave;
import ar.edu.unmdp.tpg.naves.NaveFactory;
import ar.edu.unmdp.tpg.naves.Recursos;
import ar.edu.unmdp.tpg.tripulacion.Alferez;
import ar.edu.unmdp.tpg.tripulacion.Capitan;
import ar.edu.unmdp.tpg.tripulacion.Consejero;
import ar.edu.unmdp.tpg.tripulacion.Origen;
import ar.edu.unmdp.tpg.tripulacion.Teniente;
import ar.edu.unmdp.tpg.tripulacion.Tripulante;
import ar.edu.unmdp.tpg.universo.Universo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Main de prueba de la Entrega 1: recorre los escenarios de la ficha de inicio
 * (A, B, C y D), un escenario de contratos (E) y la liquidacion de haberes, y
 * compara cada resultado con el esperado usando el Verificador.
 *
 * Para comprobar un rechazo se usa el esquema try/catch de la teoria de
 * Excepciones: la operacion va en el try y, si no se rechaza, se registra la
 * falla; el rechazo esperado se atrapa en el catch.
 *
 * Debe ejecutarse con las aserciones activadas (java -ea), porque las
 * precondiciones del modelo se comprueban con assert. Termina con codigo 0 si
 * todo coincide con lo esperado, 1 si alguna comprobacion falla y 2 si faltan
 * las aserciones.
 */
public class PruebaEscenarios {

    private static final Verificador verificador = new Verificador();

    // ---------- Creacion: Factory y tripulacion ----------

    private static Universo crearUniverso() throws NaveException {
        titulo("CREACION - Tres naves mediante NaveFactory");
        Universo universo = new Universo();
        registrarNave(universo, "EXP-1", "exploradora");
        registrarNave(universo, "CAR-1", "carguero");
        registrarNave(universo, "COM-1", "combate");

        mostrarEstado(universo.obtener("EXP-1"));
        mostrarEstado(universo.obtener("CAR-1"));
        mostrarEstado(universo.obtener("COM-1"));

        verificador.verificarIgual("Naves registradas en el universo", 3, universo.cantidadNaves());
        verificarNaveInicial(universo.obtener("EXP-1"), 60, 80);
        verificarNaveInicial(universo.obtener("CAR-1"), 100, 60);
        verificarNaveInicial(universo.obtener("COM-1"), 80, 100);
        return universo;
    }

    private static void verificarNaveInicial(AsistenteDeComando nave, int combustible, int energia) {
        verificador.verificarRecursos(nave.idNave() + ": recursos iniciales de la ficha",
            combustible, energia, 0,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        verificador.verificarIgual(nave.idNave() + ": motor inicial", "Disponible", nave.estadoDelMotor());
        verificador.verificarIgual(nave.idNave() + ": tripulantes", 5, nave.cantidadTripulantes());
        verificador.verificar(nave.idNave() + ": tripulación lista (capitán + 4)", nave.tripulacionLista());
    }

    // ---------- Escenario A: ejecucion correcta ----------

    private static void escenarioA(Universo universo) throws NaveException {
        titulo("ESCENARIO A - Ejecucion correcta de M-01, M-02 y M-03");
        universo.seleccionarNave("EXP-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave());
        mostrarEstado(nave);
        Tablero tablero = crearTableroVacio();

        // M-01: despegue 4 combustible y 4 desgaste; entrega 5 de energia y 5 de combustible.
        InformeMision m01 = ejecutarYMostrar(nave, new MisionIntercepcion(nave, tablero, "Carguero Orion", "sin propulsion"));
        verificador.verificar("M-01 exitosa", m01.fueExitosa());
        verificador.verificarRecursos("M-01: consumos del informe", 9, 5, 4,
            m01.getCombustibleConsumido(), m01.getEnergiaConsumida(), m01.getDesgasteGenerado());
        verificador.verificarRecursos("M-01: recursos de la nave al cerrar", 51, 75, 4,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        verificador.verificarIgual("M-01: motor tras el salto", "Disponible", nave.estadoDelMotor());

        // M-02: despegue 4 combustible y 4 desgaste; recoleccion 5 de energia; entre 1 y 5 gemas.
        InformeMision m02 = ejecutarYMostrar(nave, new MisionRecoleccion(nave, tablero, "Nebulosa K-7", "muestra de gas"));
        verificador.verificar("M-02 exitosa", m02.fueExitosa());
        verificador.verificarRecursos("M-02: consumos del informe", 4, 5, 4,
            m02.getCombustibleConsumido(), m02.getEnergiaConsumida(), m02.getDesgasteGenerado());
        verificador.verificarRecursos("M-02: recursos de la nave al cerrar", 47, 70, 8,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        verificador.verificar("M-02: gemas recolectadas entre 1 y 5 (obtenido " + nave.gemasDisponibles() + ")",
            nave.gemasDisponibles() >= 1 && nave.gemasDisponibles() <= 5);

        // M-03: despegue 4 combustible y 4 desgaste; al llegar a la base recarga a 100 y repara.
        InformeMision m03 = ejecutarYMostrar(nave, new MisionRetornoSeguro(nave, tablero, "Base Andromeda"));
        verificador.verificar("M-03 exitosa", m03.fueExitosa());
        verificador.verificarRecursos("M-03: consumos del informe", 4, 0, 4,
            m03.getCombustibleConsumido(), m03.getEnergiaConsumida(), m03.getDesgasteGenerado());
        verificador.verificarRecursos("M-03: nave reabastecida y reparada", 100, 100, 0,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());

        // Bitacora: orden temporal y las tres misiones cerradas en el orden en que se ejecutaron.
        List<EventoBitacora> eventos = nave.consultarBitacora();
        verificador.verificar("Bitácora en orden temporal", estaEnOrdenTemporal(eventos));
        int cierreM01 = indiceDesde(eventos, 0, "M-01 - Intercepción y asistencia finalizada: EXITOSA");
        int cierreM02 = indiceDesde(eventos, 0, "M-02 - Recolección finalizada: EXITOSA");
        int cierreM03 = indiceDesde(eventos, 0, "M-03 - Retorno seguro finalizada: EXITOSA");
        verificador.verificar("Bitácora registra el cierre de M-01, M-02 y M-03 en ese orden",
            cierreM01 >= 0 && cierreM01 < cierreM02 && cierreM02 < cierreM03);
        verificador.verificar("Bitácora registra el ciclo del Motor Warp",
            indiceDesde(eventos, 0, "Motor Warp en En warp") >= 0);
    }

    private static InformeMision ejecutarYMostrar(AsistenteDeComando nave, Mision mision) throws NaveException {
        int eventosAntes = nave.consultarBitacora().size();
        InformeMision informe = nave.ejecutarMision(mision);
        System.out.println();
        System.out.println(informe);
        mostrarEstado(nave);
        mostrarBitacoraDesde(nave, eventosAntes);
        return informe;
    }

    // ---------- Escenario B: recursos insuficientes ----------

    private static void escenarioB(Universo universo) throws NaveException {
        titulo("ESCENARIO B - Recursos insuficientes");
        universo.seleccionarNave("CAR-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave());
        mostrarEstado(nave);
        Tablero tablero = crearTableroVacio();

        // Recolecciones sucesivas: cada una gasta 4 combustible, 5 energia y 4 desgaste.
        // La energia (60) alcanza para 11; la 12.a dejaria la nave sin energia y se rechaza.
        int realizadas = 0;
        boolean rechazada = false;
        while (!rechazada && realizadas < 20) {
            int combustibleAntes = nave.combustibleDisponible();
            int energiaAntes = nave.energiaDisponible();
            int desgasteAntes = nave.desgasteActual();
            int eventosAntes = nave.consultarBitacora().size();
            try {
                nave.ejecutarMision(new MisionRecoleccion(nave, tablero, "Asteroide " + (realizadas + 1), "mineral"));
                realizadas++;
            } catch (MisionNoViableException error) {
                rechazada = true;
                System.out.println("Recolecciones completadas antes del rechazo: " + realizadas);
                mostrarEstado(nave);
                mostrarBitacoraDesde(nave, eventosAntes);

                verificador.rechazoEsperado("Misión sin recursos suficientes", error);
                verificador.verificarRecursos("Recursos sin cambios parciales tras el rechazo",
                    combustibleAntes, energiaAntes, desgasteAntes,
                    nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
                List<EventoBitacora> eventos = nave.consultarBitacora();
                verificador.verificar("La bitácora registra el motivo del rechazo",
                    indiceDesde(eventos, eventosAntes, "preparación rechazada") >= 0);
                verificador.verificarIgual("Último evento de la orden rechazada", "ERROR",
                    eventos.get(eventos.size() - 1).getCategoria());
            }
        }
        verificador.verificarIgual("Recolecciones completadas antes del rechazo", 11, realizadas);
        verificador.verificarRecursos("CAR-1 tras el escenario B", 56, 5, 44,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        verificador.verificarIgual("Motor de CAR-1 tras el rechazo", "Disponible", nave.estadoDelMotor());
    }

    // ---------- Escenario C: Motor Warp ----------

    private static void escenarioC(Universo universo) throws NaveException {
        titulo("ESCENARIO C - Motor Warp");
        universo.seleccionarNave("COM-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave());
        int eventosAntes = nave.consultarBitacora().size();

        System.out.println("Secuencia valida:");
        verificador.verificarIgual("Estado inicial", "Disponible", nave.estadoDelMotor());
        nave.prepararSalto();
        verificador.verificarIgual("prepararSalto", "Preparando salto", nave.estadoDelMotor());
        nave.iniciarSalto();
        verificador.verificarIgual("iniciarSalto", "En warp", nave.estadoDelMotor());
        nave.finalizarSalto();
        verificador.verificarIgual("finalizarSalto", "Enfriamiento", nave.estadoDelMotor());
        nave.completarEnfriamiento();
        verificador.verificarIgual("completarEnfriamiento", "Disponible", nave.estadoDelMotor());

        System.out.println("Transiciones invalidas:");
        int erroresAntes = contarCategoria(nave.consultarBitacora(), "ERROR");

        try {
            nave.iniciarSalto();
            verificador.faltoRechazo("iniciarSalto desde Disponible");
        } catch (TransicionInvalidaException error) {
            verificador.rechazoEsperado("iniciarSalto desde Disponible", error);
        }
        verificador.verificarIgual("Estado tras el rechazo", "Disponible", nave.estadoDelMotor());

        try {
            nave.finalizarSalto();
            verificador.faltoRechazo("finalizarSalto desde Disponible");
        } catch (TransicionInvalidaException error) {
            verificador.rechazoEsperado("finalizarSalto desde Disponible", error);
        }
        verificador.verificarIgual("Estado tras el rechazo", "Disponible", nave.estadoDelMotor());

        nave.prepararSalto();
        try {
            nave.prepararSalto();
            verificador.faltoRechazo("prepararSalto desde Preparando salto");
        } catch (TransicionInvalidaException error) {
            verificador.rechazoEsperado("prepararSalto desde Preparando salto", error);
        }
        verificador.verificarIgual("Estado tras el rechazo", "Preparando salto", nave.estadoDelMotor());

        try {
            nave.completarEnfriamiento();
            verificador.faltoRechazo("completarEnfriamiento desde Preparando salto");
        } catch (TransicionInvalidaException error) {
            verificador.rechazoEsperado("completarEnfriamiento desde Preparando salto", error);
        }
        verificador.verificarIgual("Estado tras el rechazo", "Preparando salto", nave.estadoDelMotor());

        // Se completa el ciclo para dejar el motor Disponible.
        nave.iniciarSalto();
        nave.finalizarSalto();
        nave.completarEnfriamiento();
        verificador.verificarIgual("Motor de vuelta en Disponible", "Disponible", nave.estadoDelMotor());

        int erroresDespues = contarCategoria(nave.consultarBitacora(), "ERROR");
        verificador.verificarIgual("Rechazos registrados en la bitácora como ERROR", 4, erroresDespues - erroresAntes);
        mostrarBitacoraDesde(nave, eventosAntes);
    }

    // ---------- Escenario D: contrato invalido y mantenimiento ----------

    private static void escenarioD(Universo universo) throws NaveException {
        titulo("ESCENARIO D - Contrato invalido y mantenimiento");
        universo.seleccionarNave("CAR-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave() + " (quedo sin energia en el escenario B)");
        mostrarEstado(nave);
        int eventosAntes = nave.consultarBitacora().size();

        System.out.println("Cargas validas:");
        nave.cargarEnergia(60);
        verificador.verificarIgual("cargarEnergia(60) sobre 5", 65, nave.energiaDisponible());
        nave.cargarCombustible(40);
        verificador.verificarIgual("cargarCombustible(40) sobre 56", 96, nave.combustibleDisponible());

        System.out.println("Cargas que exceden la capacidad maxima:");
        try {
            nave.cargarCombustible(20);
            verificador.faltoRechazo("cargarCombustible(20) con 96");
        } catch (LimiteRecursoExcedidoException error) {
            verificador.rechazoEsperado("cargarCombustible(20) con 96", error);
        }
        verificador.verificarRecursos("Recursos sin cambios tras el rechazo", 96, 65, 44,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());

        try {
            nave.cargarEnergia(36);
            verificador.faltoRechazo("cargarEnergia(36) con 65");
        } catch (LimiteRecursoExcedidoException error) {
            verificador.rechazoEsperado("cargarEnergia(36) con 65", error);
        }
        verificador.verificarRecursos("Recursos sin cambios tras el rechazo", 96, 65, 44,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        verificador.verificar("La bitácora registra los rechazos de carga",
            indiceDesde(nave.consultarBitacora(), eventosAntes, "no se pudo cargar") >= 0);

        System.out.println("Mantenimiento:");
        nave.realizarMantenimiento();
        verificador.verificarRecursos("Mantenimiento: desgaste a 0 sin tocar combustible ni energía", 96, 65, 0,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        verificador.verificar("Ya no requiere mantenimiento", !nave.requiereMantenimiento());

        // Umbral de mantenimiento de la ficha: desde 80 de desgaste.
        verificador.verificar("Desgaste 80 requiere mantenimiento", new Recursos(50, 50, 80).requiereMantenimiento());
        verificador.verificar("Desgaste 79 no requiere mantenimiento", !new Recursos(50, 50, 79).requiereMantenimiento());
        mostrarBitacoraDesde(nave, eventosAntes);
    }

    // ---------- Escenario E: contratos ----------

    private static void escenarioContratos(Universo universo) throws NaveException {
        titulo("ESCENARIO E - Contratos: precondiciones (assert) y rechazos de dominio");
        Nave prueba = NaveFactory.createNave("PRB-1", "exploradora");
        AsistenteDeComando nave = new AsistenteDeComandoBasico(prueba, new Bitacora());
        Tablero tablero = crearTableroVacio();

        // Precondicion incumplida: es obligacion del invocante, se detecta con assert.
        // Atrapar AssertionError solo se hace aca, para demostrar que el contrato se comprueba;
        // el modelo nunca lo atrapa.
        System.out.println("Precondiciones incumplidas (se detectan con assert):");
        try {
            nave.cargarCombustible(0);
            verificador.faltoRechazo("cargarCombustible(0)");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("cargarCombustible(0)", error);
        }
        try {
            nave.cargarEnergia(-5);
            verificador.faltoRechazo("cargarEnergia(-5)");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("cargarEnergia(-5)", error);
        }
        verificador.verificarRecursos("Recursos intactos tras las precondiciones incumplidas", 60, 80, 0,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        try {
            nave.asignarTripulante(null);
            verificador.faltoRechazo("asignarTripulante(null)");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("asignarTripulante(null)", error);
        }
        try {
            new Tripulante("X-1", "Ana", "Paz", new Alferez(), Origen.TERRICOLA, -1);
            verificador.faltoRechazo("Tripulante con antigüedad negativa");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("Tripulante con antigüedad negativa", error);
        }
        try {
            NaveFactory.createNave("X-2", "crucero");
            verificador.faltoRechazo("NaveFactory con tipo desconocido");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("NaveFactory con tipo desconocido", error);
        }
        int eventosAntes = nave.consultarBitacora().size();
        try {
            nave.registrarEvento("MISION", "   ");
            verificador.faltoRechazo("Evento de bitácora vacío");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("Evento de bitácora vacío", error);
        }
        verificador.verificarIgual("La bitácora no aceptó el evento vacío", eventosAntes, nave.consultarBitacora().size());
        AsistenteDeComando otro = universo.obtener("COM-1");
        try {
            nave.ejecutarMision(new MisionRecoleccion(otro, tablero, "Punto X", "muestra"));
            verificador.faltoRechazo("Misión encomendada a otro asistente");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("Misión encomendada a otro asistente", error);
        }

        // Rechazo de dominio: situacion que el programa debe gestionar, se informa con excepcion.
        System.out.println("Rechazos de dominio (excepciones que el programa gestiona):");
        verificador.verificar("Sin tripulantes, la tripulación no está lista", !nave.tripulacionLista());
        try {
            nave.ejecutarMision(new MisionRecoleccion(nave, tablero, "Punto X", "muestra"));
            verificador.faltoRechazo("Misión con la tripulación incompleta");
        } catch (MisionNoViableException error) {
            verificador.rechazoEsperado("Misión con la tripulación incompleta", error);
        }
        verificador.verificarRecursos("Recursos intactos tras el rechazo", 60, 80, 0,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());

        asignarTripulacion(nave, "PRB-1");
        verificador.verificar("Con capitán y 4 tripulantes, la tripulación está lista", nave.tripulacionLista());
        try {
            nave.asignarTripulante(new Tripulante("PRB-1-T1", "Eva", "Luna", new Teniente(), Origen.MARCIANO, 2));
            verificador.faltoRechazo("Tripulante con id repetido");
        } catch (TripulacionInvalidaException error) {
            verificador.rechazoEsperado("Tripulante con id repetido", error);
        }
        verificador.verificarIgual("La tripulación no cambió", 5, nave.cantidadTripulantes());

        nave.prepararSalto();
        try {
            nave.ejecutarMision(new MisionRecoleccion(nave, tablero, "Punto X", "muestra"));
            verificador.faltoRechazo("Misión con el Motor Warp ocupado");
        } catch (MisionNoViableException error) {
            verificador.rechazoEsperado("Misión con el Motor Warp ocupado", error);
        }
        verificador.verificarRecursos("Recursos intactos tras el rechazo", 60, 80, 0,
            nave.combustibleDisponible(), nave.energiaDisponible(), nave.desgasteActual());
        nave.iniciarSalto();
        nave.finalizarSalto();
        nave.completarEnfriamiento();

        Mision unaVez = new MisionRecoleccion(nave, tablero, "Punto Y", "muestra");
        nave.ejecutarMision(unaVez);
        try {
            nave.ejecutarMision(unaVez);
            verificador.faltoRechazo("Ejecutar dos veces la misma misión");
        } catch (MisionNoViableException error) {
            verificador.rechazoEsperado("Ejecutar dos veces la misma misión", error);
        }

        try {
            universo.registrar(new AsistenteDeComandoBasico(NaveFactory.createNave("EXP-1", "combate"), new Bitacora()));
            verificador.faltoRechazo("Registrar en el universo una nave con id repetido");
        } catch (NaveYaRegistradaException error) {
            verificador.rechazoEsperado("Registrar en el universo una nave con id repetido", error);
            verificador.verificarIgual("La excepción guarda el id repetido", "EXP-1", error.getIdNave());
        }
        verificador.verificarIgual("El universo no cambió", 3, universo.cantidadNaves());

        try {
            universo.obtener("ZZZ-9");
            verificador.faltoRechazo("Pedir al universo una nave que no existe");
        } catch (NaveNoRegistradaException error) {
            verificador.rechazoEsperado("Pedir al universo una nave que no existe", error);
            verificador.verificarIgual("La excepción guarda el id buscado", "ZZZ-9", error.getIdNave());
        }
    }

    // ---------- Haberes: 4 cargos, 3 origenes, decoradores combinados ----------

    private static void escenarioHaberes(Universo universo) throws NaveException {
        titulo("HABERES - Liquidacion de octubre 2026");
        universo.seleccionarNave("COM-1");
        AsistenteDeComando nave = universo.getNaveActiva();
        System.out.println("Nave seleccionada: " + nave.idNave());
        YearMonth periodo = YearMonth.of(2026, 10);

        // Un tripulante por cargo, cubriendo los tres origenes.
        Tripulante capitan = new Tripulante("COM-1-H1", "Ines", "Garcia", new Capitan(), Origen.TERRICOLA, 10);
        Tripulante consejero = new Tripulante("COM-1-H2", "Tuvok", "Vulc", new Consejero(), Origen.VULCANO, 5);
        Tripulante teniente = new Tripulante("COM-1-H3", "Rex", "Olimpo", new Teniente(), Origen.MARCIANO, 3);
        Tripulante alferez = new Tripulante("COM-1-H4", "Luz", "Perez", new Alferez(), Origen.TERRICOLA, 1);

        // El consejero registra 3 consejos en octubre y 1 en septiembre (no entra en el periodo).
        consejero.registrarConsejo(LocalDate.of(2026, 10, 2), "Evitar la nebulosa K-7");
        consejero.registrarConsejo(LocalDate.of(2026, 10, 9), "Reducir velocidad ante tormentas");
        consejero.registrarConsejo(LocalDate.of(2026, 10, 20), "Hacer mantenimiento antes del salto");
        consejero.registrarConsejo(LocalDate.of(2026, 9, 28), "Recalcular la ruta de retorno");

        Tripulante[] tripulantes = {capitan, consejero, teniente, alferez};
        // Importes esperados por concepto: base, antiguedad, origen y (solo consejero) consejos.
        double[][] esperados = {
            {1000, 2000, 20},      // capitan: 20 % de 1000 x 10 anios, terricola
            {600, 150, 30, 6},     // consejero: 5 % de 600 x 5 anios, vulcano, 3 consejos x 2 PG
            {400, 36, 18},         // teniente: 3 % de 400 x 3 anios, marciano
            {200, 1, 20},          // alferez: 0,5 % de 200 x 1 anio, terricola
        };
        double[] totales = {3020, 786, 454, 221};

        for (int t = 0; t < tripulantes.length; t++) {
            nave.asignarTripulante(tripulantes[t]);
            Haber haber = nave.liquidarHaberes(periodo, tripulantes[t]);
            mostrarHaber(tripulantes[t], haber);

            String quien = tripulantes[t].getCargo().getNombre();
            verificador.verificarIgual(quien + ": cantidad de conceptos (decoradores)", esperados[t].length,
                haber.cantidadConceptos());
            for (int c = 0; c < esperados[t].length && c < haber.cantidadConceptos(); c++) {
                verificador.verificarImporte(quien + ": " + haber.consultarNombreConcepto(c),
                    esperados[t][c], haber.consultarImporteConcepto(c));
            }
            verificador.verificarImporte(quien + ": total", totales[t], haber.calcularTotal());
        }
        verificador.verificarIgual("Consejos del período (el de septiembre no cuenta)", 3, consejero.cantidadConsejos(periodo));

        Haber haberCapitan = nave.liquidarHaberes(periodo, capitan);
        try {
            haberCapitan.consultarImporteConcepto(99);
            verificador.faltoRechazo("Consultar un concepto inexistente del haber");
        } catch (AssertionError error) {
            verificador.rechazoEsperado("Consultar un concepto inexistente del haber", error);
        }
    }

    // ---------- Armado del universo ----------

    // Tablero sin elementos: asi los escenarios de la parte 1 dan siempre el mismo resultado
    private static Tablero crearTableroVacio() {
        try {
            return new Tablero(10, 10, new ArrayList<>());
        } catch (DimensionesDeTableroInconsistentesException e) {
            throw new IllegalStateException(e);
        } catch (CantidadDeElementosSuperaCasillaException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void registrarNave(Universo universo, String id, String tipo) throws NaveException {
        Nave nave = NaveFactory.createNave(id, tipo);
        AsistenteDeComando asistente = new AsistenteDeComandoBasico(nave, new Bitacora());
        asignarTripulacion(asistente, id);
        universo.registrar(asistente);
    }

    // Capitan + 4 tripulantes, cubriendo los 4 cargos y los 3 origenes.
    private static void asignarTripulacion(AsistenteDeComando asistente, String idNave) throws TripulacionInvalidaException {
        asistente.asignarTripulante(new Tripulante(idNave + "-T1", "Ana", "Rojas", new Capitan(), Origen.TERRICOLA, 10));
        asistente.asignarTripulante(new Tripulante(idNave + "-T2", "Spok", "Sarek", new Consejero(), Origen.VULCANO, 5));
        asistente.asignarTripulante(new Tripulante(idNave + "-T3", "Leo", "Marte", new Teniente(), Origen.MARCIANO, 3));
        asistente.asignarTripulante(new Tripulante(idNave + "-T4", "Sol", "Diaz", new Alferez(), Origen.TERRICOLA, 1));
        asistente.asignarTripulante(new Tripulante(idNave + "-T5", "Tom", "Vala", new Alferez(), Origen.VULCANO, 0));
    }

    // ---------- Consultas sobre la bitacora ----------

    private static boolean estaEnOrdenTemporal(List<EventoBitacora> eventos) {
        for (int i = 1; i < eventos.size(); i++) {
            if (eventos.get(i).getTimestamp().isBefore(eventos.get(i - 1).getTimestamp())) {
                return false;
            }
        }
        return true;
    }

    private static int indiceDesde(List<EventoBitacora> eventos, int desde, String texto) {
        for (int i = desde; i < eventos.size(); i++) {
            if (eventos.get(i).getDescripcion().contains(texto)) {
                return i;
            }
        }
        return -1;
    }

    private static int contarCategoria(List<EventoBitacora> eventos, String categoria) {
        int cantidad = 0;
        for (EventoBitacora evento : eventos) {
            if (evento.getCategoria().equals(categoria)) {
                cantidad++;
            }
        }
        return cantidad;
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

    // Muestra solo los eventos registrados a partir de la posicion indicada.
    private static void mostrarBitacoraDesde(AsistenteDeComando nave, int desde) {
        List<EventoBitacora> eventos = nave.consultarBitacora();
        System.out.println("Bitacora:");
        for (int i = desde; i < eventos.size(); i++) {
            EventoBitacora evento = eventos.get(i);
            System.out.println("  [" + evento.getTimestamp() + "] " + evento.getCategoria() + " | " + evento.getDescripcion());
        }
    }

    private static void mostrarHaber(Tripulante tripulante, Haber haber) {
        System.out.println();
        System.out.println(tripulante.getNombre() + " " + tripulante.getApellido()
            + " - " + tripulante.getCargo().getNombre()
            + ", " + tripulante.getOrigen()
            + ", " + tripulante.getAntiguedadAnios() + " anios");
        for (int i = 0; i < haber.cantidadConceptos(); i++) {
            System.out.println(String.format("  %-35s %8.2f PG",
                haber.consultarNombreConcepto(i), haber.consultarImporteConcepto(i)));
        }
        System.out.println(String.format("  %-35s %8.2f PG", "TOTAL", haber.calcularTotal()));
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    /**
     * Ejecuta todos los escenarios y termina con codigo 0 si todo coincide con
     * lo esperado, 1 si alguna comprobacion falla y 2 si faltan las aserciones.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        if (!Verificador.asercionesActivas()) {
            System.out.println("ERROR: las aserciones estan desactivadas.");
            System.out.println("Las precondiciones del modelo se comprueban con assert: ejecutar con");
            System.out.println("  mvn compile exec:exec      o      java -ea -cp target/classes ar.edu.unmdp.tpg.test.PruebaEscenarios");
            System.exit(2);
        }
        Mision.setPausaEntreTurnos(0);

        Universo universo = null;
        try {
            universo = crearUniverso();
        } catch (NaveException error) {
            verificador.escenarioInterrumpido("Creación del universo", error);
        } catch (AssertionError error) {
            verificador.escenarioInterrumpido("Creación del universo", error);
        }

        if (universo != null) {
            // Cada escenario corre aislado: si se interrumpe por un error que no esperaba,
            // se registra como falla y se sigue con el siguiente.
            try {
                escenarioA(universo);
            } catch (NaveException error) {
                verificador.escenarioInterrumpido("Escenario A", error);
            } catch (AssertionError error) {
                verificador.escenarioInterrumpido("Escenario A", error);
            }
            try {
                escenarioB(universo);
            } catch (NaveException error) {
                verificador.escenarioInterrumpido("Escenario B", error);
            } catch (AssertionError error) {
                verificador.escenarioInterrumpido("Escenario B", error);
            }
            try {
                escenarioC(universo);
            } catch (NaveException error) {
                verificador.escenarioInterrumpido("Escenario C", error);
            } catch (AssertionError error) {
                verificador.escenarioInterrumpido("Escenario C", error);
            }
            try {
                escenarioD(universo);
            } catch (NaveException error) {
                verificador.escenarioInterrumpido("Escenario D", error);
            } catch (AssertionError error) {
                verificador.escenarioInterrumpido("Escenario D", error);
            }
            try {
                escenarioContratos(universo);
            } catch (NaveException error) {
                verificador.escenarioInterrumpido("Escenario E", error);
            } catch (AssertionError error) {
                verificador.escenarioInterrumpido("Escenario E", error);
            }
            try {
                escenarioHaberes(universo);
            } catch (NaveException error) {
                verificador.escenarioInterrumpido("Haberes", error);
            } catch (AssertionError error) {
                verificador.escenarioInterrumpido("Haberes", error);
            }
        }

        verificador.imprimirResumen();
        if (verificador.fallos() == 0) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
